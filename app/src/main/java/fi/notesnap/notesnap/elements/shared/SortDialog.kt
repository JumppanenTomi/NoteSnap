package fi.notesnap.notesnap.elements.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fi.notesnap.notesnap.data.viewmodels.NoteViewModel
import fi.notesnap.notesnap.data.viewmodels.SortField
import fi.notesnap.notesnap.data.viewmodels.SortOrder

@Composable
fun SortDialog(noteViewModel: NoteViewModel) {
    val sortFields = mutableListOf(SortField.Title, SortField.Content, SortField.Time)
    val sortField by noteViewModel.sortField.observeAsState()
    val sortOrder by noteViewModel.sortOrder.observeAsState()

    Dialog(onDismissRequest = { noteViewModel.closeSortDialog() }) {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sortFields) { field ->
                        Column {
                            ListItem(headlineContent = { Text(field.label) }, trailingContent = {
                                if (field == sortField) {
                                    Icon(
                                        when (sortOrder!!) {
                                            SortOrder.ASC -> Icons.Default.ArrowUpward
                                            SortOrder.DESC -> Icons.Default.ArrowDownward
                                        },
                                        contentDescription = "Order",
                                    )
                                }
                            }, modifier = Modifier.clickable {
                                var newSortOrder: SortOrder
                                newSortOrder = if (field == sortField) {
                                    when (sortOrder!!) {
                                        SortOrder.ASC -> SortOrder.DESC
                                        SortOrder.DESC -> SortOrder.ASC
                                    }
                                } else {
                                    SortOrder.ASC
                                }
                                noteViewModel.setSortParams(field, newSortOrder)
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(modifier = Modifier.align(Alignment.End), onClick = {
                    noteViewModel.closeSortDialog()
                }) {
                    Text(text = "Close")
                }
            }
        }
    }
}