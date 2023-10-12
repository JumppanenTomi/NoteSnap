package fi.notesnap.notesnap

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.notesnap.notesnap.elements.AddFolderForm
import fi.notesnap.notesnap.elements.AddNoteForm
import fi.notesnap.notesnap.elements.BottomSheetNav
import fi.notesnap.notesnap.elements.CameraCompose
import fi.notesnap.notesnap.elements.FoldersScreen
import fi.notesnap.notesnap.entities.Folder
import fi.notesnap.notesnap.ui.theme.NoteSnapTheme
import fi.notesnap.notesnap.utilities.BiometricUnlockNote
import fi.notesnap.notesnap.viewmodels.FolderViewModel
import fi.notesnap.notesnap.viewmodels.NoteViewModelV2
import fi.notesnap.notesnap.views.NoteScreen
import fi.notesnap.notesnap.views.SettingsView
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class MainActivity : FragmentActivity() {

    private val noteViewModelV2:NoteViewModelV2 by viewModels()
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var biometricUnlockNote = BiometricUnlockNote(applicationContext, this)
        biometricUnlockNote.checkDeviceHasBiometric(this)


        setContent {
            NoteSnapTheme {
                val navController = rememberNavController()
                var selectedItem by remember { mutableStateOf("") }
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
                                startDestination = "folderList"
                            ) {
                                composable("folderList") {
                                    val folderViewModel: FolderViewModel = viewModel()

                                    // Removed the folders and onFoldersUpdated parameters
                                    FoldersScreen(
                                        viewModel = folderViewModel
                                    )
                                    toggleFloatingButton(true)
                                }

                                composable("noteList") {
                                    toggleFloatingButton(true)
                                    NoteScreen(
                                        navController,
                                        noteViewModelV2,
                                        biometricUnlockNote
                                    )
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
                                            AddNoteForm(
                                                cameraTitle,
                                                cameraContent,
                                                viewModelV2 = noteViewModelV2
                                            )
                                        }
                                        composable("addFolder") {
                                            val folderViewModel = viewModel<FolderViewModel>()
                                            AddFolderForm(viewModel = folderViewModel)
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
                    },
                    floatingActionButton = {
                        if (showFloatingButton) {
                            FloatingActionButton(onClick = {
                                showBottomSheet = true
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
                                    selectedItem =
                                        "folderList"; navController.navigate("folderList")
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
                                    selectedItem =
                                        "noteList"; navController.navigate("noteList")
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
                                    selectedItem =
                                        "settings"; navController.navigate("settings")
                                }
                            )
                        }
                    })
            }
        }
    }
}