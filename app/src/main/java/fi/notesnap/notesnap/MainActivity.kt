package fi.notesnap.notesnap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import fi.notesnap.notesnap.elements.AddFolderForm
import fi.notesnap.notesnap.elements.AddNoteForm
import fi.notesnap.notesnap.ui.theme.NoteSnapTheme
import fi.notesnap.notesnap.views.CameraCompose
import fi.notesnap.notesnap.views.FolderView
import fi.notesnap.notesnap.views.NoteView
import fi.notesnap.notesnap.views.SettingsView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val lifecycleOwner = this

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "notes_nap.db"
        ).build()

    }

    private val folderViewModel by viewModels<FolderViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FolderViewModel(db.noteDao(), 1) as T
                }
            }
        }
    )
    private val noteViewModel by viewModels<NoteViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(db.noteDao()) as T
                }
            }
        }
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteSnapTheme {
                val navController = rememberNavController()
                val folderState by folderViewModel.state.collectAsState()
                var selectedItem by remember { mutableStateOf("") }
                val scrollBehavior =
                    TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var showBottomSheet by remember { mutableStateOf(false) }
                var showFloatingButton by remember { mutableStateOf(false) }

                fun toggleFloatingButton(boolean: Boolean) {
                    showFloatingButton = boolean
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    "NoteSnap",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    content = { innerPadding ->
                        Column(Modifier.padding(innerPadding)) {
                            NavHost(
                                navController,
                                startDestination = "list"
                            ) {
                                composable("list") {
                                    toggleFloatingButton(true)
                                    FolderView(
                                        state = folderState,
                                        navController = navController,
                                        folderDao = db.folderDao()
                                    )
                                }
                                composable("note/{id}") { navBackStackEntry ->
                                    toggleFloatingButton(true)
                                    val noteIdString = navBackStackEntry.arguments?.getString("id")
                                    val noteId = noteIdString?.toLongOrNull()

                                    navBackStackEntry.arguments?.let {
                                        if (noteId != null) {
                                            NoteView(
                                                noteId,
                                                navController = navController,
                                                onEvent = noteViewModel::onEvent,
                                                viewModel = noteViewModel,
                                                lifecycleOwner
                                            )
                                        }
                                    }
                                }
                                composable("settings") {
                                    toggleFloatingButton(false)
                                    SettingsView()
                                }
                            }
                            if (showBottomSheet) {
                                ModalBottomSheet(
                                    onDismissRequest = {
                                        showBottomSheet = false
                                    },
                                    sheetState = sheetState,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    val bottomSheetNavController = rememberNavController()
                                    var cameraText by remember {
                                        mutableStateOf("Not yet")
                                    }

                                    fun onTextChange(text: String) {
                                        scope.launch { sheetState.partialExpand() }
                                        cameraText = text
                                        bottomSheetNavController.navigate("addNote/${"title"}/${text}")
                                    }

                                    NavHost(
                                        navController = bottomSheetNavController,
                                        startDestination = "options",
                                    ) {
                                        composable("options") {
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
                                                            bottomSheetNavController.navigate(
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
                                                            bottomSheetNavController.navigate(
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
                                                            bottomSheetNavController.navigate(
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
                                        composable("addNote/{title}/{content}") { result ->
                                            val title = result.arguments?.getString("title")
                                            val content = result.arguments?.getString("content")
                                            AddNoteForm(title, content)
                                        }
                                        composable("addFolder") {
                                            AddFolderForm()
                                        }
                                        composable("addWithCamera") {
                                            scope.launch { sheetState.expand() }
                                            CameraCompose(
                                                context = this@MainActivity,
                                                lifecycleOwner = lifecycleOwner,
                                                onDetectedTextUpdate = ::onTextChange
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        if (showFloatingButton) {
                            FloatingActionButton(onClick = {
                                navController.navigate("note/0"); showBottomSheet = true
                            }) {
                                Icon(Icons.Filled.Add, "Floating action button.")
                            }
                        }
                    },
                    bottomBar = {
                        //TODO: planned icons are not yet implement in material 3 jetpack compose library so for now we use placeholders
                        NavigationBar {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Filled.Home,
                                        contentDescription = "Folders view"
                                    )
                                },
                                label = { Text("Folders") },
                                selected = selectedItem == "folders",
                                onClick = {
                                    selectedItem = "folders"; navController.navigate("list")
                                }
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Filled.List,
                                        contentDescription = "Notes view"
                                    )
                                },
                                label = { Text("Notes") },
                                selected = selectedItem == "notes",
                                onClick = {
                                    selectedItem = "notes"; navController.navigate("notes")
                                }
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Filled.Settings,
                                        contentDescription = "Settings view"
                                    )
                                },
                                label = { Text("Settings") },
                                selected = selectedItem == "settings",
                                onClick = {
                                    selectedItem = "settings"; navController.navigate("settings")
                                }
                            )
                        }
                    })
            }
        }
    }

}