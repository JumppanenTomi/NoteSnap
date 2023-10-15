package fi.notesnap.notesnap.elements.note

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.data.viewmodels.LayoutMode
import fi.notesnap.notesnap.data.viewmodels.MainViewModel
import fi.notesnap.notesnap.data.viewmodels.NoteViewModel
import fi.notesnap.notesnap.data.viewmodels.SortField
import fi.notesnap.notesnap.data.viewmodels.SortOrder
import fi.notesnap.notesnap.elements.note.menu.NoteListMenu
import fi.notesnap.notesnap.elements.shared.Search
import fi.notesnap.notesnap.utilities.BiometricUnlockNote

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NoteList(
    viewModel: MainViewModel,
    context: Context,
    fragmentActivity: FragmentActivity,
    noteViewModel: NoteViewModel,
    folderId = Folder?
) {
    val layoutMode by noteViewModel.layoutMode.observeAsState(LayoutMode.Small)
    val folder by noteViewModel.folder.observeAsState()

    val folders by viewModel.getAllFolders().observeAsState(listOf())
    val notes = noteViewModel.getAllNotes(folder).observeAsState(listOf())
    val sortField by noteViewModel.sortField.observeAsState()
    val sortOrder by noteViewModel.sortOrder.observeAsState()

    var showNoteDetails by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var showMenu by remember { mutableStateOf(false) }

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
        else -> 1
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
            contentDescription = "Menu Options",
            modifier = Modifier
                .clickable { showMenu = true }
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
            val sortedNotes = when (sortField) {
                SortField.Title -> notes.value.sortedWith { note1, note2 ->
                    if (sortOrder == SortOrder.ASC) {
                        note1.title.compareTo(note2.title)
                    } else {
                        note2.title.compareTo(note1.title)
                    }
                }

                SortField.Content -> notes.value.sortedWith { note1, note2 ->
                    if (sortOrder == SortOrder.ASC) {
                        note1.content.compareTo(note2.content)
                    } else {
                        note2.content.compareTo(note1.content)
                    }
                }

                SortField.Time -> notes.value.sortedWith { note1, note2 ->
                    if (sortOrder == SortOrder.ASC) {
                        note1.updatedAt.compareTo(note2.updatedAt)
                    } else {
                        note2.updatedAt.compareTo(note1.updatedAt)
                    }
                }

                else -> notes.value
            }

            items(sortedNotes) { note ->
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

                    else -> {}
                }
            }
        }

    }

    fun closeMenu() { showMenu = false }
    if (showMenu) {
        NoteListMenu(noteViewModel = noteViewModel, ::closeMenu)
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