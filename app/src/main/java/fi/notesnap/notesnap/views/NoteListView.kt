package fi.notesnap.notesnap.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import fi.notesnap.notesnap.MainViewModel
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.elements.Search
import fi.notesnap.notesnap.utilities.BiometricUnlockNote
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteListView(
    navController: NavController,
    viewModel: MainViewModel,
    context: Context,
    fragmentActivity: FragmentActivity
) {
    // State to track the current layout mode (small, big, card)
    var layoutMode by remember { mutableStateOf(LayoutMode.Small) }
    // Observe notes from the view model
    val folders by viewModel.getAllFolders().observeAsState(listOf())
    var selectedFolder by remember { mutableStateOf<Folder?>(null) }

    val notes = viewModel.getAllNotes(selectedFolder).observeAsState(listOf())
    var showNoteDetails by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showLayoutOptions by remember { mutableStateOf(false) }
    var showFoldersDialog by remember { mutableStateOf(false) }

    if (showFoldersDialog) {
        Dialog(onDismissRequest = { showFoldersDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Folder",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                    ) {
                        items(folders) { folder ->
                            Column {
                                ListItem(headlineContent = { Text(folder.name) }, leadingContent = {
                                    Icon(
                                        Icons.Default.Folder,
                                        contentDescription = "Localized description",
                                    )
                                }, modifier = Modifier.clickable {
                                    selectedFolder = folder
                                    showFoldersDialog = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        OutlinedButton(
                            onClick = {
                                selectedFolder = null
                                showFoldersDialog = false
                            },
                        ) {
                            Text(text = "Reset")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            showFoldersDialog = false
                        }) {
                            Text(text = "Close")
                        }
                    }

                }
            }
        }
    }

    fun setSelectedNote(note: Note) {
        selectedNote = note
    }

    fun setShowNote(boolean: Boolean) {
        showNoteDetails = boolean
    }

    fun toggleShowNoteDetails(boolean: Boolean) {
        showNoteDetails = boolean
    }

    var biometricUnlockNote =
        BiometricUnlockNote(context, fragmentActivity, ::toggleShowNoteDetails)

    // Define the number of columns for the grid layout
    val columns = when (layoutMode) {
        LayoutMode.Small -> 1
        LayoutMode.Big -> 1
        LayoutMode.Card -> 2
    }

    Column {
        if (notes.value.isNotEmpty()) {
            Search(
                folderList = null,
                noteList = notes.value,
                setNote = ::setSelectedNote,
                setVisibility = ::setShowNote
            )
        }
        Icon(Icons.Default.MoreVert,
            contentDescription = "More Options",
            modifier = Modifier
                .clickable { showLayoutOptions = true }
                .align(Alignment.End)
                .padding(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = if (layoutMode == LayoutMode.Card) PaddingValues(8.dp) else PaddingValues(
                0.dp
            ),
            verticalArrangement = if (layoutMode == LayoutMode.Card) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(
                0.dp
            ),
            horizontalArrangement = if (layoutMode == LayoutMode.Card) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(
                0.dp
            )
        ) {
            items(notes.value) { note ->
                when (layoutMode) {
                    LayoutMode.Small, LayoutMode.Big -> ListNoteItem(note, layoutMode, folders) {
                        selectedNote = note
                        if (selectedNote!!.locked) {
                            showNoteDetails = false
                            biometricUnlockNote.authenticate()
                        } else {
                            showNoteDetails = true
                        }
                    }

                    LayoutMode.Card -> CardNoteItem(note, folders) {
                        selectedNote = note
                        if (selectedNote!!.locked) {
                            showNoteDetails = false
                            biometricUnlockNote.authenticate()
                        } else {
                            showNoteDetails = true
                        }
                    }
                }
            }
        }

    }

    if (showLayoutOptions) {
        ModalBottomSheet(
            onDismissRequest = { showLayoutOptions = false },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .height(60.dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.Start,
                    Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FilterList, "Change layout")
                    Text(
                        "Choose layout mode", Modifier.padding(horizontal = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LayoutOptionButton(
                        layoutMode = LayoutMode.Small, currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Small
                    }

                    LayoutOptionButton(
                        layoutMode = LayoutMode.Big, currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Big
                    }

                    LayoutOptionButton(
                        layoutMode = LayoutMode.Card, currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Card
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider()

                Row(
                    Modifier
                        .height(60.dp)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            showFoldersDialog = true
                        }, Arrangement.Start, Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Folder, "Group by folder")
                    Text(
                        buildAnnotatedString {
                            if (selectedFolder != null) {
                                append(" Group by folder ")
                            } else {
                                append(" Group by folder")
                            }
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                if (selectedFolder != null) {
                                    append("#${selectedFolder!!.name}")
                                }
                            }
                        }, modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }


    }

    fun closeNoteDetails() {
        showNoteDetails = false
    }

    if (showNoteDetails && selectedNote != null) {
        ModalBottomSheet(
            onDismissRequest = { closeNoteDetails() },
            modifier = Modifier.fillMaxSize(),
        ) {
            NoteEditView(
                selectedNote!!, folders, viewModel, ::closeNoteDetails
            )
        }
    }
}

@Composable
fun LayoutOptionButton(
    layoutMode: LayoutMode, currentLayoutMode: LayoutMode, onLayoutSelected: () -> Unit
) {
    val isSelected = layoutMode == currentLayoutMode

    Card(
        modifier = Modifier
            .size(72.dp)
            .padding(8.dp)
            .clickable { onLayoutSelected() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (layoutMode) {
                    LayoutMode.Small -> Icons.Default.List
                    LayoutMode.Big -> Icons.Default.ViewList
                    LayoutMode.Card -> Icons.Default.GridView
                },
                contentDescription = layoutMode.label,
                tint = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


enum class LayoutMode(val label: String) {
    Small("Small"), Big("Big"), Card("Card")
}

fun formatUpdatedAt(updatedAt: Long): String {
    val currentInstant = Instant.ofEpochMilli(System.currentTimeMillis())
    val updatedAtInstant = Instant.ofEpochMilli(updatedAt)

    val currentZonedDateTime = ZonedDateTime.ofInstant(currentInstant, ZoneId.systemDefault())
    val updatedAtZonedDateTime = ZonedDateTime.ofInstant(updatedAtInstant, ZoneId.systemDefault())

    val today = LocalDate.now()

    val period = Period.between(updatedAtZonedDateTime.toLocalDate(), today)

    return when {
        period.days == 0 -> {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            updatedAtZonedDateTime.format(formatter)
        }

        period.months == 0 -> "${period.days}d ago"
        period.years == 0 -> "${period.months}m ago"
        else -> "${period.years}y"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListNoteItem(note: Note, layoutMode: LayoutMode, folders: List<Folder>, onClick: () -> Unit) {
    val folder = folders.find { it.id == note.folderId }

    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {


                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (note.locked) "Unlock to see content" else note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = when (layoutMode) {
                        LayoutMode.Small -> 1
                        LayoutMode.Big -> 3
                        else -> 0
                    },
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (note.locked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(8.dp))

                        }


                        // Display folder name (You can style this as needed)
                        Text(
                            text = if (folder != null) "#${folder.name}" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = formatUpdatedAt(note.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray, // You can change the color
                    )
                }
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(), color = Color.LightGray, thickness = 1.dp
        )
    }
}


@Composable
fun CardNoteItem(note: Note, folders: List<Folder>, onClick: () -> Unit) {
    val folder = folders.find { it.id == note.folderId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(256.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (note.locked) "Unlock to see content" else note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.locked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    // Display folder name (You can style this as needed)
                    Text(
                        text = if (folder != null) "#${folder.name}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Text(
                    text = formatUpdatedAt(note.updatedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray, // You can change the color
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FolderNoteScreen(navController: NavController, viewModel: MainViewModel, folderId: Long) {
    // State to track the current layout mode (small, big, card)
    var layoutMode by remember { mutableStateOf(LayoutMode.Small) }
    val folders by viewModel.getAllFolders().observeAsState(listOf())
    // Observe notes from the view model
    val notes = viewModel.getByFolderId(folderId).observeAsState(listOf())
    var showNoteDetails by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showLayoutOptions by remember { mutableStateOf(false) }

    fun setSelectedNote(note: Note) {
        selectedNote = note
    }

    fun setShowNote(boolean: Boolean) {
        showNoteDetails = boolean
    }

    fun toggleShowNoteDetails(boolean: Boolean) {
        showNoteDetails = boolean
    }

    // Define the number of columns for the grid layout
    val columns = when (layoutMode) {
        LayoutMode.Small -> 1
        LayoutMode.Big -> 1
        LayoutMode.Card -> 2
    }

    Column {
        if (notes.value.isNotEmpty()) {
            Search(
                folderList = null,
                noteList = notes.value,
                setNote = ::setSelectedNote,
                setVisibility = ::setShowNote
            )
        }
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "More Options",
            modifier = Modifier
                .clickable { showLayoutOptions = true }
                .align(Alignment.End)
                .padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = if (layoutMode == LayoutMode.Card) PaddingValues(8.dp) else PaddingValues(
                0.dp
            ),
            verticalArrangement = if (layoutMode == LayoutMode.Card) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(
                0.dp
            ),
            horizontalArrangement = if (layoutMode == LayoutMode.Card) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(
                0.dp
            )
        ) {
            items(notes.value) { note ->
                when (layoutMode) {
                    LayoutMode.Small, LayoutMode.Big -> ListNoteItem(note, layoutMode, folders) {
                        selectedNote = note
                        showNoteDetails = true
                    }

                    LayoutMode.Card -> CardNoteItem(note, folders) {
                        selectedNote = note
                        showNoteDetails = true
                    }
                }
            }
        }

    }

    if (showLayoutOptions) {
        ModalBottomSheet(
            onDismissRequest = { showLayoutOptions = false },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Choose Layout Mode",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LayoutOptionButton(
                        layoutMode = LayoutMode.Small,
                        currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Small
                        showLayoutOptions = false
                    }

                    LayoutOptionButton(
                        layoutMode = LayoutMode.Big,
                        currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Big
                        showLayoutOptions = false
                    }

                    LayoutOptionButton(
                        layoutMode = LayoutMode.Card,
                        currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Card
                        showLayoutOptions = false
                    }
                }
            }
        }
    }



    if (showNoteDetails && selectedNote != null) {
        ModalBottomSheet(
            onDismissRequest = { showNoteDetails = false },
            modifier = Modifier.fillMaxSize(),
        ) {
            fun closeNoteDetails() {
                showNoteDetails = false
            }

            NoteEditView(note = selectedNote!!, folders = folders, viewModel = viewModel, ::closeNoteDetails)
        }
    }
}
