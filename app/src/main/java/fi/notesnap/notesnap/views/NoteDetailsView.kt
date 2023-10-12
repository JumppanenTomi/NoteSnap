package fi.notesnap.notesnap.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun NoteDetailsView(note: Note, viewModel: NoteViewModelV2, toggleNoteDetails: (Boolean) -> Unit) {
    val id = remember { mutableLongStateOf(note.id) }
    val title = remember { mutableStateOf(note.title) }
    val content = remember { mutableStateOf(note.content) }
    val locked = remember { mutableStateOf(note.locked) } // Changed to Boolean
    val folderId = remember { mutableStateOf(note.folderId) }

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
                                    folderId.value = folder.id
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
            .fillMaxSize(), // Fill the entire screen
        verticalArrangement = Arrangement.Top, // Vertically align content at the top
        horizontalAlignment = Alignment.Start
    ) {
        Text("Edit Note", style = MaterialTheme.typography.headlineSmall) // Adjusted text style
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.value.isNotBlank()) {
                    val updatedNote = note.copy(
                        title = title.value,
                        content = content.value,
                        locked = locked.value,
                        folderId = folderId.value,
                        updatedAt = System.currentTimeMillis()
                    )
                    viewModel.updateNote(updatedNote)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update")
        }

        Spacer(Modifier.height(16.dp))

        // Toggle button for locked/unlocked
        Icon(
            imageVector = if (locked.value) Icons.Default.Lock else Icons.Default.Lock,
            contentDescription = if (locked.value) "Locked" else "Unlocked",
            tint = if (locked.value) Color.Red else Color.Green, // Customize the icon color
            modifier = Modifier
                .size(24.dp)
                .clickable { locked.value = !locked.value }
        )
        // show the dialog
        Button(onClick = {
            isDialogVisible.value = true


        }) {
            Text(text = currentFolder)
        }

        Spacer(Modifier.height(8.dp))

        // Title TextField with no outline
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            textStyle = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            singleLine = true, // Allow only a single line for title
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = content.value,
            onValueChange = { content.value = it },
            textStyle = TextStyle(fontSize = 18.sp),
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth() // Content takes up most of the space
                .height(100.dp)
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { deleteEvent = true },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(text = "Delete")
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
                            viewModel.deleteNote(id.longValue)
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
