package fi.notesnap.notesnap.elements.note

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.data.viewmodels.MainViewModel
import fi.notesnap.notesnap.elements.shared.Search
import fi.notesnap.notesnap.utilities.BiometricUnlockNote

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteList(
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

    val biometricUnlockNote =
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
            NoteEdit(
                selectedNote!!, folders, viewModel, ::closeNoteDetails
            )
        }
    }
}


enum class LayoutMode(val label: String) {
    Small("Small"), Big("Big"), Card("Card")
}
