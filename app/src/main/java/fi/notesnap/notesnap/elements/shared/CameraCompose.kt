package fi.notesnap.notesnap.elements.shared

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import fi.notesnap.notesnap.utilities.CameraController
import fi.notesnap.notesnap.utilities.CameraUtilities.REQUIRED_PERMISSIONS

/**
 * This is Composable that is used when ever we need to extract text or object from image. It returns camera element and after capture it process it
 */

@Composable
fun CameraCompose(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onDetectedTitleUpdate: (String) -> Unit,
    onDetectedContentUpdate: (String) -> Unit
) {
    var cameraController = CameraController(context, onDetectedTitleUpdate, onDetectedContentUpdate)
    var loading by remember { mutableStateOf(false) }

    //checking if app has permission to use camera
    val hasCamPermission = remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    //If there was no permission we ask it
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            hasCamPermission.value = allPermissionsGranted
        }
    )
    LaunchedEffect(key1 = true) {
        if (!hasCamPermission.value) {
            launcher.launch(REQUIRED_PERMISSIONS)
        }
    }

    //If we are not already processing image we show camera view
    if (!loading) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (hasCamPermission.value) {
                var previewView by remember { mutableStateOf(PreviewView(context)) }

                fun updatePreviewView(updatedPreviewView: PreviewView) {
                    previewView = updatedPreviewView
                }

                cameraController.startPreviewView(
                    previewView,
                    ::updatePreviewView,
                    context,
                    lifecycleOwner
                )

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { previewView }
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(100.dp)
                    .clickable {
                        if (hasCamPermission.value) {
                            loading = true
                            cameraController.capturePhoto(context)
                        }
                    }
                    .clip(CircleShape)
                    .background(Color.Red),
            )
        }
    }
    //otherwise we show loading screen
    else {
        LoadingElement(loadingText = "Processing image")
    }
}