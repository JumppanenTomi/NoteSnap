package fi.notesnap.notesnap.elements.folder

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import fi.notesnap.notesnap.data.viewmodels.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FolderNoteScreen(
    viewModel: MainViewModel, folderId: Long, context: Context, fragmentActivity: FragmentActivity
) {
    /*
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

    val biometricUnlockNote = BiometricUnlockNote(context, fragmentActivity, ::setShowNote)

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
            fun closeNoteDetails() {
                showNoteDetails = false
            }

            NoteEdit(
                note = selectedNote!!, folders = folders, viewModel = viewModel, ::closeNoteDetails
            )
        }
    }

     */
}