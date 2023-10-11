package fi.notesnap.notesnap.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.notesnap.notesnap.NoteEvent
import fi.notesnap.notesnap.NoteViewModel
import fi.notesnap.notesnap.machineLearning.translateString
import fi.notesnap.notesnap.utilities.languageCodeToNameMap
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2


// Modify the AddFolderForm to accept a callback function for adding folders
@Composable
fun AddFolderForm(onAddFolder: (String) -> Unit) {
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
            value = text,
            label = { Text(text = "Folder name") },
            onValueChange = { newText ->
                text = newText
            }
        )
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.End) {
            Button(onClick = {
                onAddFolder(text)
                text = "" // reset the text field after adding
            }) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun FolderItem(
    folderName: String,
    onEditCompleted: (newName: String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingText by remember { mutableStateOf(folderName) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            // Show TextField when editing
            TextField(
                value = editingText,
                onValueChange = { newEditingText -> editingText = newEditingText },
                keyboardActions = KeyboardActions(onDone = {
                    onEditCompleted(editingText)
                    isEditing = false
                }),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )
        } else {
            // Show folder name when not editing
            Text(text = folderName)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Folder Name",
                modifier = Modifier.clickable(onClick = {
                    if (isEditing) {
                        onEditCompleted(editingText) // Save changes when done editing
                    }
                    isEditing = !isEditing // Toggle edit mode
                })
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Folder",
                modifier = Modifier.clickable(onClick = onDelete)
            )
        }
    }
}

@Composable
fun FoldersScreen(folders: List<String>, onFoldersUpdated: (List<String>) -> Unit) {
    Column {
        // Display the list of folders
        FolderList(folders) { updatedFolders ->
            onFoldersUpdated(updatedFolders)
        }
    }
}

@Composable
fun FolderList(folders: List<String>, onFoldersUpdated: (List<String>) -> Unit) {
    Column {
        Spacer(Modifier.height(8.dp))

        if (folders.isEmpty()) {
            Text(text = "No folders added yet.")
        } else {
            folders.forEachIndexed { index, folder ->
                FolderItem(
                    folderName = folder,
                    onEditCompleted = { updatedName ->
                        val updatedFolders = folders.toMutableList().apply {
                            this[index] = updatedName
                        }
                        onFoldersUpdated(updatedFolders)
                    },
                    onDelete = {
                        val updatedFolders = folders.toMutableList().apply {
                            removeAt(index)
                        }
                        onFoldersUpdated(updatedFolders)
                    }
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}


@Composable
fun AddNoteForm(
    titleFromCamera: String?,
    contentFromCamera: String?,
    onEvent: (NoteEvent) -> Unit,
    viewModel: NoteViewModel,
    viewModelV2: NoteViewModelV2
) {
    var title by remember { mutableStateOf(titleFromCamera) }
    var titleReady by remember { mutableStateOf(false) }

    var content by remember { mutableStateOf(contentFromCamera) }
    var contentReady by remember { mutableStateOf(false) }

    var translateToCode by remember { mutableStateOf(languageCodeToNameMap["en"]) }
    var loading by remember { mutableStateOf(false) }

    fun onContentChange(string: String) {
        content = string
        contentReady = true
        if (titleReady) {
            loading = false
            contentReady = false
            titleReady = false
        }
    }

    fun onTitleChange(string: String) {
        title = string
        titleReady = true
        if (contentReady) {
            loading = false
            contentReady = false
            titleReady = false
        }
    }

    fun onLanguageChange(languageCode: String) {
        translateToCode = languageCode
    }

    val state by viewModel.state.collectAsState()
    onEvent(NoteEvent.EmptyState)

    state.title = title.toString()
    state.content = content.toString()

    if (!loading) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            Arrangement.Top,
            Alignment.Start
        ) {
            Text("Add new note", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                maxLines = 1,
                value = state.title,
                textStyle = TextStyle(
                    fontSize = 30.sp
                ),
                label = { Text(text = "Note title") },
                onValueChange = { newText ->
                    title = newText
                    onEvent(NoteEvent.SetTitle(newText))
                }
            )
            Spacer(Modifier.height(24.dp))
            TextField(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                value = state.content,
                onValueChange = { newText ->
                    content = newText
                    onEvent(NoteEvent.SetContent(newText))
                },
                label = { Text("Note") }
            )
            Spacer(Modifier.height(24.dp))

            var translatorOpen by remember {
                mutableStateOf(false)
            }
            Row(modifier = Modifier.clickable { translatorOpen = !translatorOpen }) {
                Text(text = "Translator options")
                if (!translatorOpen) {
                    Icon(Icons.Filled.KeyboardArrowDown, "Open translator")
                } else {
                    Icon(Icons.Filled.KeyboardArrowUp, "Close translator")
                }
            }
            if (translatorOpen) {
                Column(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(24.dp))
                    LanguageSelector(::onLanguageChange)
                    Button(onClick = {
                        content?.let {
                            translateToCode?.let { it1 ->
                                translateString(
                                    it1,
                                    it,
                                    ::onContentChange
                                )
                            }
                            loading = true
                        }
                        title?.let {
                            translateToCode?.let { it1 ->
                                translateString(
                                    it1,
                                    it,
                                    ::onTitleChange
                                )
                            }
                            loading = true
                        }
                    }) {
                        Text(text = "Translate")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                Arrangement.End
            ) {
                Button(onClick = { viewModelV2.insertNote(title!!, content!!) }) {
                    Text(text = "Save")
                }
            }
        }
    } else {
        LoadingElement(loadingText = "Translating")
    }
}