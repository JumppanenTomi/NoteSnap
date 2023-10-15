package fi.notesnap.notesnap.elements.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.viewmodels.FolderViewModel
import fi.notesnap.notesnap.elements.shared.Search

@Composable
fun FolderList(
    folders: List<Folder>,
    viewModel: FolderViewModel,
    navController: NavController  // Add this parameter
) {
    Column {
        if (folders.isEmpty()) {
            Text(text = "No folders added yet.")
        } else {
            Search(folders, null, null, null)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )
            ) {
                items(folders) { folder ->
                    FolderItem(
                        folder = folder,
                        viewModel = viewModel,
                        navController = navController
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}