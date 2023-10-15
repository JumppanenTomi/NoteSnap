package fi.notesnap.notesnap.elements.folder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.viewmodels.FolderViewModel

@Composable
fun FolderItem(
    folder: Folder,
    viewModel: FolderViewModel,
    navController: NavController,
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingText by remember { mutableStateOf(folder.name) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("folderNotes/${folder.id}")
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            // Show TextField when editing
            TextField(
                value = editingText,
                onValueChange = { newEditingText -> editingText = newEditingText },
                keyboardActions = KeyboardActions(onDone = {
                    isEditing = false
                }),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
        } else {
            Text(text = folder.name)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Folder Name",
                modifier = Modifier.clickable(onClick = {
                    isEditing = !isEditing
                })
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Folder",
                modifier = Modifier.clickable(onClick = {
                    viewModel.deleteFolder(folder)
                })
            )
        }
    }
}