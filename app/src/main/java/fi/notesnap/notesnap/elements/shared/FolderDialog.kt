package fi.notesnap.notesnap.elements.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fi.notesnap.notesnap.data.viewmodels.NoteViewModel

@Composable
fun FolderDialog(noteViewModel: NoteViewModel) {
    val folders by noteViewModel.getAllFolders().observeAsState(listOf())

    Dialog(onDismissRequest = { noteViewModel.closeFolderDialog() }) {
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
                    //.fillMaxHeight(0.4f)
                ) {
                    items(folders) { folder ->
                        Column {
                            ListItem(headlineContent = { Text(folder.name) }, leadingContent = {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = "Localized description",
                                )
                            }, modifier = Modifier.clickable {
                                noteViewModel.setFolder(folder)
                                noteViewModel.closeFolderDialog()
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
                            noteViewModel.setFolder(null)
                            noteViewModel.closeFolderDialog()
                        },
                    ) {
                        Text(text = "Reset")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        noteViewModel.closeFolderDialog()
                    }) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }
}