package fi.notesnap.notesnap.elements.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * This is Composable that is used in main bottom sheet to navigate between "add folder" and "add note"
 */

@Composable
fun BottomSheetNav(navController: NavController) {
    Column(
        Modifier
            .padding(16.dp)
            .defaultMinSize(minHeight = 200.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .height(60.dp)
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    navController.navigate(
                        "addFolder"
                    )
                },
            Arrangement.Start,
            Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Add, "Add new folder")
            Text(
                "Add new folder",
                Modifier.padding(horizontal = 8.dp)
            )
        }
        Divider()
        Row(
            Modifier
                .height(60.dp)
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    navController.navigate(
                        "addNote"
                    )
                },
            Arrangement.Start,
            Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Create, "Add new note")
            Text(
                "Add new note",
                Modifier.padding(horizontal = 8.dp)
            )
        }
        Divider()
        Row(
            Modifier
                .height(60.dp)
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    navController.navigate(
                        "addWithCamera"
                    )
                },
            Arrangement.Start,
            Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Add,
                "Add new note using camera and AI"
            )
            Text(
                "Add new note using camera and AI",
                Modifier.padding(horizontal = 8.dp)
            )
        }
        Divider()
    }
}