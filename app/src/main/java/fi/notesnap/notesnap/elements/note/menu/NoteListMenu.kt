package fi.notesnap.notesnap.elements.note.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fi.notesnap.notesnap.data.viewmodels.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListMenu(noteViewModel: NoteViewModel, closeMenu: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { closeMenu() },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            LayoutModeMenuItem(noteViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            GroupByFolderMenuItem(noteViewModel)
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            SortByMenuItem(noteViewModel)
        }
    }
}