package fi.notesnap.notesnap.elements.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.data.viewmodels.LayoutMode
import fi.notesnap.notesnap.utilities.formatTimeAgo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListNoteItem(note: Note, layoutMode: LayoutMode, folders: List<Folder>, onClick: () -> Unit) {
    val folder = folders.find { it.id == note.folderId }

    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {


                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (note.locked) "Unlock to see content" else note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = when (layoutMode) {
                        LayoutMode.Small -> 1
                        LayoutMode.Big -> 3
                        else -> 0
                    },
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (note.locked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(8.dp))

                        }


                        // Display folder name (You can style this as needed)
                        Text(
                            text = if (folder != null) "#${folder.name}" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = formatTimeAgo(note.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray, // You can change the color
                    )
                }
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(), color = Color.LightGray, thickness = 1.dp
        )
    }
}