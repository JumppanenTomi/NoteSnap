package fi.notesnap.notesnap.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fi.notesnap.notesnap.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderView(viewModel: NoteViewModel) {
    var folderName by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = folderName,
            onValueChange = { folderName = it },
            label = { Text("Folder Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.createFolder(folderName)
        }) {
            Text("Create Folder")
        }
    }
}
