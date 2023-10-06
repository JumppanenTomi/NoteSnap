package fi.notesnap.notesnap.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.notesnap.notesnap.FolderState
import fi.notesnap.notesnap.NoteState
import fi.notesnap.notesnap.daos.FolderDao
import fi.notesnap.notesnap.daos.NoteDao
import fi.notesnap.notesnap.entities.Folder
import fi.notesnap.notesnap.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun FolderView(
    state: FolderState,
    navController: NavController,
    folderDao: FolderDao,
    ) {
    val folderCount by remember { mutableStateOf(0) }

    LaunchedEffect(folderCount) {
        val count = checkFolderCount(folderDao)
        if (count == 0) {
            createFolder(folderDao)
        }
    }
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NoteSnap",
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        },
        content = {innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                items(state.notes){note ->
                    Row(
                        modifier= Modifier.clickable {
                            navController.navigate("note/${note.id}")
                        }
                    ){
                        Text(text = note.title)
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        navController.navigate("note")
                    }) {
                        Icon(Icons.Filled.Create, contentDescription = "Localized description")
                    }
                },
            )

        })
}

// this is temporary
fun createFolder(dao: FolderDao){
     val folder = Folder( 0,"Main")
     val myCoroutineScope = CoroutineScope(Dispatchers.Default)
     myCoroutineScope.launch {     dao.insertFolder(folder)
    println("Folder created")}
 }

suspend fun checkFolderCount(dao: FolderDao): Int {
    return withContext(Dispatchers.Default) {
        val count = dao.getFolderCount()
        println("folder count $count")
        count
    }
}