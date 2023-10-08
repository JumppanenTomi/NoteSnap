package fi.notesnap.notesnap.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import fi.notesnap.notesnap.FolderState
import fi.notesnap.notesnap.daos.FolderDao
import fi.notesnap.notesnap.elements.ListNotes
import fi.notesnap.notesnap.entities.Folder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FolderView(
    state: FolderState,
    navController: NavController,
    folderDao: FolderDao,
) {
    val folderCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(folderCount) {
        val count = checkFolderCount(folderDao)
        if (count == 0) {
            createFolder(folderDao)
        }
    }

    ListNotes(children = state.notes, navController = navController)
}

// this is temporary
fun createFolder(dao: FolderDao) {
    val folder = Folder(0, "Main")
    val myCoroutineScope = CoroutineScope(Dispatchers.Default)
    myCoroutineScope.launch {
        dao.insertFolder(folder)
        println("Folder created")
    }
}

suspend fun checkFolderCount(dao: FolderDao): Int {
    return withContext(Dispatchers.Default) {
        val count = dao.getFolderCount()
        println("folder count $count")
        count
    }
}