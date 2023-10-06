package fi.notesnap.notesnap.views
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fi.notesnap.notesnap.NoteEvent
import fi.notesnap.notesnap.NoteState
import fi.notesnap.notesnap.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable

fun NoteView(
    id: Long,
    state : NoteState,
    navController: NavController,
    onEvent: (NoteEvent) -> Unit,
    viewModel :NoteViewModel) {
    val context = LocalContext.current
    val kController = LocalSoftwareKeyboardController.current
    println("id $id")
    val event = NoteEvent.UpdateState(id)
    viewModel.onEvent(event)

    Scaffold (
       topBar = {
           TopAppBar(
               title = {
                   Text(
                       "",
                       overflow = TextOverflow.Ellipsis
                   )
               },
               navigationIcon = {
                   IconButton(onClick = {
                       onEvent(NoteEvent.SaveNote)
                       navController.popBackStack()
                   }) {
                       Icon(
                           imageVector = Icons.Filled.ArrowBack,
                           contentDescription = "Localized description"
                       )
                   }
               },
               actions = {
                   IconButton(onClick = {
                       state.locked = state.locked == false
                   println(state.locked)}) {
                       Icon(
                           imageVector = Icons.Outlined.Lock,
                           contentDescription = "Localized description"
                       )
                   }
               }
           )
       },
       content = {innerPadding ->
           Column(Modifier.padding(innerPadding)){
               var title by remember { mutableStateOf("") }
               var content by remember {
                   mutableStateOf("")
               }

               TextField(
                   modifier = Modifier.fillMaxWidth(),
                   textStyle = TextStyle(
                       color = Color.White,
                       fontSize = 30.sp
                   ),
                   keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                   maxLines = 1,
                   value = state.title,
                   onValueChange = {
                       title = state.title
                       onEvent(NoteEvent.SetTitle(it))
                   },
                   label = { Text("Title") }
               )
               TextField(
                   modifier = Modifier.fillMaxWidth(),
                   value = state.content,
                   onValueChange = {
                       content = it
                       onEvent(NoteEvent.SetContent(it)) },

                   label = { Text("Note") }
               )
           }
       },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = {
                        navController.navigate("camera")
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Localized description")
                    }
                },
            )

        }
    )
}



