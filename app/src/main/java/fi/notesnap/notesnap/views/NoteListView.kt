package fi.notesnap.notesnap.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListView(
    navController: NavController,
    ) {

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NoteSnap",
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        content = {innerPadding ->
            Column (Modifier.padding(innerPadding)){
                Text(text = "....")
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        navController.navigate("note")
                    }) {
                        Icon(Icons.Filled.Create, contentDescription = "Localized description")
                    }
                },
            )

        })
}
