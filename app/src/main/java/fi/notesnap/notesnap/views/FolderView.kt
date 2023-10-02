package fi.notesnap.notesnap.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import fi.notesnap.notesnap.NoteViewModel
import fi.notesnap.notesnap.entities.Note

@Composable
fun FolderView(folderId: Long, viewModel: NoteViewModel) {
    val notesInFolder: List<Note> by viewModel.getNotesByFolder(folderId).collectAsState(initial = emptyList())

    var searchTerm by remember { mutableStateOf("") }

    Column {
        TextField(
            value = searchTerm,
            onValueChange = { searchTerm = it },
            label = { Text("Search Notes") }
        )

        LazyColumn {
            items(notesInFolder.filter { it.title.contains(searchTerm, true) }) { note ->
                NoteItem(note = note)
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Text(text = note.title)
}

@Preview
@Composable
fun PreviewFolderView() {
    // need mock ViewModel and folderId for previewing
}
