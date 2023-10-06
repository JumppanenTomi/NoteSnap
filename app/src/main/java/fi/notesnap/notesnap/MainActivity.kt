package fi.notesnap.notesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import fi.notesnap.notesnap.CameraUtilities.allPermissionsGranted
import fi.notesnap.notesnap.ui.theme.NoteSnapTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import fi.notesnap.notesnap.views.CameraCompose
import fi.notesnap.notesnap.views.FolderView
import fi.notesnap.notesnap.views.NoteView

class MainActivity : ComponentActivity() {
    private var cameraController: CameraController = CameraController(this, this)
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "notes_nap.db"
        ).build()

    }

    private val folderViewModel by viewModels<FolderViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FolderViewModel(db.noteDao(), 1) as T
                }
            }
        }
    )
    val noteViewModel by viewModels<NoteViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return  NoteViewModel(db.noteDao()) as T
                }
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteSnapTheme {
                val navController = rememberNavController()
                val folderState by folderViewModel.state.collectAsState()

                NavHost(navController, startDestination = "list"){
                    composable("list") { FolderView(state = folderState, navController = navController, folderDao = db.folderDao()) }
                    composable("note/{id}") {NavBackStackEntry ->
                        val noteIdString = NavBackStackEntry.arguments?.getString("id")
                        val noteId = noteIdString?.toLongOrNull()
                        println("note ID is $noteId")

                        val noteState by noteViewModel.state.collectAsState()

                        NavBackStackEntry.arguments?.let {
                            if (noteId != null) {
                                NoteView(
                                    noteId , state = noteState, navController = navController,
                                    onEvent = noteViewModel::onEvent, viewModel = noteViewModel)
                            }
                        }
                    }
                    composable("camera") { navBackStackEntry ->
                        CameraCompose(this@MainActivity, cameraController = cameraController,
                            navController = navController) {
                            if (allPermissionsGranted(this@MainActivity)) {
                                cameraController.capturePhoto()
                            }
                        }
                    }
                }
            }
        }
    }

}