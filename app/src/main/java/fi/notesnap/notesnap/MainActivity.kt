package fi.notesnap.notesnap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.notesnap.notesnap.elements.BottomSheetNav
import fi.notesnap.notesnap.elements.CameraCompose
import fi.notesnap.notesnap.elements.FoldersScreen
import fi.notesnap.notesnap.ui.theme.NoteSnapTheme
import fi.notesnap.notesnap.viewmodels.FolderViewModel
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2
import fi.notesnap.notesnap.views.AppInfoView
import fi.notesnap.notesnap.views.FolderAddView
import fi.notesnap.notesnap.views.FolderNoteScreen
import fi.notesnap.notesnap.views.NoteAddView
import fi.notesnap.notesnap.views.NoteListView
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private val noteViewModelV2: NoteViewModelV2 by viewModels()
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NoteSnapTheme {
                val navController = rememberNavController()
                var selectedItem by remember { mutableStateOf("folderList") }
                val scrollBehavior =
                    TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                var folders by remember { mutableStateOf(listOf<String>()) }
                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var showBottomSheet by remember { mutableStateOf(false) }
                var showFloatingButton by remember { mutableStateOf(false) }

                fun toggleFloatingButton(boolean: Boolean) {
                    showFloatingButton = boolean
                }

                Scaffold(topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text(
                                "NoteSnap",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        scrollBehavior = scrollBehavior,
                    )
                }, content = { innerPadding ->
                    Column(
                        Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {

                        NavHost(
                            navController, startDestination = "folderList"
                        ) {
                            composable("folderList") {
                                val folderViewModel: FolderViewModel = viewModel()

                                FoldersScreen(
                                    viewModel = folderViewModel, navController = navController
                                )
                                toggleFloatingButton(true)
                            }


                            composable("folderNotes/{folderId}") { backStackEntry ->
                                val folderId = backStackEntry.arguments?.getString("folderId")
                                    ?.toLongOrNull()
                                if (folderId != null) {
                                    FolderNoteScreen(
                                        navController = navController,
                                        viewModel = viewModel,
                                        folderId = folderId
                                    )
                                } else {
                                    // Handle error (e.g. pop back to folderList)
                                    navController.popBackStack()
                                }
                            }


                            composable("noteList") {
                                toggleFloatingButton(true)
                                NoteListView(
                                    navController = navController,
                                    viewModel = viewModel,
                                    context = applicationContext,
                                    fragmentActivity = this@MainActivity
                                )
                            }
/*
                            composable("settings") {
                                toggleFloatingButton(false)
                                AppInfoView()
                            }

 */
                        }

                        fun onCloseBottomSheet() {
                            showBottomSheet = false
                        }

                        if (showBottomSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showBottomSheet = false
                                }, sheetState = sheetState, modifier = Modifier.fillMaxHeight()
                            ) {
                                val bottomSheetNavController = rememberNavController()
                                var cameraTitle by remember { mutableStateOf("") }
                                var cameraContent by remember { mutableStateOf("") }

                                fun onContentChange(text: String) {
                                    cameraContent = text
                                    if (cameraContent.isNotEmpty() && cameraTitle.isNotEmpty()) {
                                        scope.launch { sheetState.partialExpand() }
                                        bottomSheetNavController.navigate("addNote")
                                    }
                                }

                                fun onTitleChange(text: String) {
                                    cameraTitle = text
                                    if (cameraContent.isNotEmpty() && cameraTitle.isNotEmpty()) {
                                        scope.launch { sheetState.partialExpand() }
                                        bottomSheetNavController.navigate("addNote")
                                    }
                                }

                                NavHost(
                                    navController = bottomSheetNavController,
                                    startDestination = "options"
                                ) {
                                    composable("options") {
                                        BottomSheetNav(navController = bottomSheetNavController)
                                    }
                                    composable("addNote") {
                                        NoteAddView(
                                            cameraTitle,
                                            cameraContent,
                                            viewModel,
                                            ::onCloseBottomSheet
                                        )
                                    }
                                    composable("addFolder") {
                                        FolderAddView(viewModel = viewModel)
                                    }
                                    composable("addWithCamera") {
                                        scope.launch { sheetState.expand() }
                                        CameraCompose(
                                            context = this@MainActivity,
                                            lifecycleOwner = this@MainActivity,
                                            onDetectedTitleUpdate = ::onTitleChange,
                                            onDetectedContentUpdate = ::onContentChange
                                        )
                                    }
                                }
                            }
                        }
                    }
                }, floatingActionButton = {
                    if (showFloatingButton) {
                        FloatingActionButton(onClick = {
                            showBottomSheet = true
                        }) {
                            Icon(Icons.Filled.Add, "Floating action button.")
                        }
                    }
                }, bottomBar = {
                    //TODO: planned icons are not yet implement in material 3 jetpack compose library so for now we use placeholders
                    NavigationBar(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        NavigationBarItem(icon = {
                            Icon(
                                Icons.Filled.Folder,
                                contentDescription = "Folders view",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }, label = {
                            Text(
                                "Folders",
                            )
                        }, selected = selectedItem == "folderList", onClick = {
                            selectedItem = "folderList"; navController.navigate("folderList")
                        })
                        NavigationBarItem(icon = {
                            Icon(
                                Icons.Filled.Note,
                                contentDescription = "Notes view",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                            label = { Text("Notes") },
                            selected = selectedItem == "noteList",
                            onClick = {
                                selectedItem = "noteList"; navController.navigate("noteList")
                            })
                        /*
                        NavigationBarItem(icon = {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Settings view",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                            label = { Text("Settings") },
                            selected = selectedItem == "settings",
                            onClick = {
                                selectedItem = "settings"; navController.navigate("settings")
                            })

                         */
                    }
                })
            }
        }
    }
}