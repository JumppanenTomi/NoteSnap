package fi.notesnap.notesnap.views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.mlkit.nl.translate.TranslateLanguage
import fi.notesnap.notesnap.MainViewModel
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.elements.LoadingElement
import fi.notesnap.notesnap.machineLearning.translateString
import fi.notesnap.notesnap.utilities.getLanguageName

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun NoteAddView(
    titleFromCamera: String?,
    contentFromCamera: String?,
    viewModel: MainViewModel,
    onCloseBottomSheet: () -> Unit
) {
    val context = LocalContext.current
    val folders by viewModel.getAllFolders().observeAsState(listOf())
    val languages = TranslateLanguage.getAllLanguages()
    var title by remember { mutableStateOf(titleFromCamera ?: "") }
    var content by remember { mutableStateOf(contentFromCamera ?: "") }
    var locked by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf<Folder?>(null) }

    var showLanguagesDialog by remember { mutableStateOf(false) }
    var showFoldersDialog by remember { mutableStateOf(false) }

    var titleReady by remember { mutableStateOf(false) }
    var contentReady by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    if (!loading) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
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
                        Icon(imageVector = Icons.Default.Check,
                            contentDescription = "Save Note",
                            modifier = Modifier
                                .clickable {
                                    if (title.isNotBlank()) {
                                        viewModel.insertNote(
                                            title, content, locked, selectedFolder?.id
                                        )
                                        onCloseBottomSheet()
                                        Toast
                                            .makeText(
                                                context, "Added successfully", Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } else {
                                        Toast
                                            .makeText(
                                                context, "Title is required", Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.width(16.dp))

                        Icon(imageVector = Icons.Default.Translate,
                            contentDescription = "Translate note",
                            modifier = Modifier
                                .clickable {
                                    showLanguagesDialog = true
                                }
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(imageVector = Icons.Default.Folder,
                            contentDescription = "Folder Icon",
                            modifier = Modifier
                                .clickable { showFoldersDialog = true }
                                .padding(8.dp),
                            tint = if (selectedFolder != null) MaterialTheme.colorScheme.primary else Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))

                        Icon(imageVector = if (locked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock/Unlock Note",
                            modifier = Modifier
                                .clickable { locked = !locked }
                                .padding(8.dp),
                            tint = if (locked) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NoteAdd,
                    contentDescription = "Note add icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Add new note",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = if (selectedFolder != null) "#${selectedFolder?.name}" else "",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxSize()
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
        }
    } else {
        LoadingElement(loadingText = "Translating")
    }

    if (showFoldersDialog) {
        Dialog(onDismissRequest = { showFoldersDialog = false }) {
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
                            .fillMaxHeight(0.4f)
                    ) {
                        items(folders) { folder ->
                            Column {
                                ListItem(headlineContent = { Text(folder.name) }, leadingContent = {
                                    Icon(
                                        Icons.Default.Folder,
                                        contentDescription = "Localized description",
                                    )
                                }, modifier = Modifier.clickable {
                                    selectedFolder = folder
                                    showFoldersDialog = false
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
                                selectedFolder = null
                                showFoldersDialog = false
                            },
                        ) {
                            Text(text = "Reset")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(onClick = {
                            showFoldersDialog = false
                        }) {
                            Text(text = "Close")
                        }
                    }

                }
            }
        }
    }

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

    if (showLanguagesDialog) {
        Dialog(onDismissRequest = { showLanguagesDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Translate to",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                    ) {
                        items(languages) { language ->
                            Column {
                                ListItem(headlineContent = { Text(getLanguageName(language)) },
                                    modifier = Modifier.clickable {
                                        if (title.isNotEmpty()) {
                                            showLanguagesDialog = false
                                            content.let {
                                                language?.let { it1 ->
                                                    translateString(
                                                        it1, it, ::onContentChange
                                                    )
                                                }
                                                loading = true
                                            }
                                            title.let {
                                                language?.let { it1 ->
                                                    translateString(
                                                        it1, it, ::onTitleChange
                                                    )
                                                }
                                                loading = true
                                            }
                                        } else {
                                            Toast.makeText(
                                                context, "Title is required", Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showLanguagesDialog = false
                        }, modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Close")
                    }

                }
            }
        }
    }
}