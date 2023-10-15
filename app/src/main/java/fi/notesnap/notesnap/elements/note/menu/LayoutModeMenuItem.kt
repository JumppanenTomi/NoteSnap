package fi.notesnap.notesnap.elements.note.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fi.notesnap.notesnap.data.viewmodels.LayoutMode
import fi.notesnap.notesnap.data.viewmodels.NoteViewModel

@Composable
fun LayoutModeMenuItem(noteViewModel: NoteViewModel) {
    Row(
        Modifier
            .height(60.dp)
            .padding(8.dp)
            .fillMaxWidth(),
        Arrangement.Start,
        Alignment.CenterVertically
    ) {
        Icon(Icons.Default.FilterList, "Change layout")
        Text(
            "Choose layout mode", Modifier.padding(horizontal = 8.dp)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LayoutModeButton(layoutMode = LayoutMode.Small, noteViewModel)
        LayoutModeButton(layoutMode = LayoutMode.Big, noteViewModel)
        LayoutModeButton(layoutMode = LayoutMode.Card, noteViewModel)
    }
}

@Composable
fun LayoutModeButton(layoutMode: LayoutMode, noteViewModel: NoteViewModel) {
    val currentLayoutMode by noteViewModel.layoutMode.observeAsState()
    val isSelected = layoutMode == currentLayoutMode

    Card(
        modifier = Modifier
            .size(72.dp)
            .padding(8.dp)
            .clickable { noteViewModel.setLayoutMode(layoutMode) }, shape = RoundedCornerShape(8.dp)
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