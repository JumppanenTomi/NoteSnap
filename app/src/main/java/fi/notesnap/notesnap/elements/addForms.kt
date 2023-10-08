package fi.notesnap.notesnap.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddFolderForm() {
    var text by remember { mutableStateOf("") }
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.Start
    ) {
        Text("Add new folder", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            label = { Text(text = "Folder name") },
            onValueChange = { newText ->
                text = newText
            }
        )
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.End) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun AddNoteForm(titleFromCamera: String?, contentFromCamera: String?) {
    var title by remember { mutableStateOf(titleFromCamera) }
    var content by remember { mutableStateOf(contentFromCamera) }
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        Arrangement.Center,
        Alignment.Start
    ) {
        Text("Add new note", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            maxLines = 1,
            value = "",
            textStyle = TextStyle(
                fontSize = 30.sp
            ),
            label = { Text(text = "Note title") },
            onValueChange = { newText ->
                title = newText
            }
        )
        Spacer(Modifier.height(24.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            value = "",
            onValueChange = { newText ->
                content = newText
            },
            label = { Text("Note") }
        )
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.End) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Save")
            }
        }
    }
}