package fi.notesnap.notesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fi.notesnap.notesnap.CameraUtilities.allPermissionsGranted
import fi.notesnap.notesnap.ui.theme.NoteSnapTheme

class MainActivity : ComponentActivity() {
    private var cameraController: CameraController = CameraController(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteSnapTheme {
                CameraCompose(this, cameraController = cameraController) {
                    if (allPermissionsGranted(this)) {
                        cameraController.capturePhoto()
                    }
                }
            }
        }
    }
}