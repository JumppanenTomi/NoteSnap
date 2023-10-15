package fi.notesnap.notesnap.elements.note.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import fi.notesnap.notesnap.data.viewmodels.NoteViewModel
import fi.notesnap.notesnap.elements.shared.SortDialog

@Composable
fun SortByMenuItem(noteViewModel: NoteViewModel) {
    val showSortDialog by noteViewModel.showSortDialog.observeAsState(false)
    val sortField by noteViewModel.sortField.observeAsState()

    Row(
        Modifier
            .height(60.dp)
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { noteViewModel.openSortDialog() },
        Arrangement.Start,
        Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Sort, "Sort by")
        Text(
            buildAnnotatedString {
                append(" Sort by ")
                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${sortField?.label}")
                    }
                }
            }, modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    if (showSortDialog) {
        SortDialog(noteViewModel = noteViewModel)
    }
}