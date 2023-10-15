package fi.notesnap.notesnap.elements.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note

/**
 * This fucntion returns composable search field that is capable to show and filter either Note list or Folder list
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    folderList: List<Folder>?,
    noteList: List<Note>?,
    setNote: ((Note) -> Unit)?,
    setVisibility: ((Boolean) -> Unit)?
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { traversalIndex = -1f },
        query = text,
        onQueryChange = { text = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = { Text("Hinted search text") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close search",
                    modifier = Modifier.clickable { active = false; text = "" })
            }
        }
    ) {
        if (folderList !== null) {
            folderList.forEach { folder ->
                if (folder.name.lowercase().contains(text.lowercase()) || text == "") {
                    ListItem(
                        headlineContent = { Text(folder.name) },
                        supportingContent = { Text("Additional info of folder $folder") },
                        modifier = Modifier
                            .clickable {
                                if (setVisibility != null) {
                                    setVisibility(true)
                                }
                                active = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
        noteList?.forEach { note ->
            if (note.title.lowercase().contains(text.lowercase()) || note.content.lowercase()
                    .contains(text.lowercase()) || text == ""
            ) {
                ListItem(
                    headlineContent = { Text(note.title) },
                    supportingContent = { Text(note.content) },
                    modifier = Modifier
                        .clickable {
                            if (setNote != null) {
                                setNote(note)
                            }
                            if (setVisibility != null) {
                                setVisibility(true)
                            }
                            active = false
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}