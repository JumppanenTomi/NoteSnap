package fi.notesnap.notesnap.elements.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import fi.notesnap.notesnap.data.viewmodels.FolderViewModel

@Composable
fun FoldersScreen(
    viewModel: FolderViewModel,
    navController: NavController
) {
    val folders by viewModel.getAllFolders().observeAsState(emptyList())

    Column {
        FolderList(folders, viewModel, navController)
    }
}