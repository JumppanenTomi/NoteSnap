package fi.notesnap.notesnap

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderComposable(viewModel: NoteViewModel) {
    var folderName by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        TextField(
            value = folderName,
            onValueChange = { folderName = it },
            label = { Text("Folder Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (folderName.isNotBlank()) {
                viewModel.createFolder(folderName)
                folderName = ""
            }
        }) {
            Text("Create Folder")
        }
    }
}

@Composable
fun FolderViewComposable(viewModel: NoteViewModel) {
    val folders by viewModel.getAllFolders().observeAsState(listOf())

    LazyColumn {
        items(folders) { folder ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        // Handle folder click
                    }
            ) {
                Text(
                    text = folder.name,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun NotesInFolderComposable(folderId: Long, viewModel: NoteViewModel) {
    val notes by viewModel.getNotesByFolder(folderId).observeAsState(listOf())

    LazyColumn {
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        // Handle note click
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = note.title, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = note.content)
                }
            }
        }
    }
}
