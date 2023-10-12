package fi.notesnap.notesnap.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun NoteDetailsView(note: Note, viewModel: NoteViewModelV2, toggleNoteDetails: (Boolean) -> Unit) {
    val id by remember { mutableLongStateOf(note.id) }
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var locked by remember { mutableStateOf(note.locked) } // Changed to Boolean
    var folderId by remember { mutableStateOf(note.folderId) }

    var isDialogVisible = remember { mutableStateOf(false) }
    val listOfFolders by viewModel.getAllFolders().observeAsState(initial = emptyList())


    var currentFolder by remember { mutableStateOf("Choose Folder") }

    var deleteEvent by remember {
        mutableStateOf(false)
    }
//dialog to choose a folder (hidden)
    if (isDialogVisible.value) {
        Dialog(
            onDismissRequest = { isDialogVisible.value = false },
            content = {
                LazyColumn {
                    items(listOfFolders) { folder ->
                        Row(
                            Modifier
                                .background(Color.White)
                                .padding(16.dp)
                                .fillMaxWidth()
                                .clickable {
                                    folderId = folder.id
                                    isDialogVisible.value = false
                                    currentFolder = if (folderId != null) {
                                        folder.name
                                    } else {
                                        "Choose Folder"
                                    }

                                }) {
                            Text(
                                text = folder.name,
                                style = TextStyle(fontWeight = FontWeight.Normal),
                                fontSize = 26.sp,
                            )
                        }
                        Divider(
                            modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 1.dp
                        )
                    }
                }

            }
        )
    }

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize() // Fill the entire screen
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top, // Vertically align content at the top
        horizontalAlignment = Alignment.Start
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save Icon
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save Note",
                        modifier = Modifier
                            .clickable {
                                if (title.isNotBlank()) {
                                    val updatedNote = note.copy(
                                        title = title,
                                        content = content,
                                        locked = locked,
                                        folderId = folderId,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    viewModel.updateNote(updatedNote)
                                }
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Delete Icon
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        modifier = Modifier
                            .clickable {
                                deleteEvent = true
                            }
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Folder Icon",
                    modifier = Modifier
                        .clickable { isDialogVisible.value = true }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = if (locked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Lock/Unlock Note",
                    modifier = Modifier
                        .clickable { locked = !locked }
                        .padding(8.dp),
                    tint = if (locked) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Note Icon",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))

            Text(
                text = "Edit your note",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = if (currentFolder != "Choose Folder") "#${currentFolder}" else "",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Gray
            )
        }
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxSize()
            )
            if (title == "") {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxSize()
            )
            if (content == "") {
                Text(
                    text = "Content",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }


        if (deleteEvent) {
            AlertDialog(
                icon = {
                    Icon(Icons.Default.Delete, contentDescription = "Delete icon")
                },
                title = {
                    Text(text = "Are you sure?")
                },
                text = {
                    Text(text = "Are you sure that you want to delete current note")
                },
                onDismissRequest = {
                    deleteEvent = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteNote(id)
                            deleteEvent = false
                            toggleNoteDetails(false)
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            deleteEvent = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}
