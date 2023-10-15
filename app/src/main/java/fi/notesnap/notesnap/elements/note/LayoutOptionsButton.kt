package fi.notesnap.notesnap.elements.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LayoutOptionButton(
    layoutMode: LayoutMode, currentLayoutMode: LayoutMode, onLayoutSelected: () -> Unit
) {
    val isSelected = layoutMode == currentLayoutMode

    Card(
        modifier = Modifier
            .size(72.dp)
            .padding(8.dp)
            .clickable { onLayoutSelected() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (layoutMode) {
                    LayoutMode.Small -> Icons.Default.List
                    LayoutMode.Big -> Icons.Default.ViewList
                    LayoutMode.Card -> Icons.Default.GridView
                },
                contentDescription = layoutMode.label,
                tint = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}