package fi.notesnap.notesnap.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import fi.notesnap.notesnap.daos.FolderDao
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.data.state.FolderState
import fi.notesnap.notesnap.elements.ListNotes
import fi.notesnap.notesnap.elements.Search
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteScreen(navController: NavController, viewModel: NoteViewModelV2, context: Context, fragmentActivity: FragmentActivity) {
    // State to track the current layout mode (small, big, card)
    var layoutMode by remember { mutableStateOf(LayoutMode.Small) }
    // Observe notes from the view model
    val notes = viewModel.getAllNotes().observeAsState(listOf())
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

    var biometricUnlockNote = BiometricUnlockNote(context, fragmentActivity, ::toggleShowNoteDetails)

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
                    LayoutMode.Small -> SmallNoteItem(note) {
                        selectedNote = note
                        if(selectedNote!!.locked){
                            showNoteDetails = false
                            biometricUnlockNote.authenticate()
                        }else{
                        showNoteDetails = true
                        }
                    }

                    LayoutMode.Big -> BigNoteItem(note) {
                        selectedNote = note
                        if(selectedNote!!.locked){
                            showNoteDetails = false
                            biometricUnlockNote.authenticate()
                        }else{
                            showNoteDetails = true
                        }                    }

                    LayoutMode.Card -> CardNoteItem(note) {
                        selectedNote = note
                        if(selectedNote!!.locked){
                            showNoteDetails = false
                            biometricUnlockNote.authenticate()
                        }else{
                            showNoteDetails = true
                        }                    }
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
                    text = "Choose Layout Mode", style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LayoutOptionButton(
                        layoutMode = LayoutMode.Small, currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Small
                        showLayoutOptions = false
                    }

                    LayoutOptionButton(
                        layoutMode = LayoutMode.Big, currentLayoutMode = layoutMode
                    ) {
                        layoutMode = LayoutMode.Big
                        showLayoutOptions = false
                    }

                    LayoutOptionButton(
                        layoutMode = LayoutMode.Card, currentLayoutMode = layoutMode
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

            NoteDetailsView(
                selectedNote!!,
                viewModel = viewModel,
                toggleNoteDetails = ::toggleShowNoteDetails
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FolderNoteScreen(navController: NavController, viewModel: NoteViewModelV2, folderId: Long) {
    // State to track the current layout mode (small, big, card)
    var layoutMode by remember { mutableStateOf(LayoutMode.Small) }
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
                    LayoutMode.Small -> SmallNoteItem(note) {
                        if (selectedNote!!.locked){}
                        selectedNote = note
                        showNoteDetails = true
                    }

                    LayoutMode.Big -> BigNoteItem(note) {
                        selectedNote = note
                        showNoteDetails = true
                    }

                    LayoutMode.Card -> CardNoteItem(note) {
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

            NoteDetailsView(
                selectedNote!!,
                viewModel = viewModel,
                toggleNoteDetails = ::toggleShowNoteDetails
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
            .height(100.dp)
            .width(100.dp)
            .padding(8.dp)
            .clickable { onLayoutSelected() }, shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = layoutMode.label,
                tint = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = layoutMode.label,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.Gray,
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

    val formatter = DateTimeFormatter.ofPattern("HH:mm") // Format for time

    // Check if it's the same date
    return if (currentZonedDateTime.toLocalDate() == updatedAtZonedDateTime.toLocalDate()) {
        updatedAtZonedDateTime.format(formatter) // Format for time
    } else {
        updatedAtZonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // Format for date
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListNoteItem(note: Note, layoutMode: LayoutMode, onClick: () -> Unit) {
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
                    text = note.content,
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
                                modifier = Modifier
                                    .size(12.dp)
                            )
                            Spacer(Modifier.width(8.dp))

                        }


                        // Display folder name (You can style this as needed)
                        Text(
                            text = "#${(note.folderId ?: "Undefined")}", // Replace with your folder retrieval logic
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
fun CardNoteItem(note: Note, onClick: () -> Unit) {
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
                text = note.content,
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
                            modifier = Modifier
                                .size(12.dp)
                        )
                        Spacer (Modifier.width(8.dp))
                    }
                    // Display folder name (You can style this as needed)
                    Text(
                        text = "#${(note.folderId ?: "Undefined")}", // Replace with your folder retrieval logic
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


@Composable
fun NoteListingView(
    state: FolderState,
    navController: NavController,
    folderDao: FolderDao,
) {
    val folderCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(folderCount) {
        val count = checkFolderCount(folderDao)
        if (count == 0) {
            createFolder(folderDao)
        }
    }

    ListNotes(children = state.notes, navController = navController)
}

// this is temporary
fun createFolder(dao: FolderDao) {
    val folder = Folder(0, "Main")
    val myCoroutineScope = CoroutineScope(Dispatchers.Default)
    myCoroutineScope.launch {
        dao.insertFolder(folder)
        println("Folder created")
    }
}

suspend fun checkFolderCount(dao: FolderDao): Int {
    return withContext(Dispatchers.Default) {
        val count = dao.getFolderCount()
        println("folder count $count")
        count
    }
}