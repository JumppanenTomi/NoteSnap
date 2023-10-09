package fi.notesnap.notesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import fi.notesnap.notesnap.CameraUtilities.allPermissionsGranted
import fi.notesnap.notesnap.ui.theme.NoteSnapTheme

sealed class Screen {
    object CameraView : Screen()
    object FolderView : Screen()
    object CreateFolder : Screen()
}

class MainActivity : ComponentActivity() {
    private lateinit var cameraController: CameraController
    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val viewModelFactory by lazy { NoteViewModelFactory(appDatabase) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraController = CameraController(this, this)
        setContent {
            NoteSnapTheme {
                MainContent(this, cameraController)
            }
        }
    }

    @Composable
    fun MainContent(mainActivity: MainActivity, cameraController: CameraController) {
        val viewModel = ViewModelProvider(mainActivity, mainActivity.viewModelFactory).get(NoteViewModel::class.java)

        var currentScreen by remember { mutableStateOf<Screen>(Screen.CameraView) } // Specify the type explicitly

        when (currentScreen) {
            Screen.CameraView -> CameraCompose(mainActivity, cameraController) {
                if (allPermissionsGranted(mainActivity)) {
                    cameraController.capturePhoto()
                }
            }
            Screen.FolderView -> FolderViewComposable(viewModel)
            Screen.CreateFolder -> CreateFolderComposable(viewModel)
        }
    }
}

