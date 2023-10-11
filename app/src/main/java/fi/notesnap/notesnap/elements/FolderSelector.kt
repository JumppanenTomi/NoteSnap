package fi.notesnap.notesnap.elements


import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSelector(viewModelV2: NoteViewModelV2) {
    val listOfFolders by viewModelV2.getAllFolders().observeAsState(initial = emptyList())
    Log.d("EEEEE", listOfFolders.toString())
    AlertDialog(
        onDismissRequest = { },
        content = {
            LazyColumn() {
                items(listOfFolders) { folder ->
                    Text(text = folder.name)
                }
            }
        }
    )
}