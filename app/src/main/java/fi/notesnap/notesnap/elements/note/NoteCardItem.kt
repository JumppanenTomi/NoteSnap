package fi.notesnap.notesnap.elements.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.utilities.formatTimeAgo

@Composable
fun CardNoteItem(note: Note, folders: List<Folder>, onClick: () -> Unit) {
    val folder = folders.find { it.id == note.folderId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(256.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

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
}