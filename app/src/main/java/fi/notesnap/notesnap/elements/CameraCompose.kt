package fi.notesnap.notesnap.elements

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import fi.notesnap.notesnap.CameraController
import fi.notesnap.notesnap.CameraUtilities.REQUIRED_PERMISSIONS

@Composable
fun CameraCompose(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onDetectedTextUpdate: (String) -> Unit
) {
    var cameraController: CameraController =
        CameraController(context, lifecycleOwner, onDetectedTextUpdate)
    var loading by remember { mutableStateOf(false) }

    //TODO: Move permission check to CameraUtilities
    val hasCamPermission = remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

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

    if (!loading) {
        // Composable content
        Column(modifier = Modifier.fillMaxSize()) {
            Log.d("DEBUG", "start preview")
            if (hasCamPermission.value) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { cameraController.startPreviewView() }
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
                            Log.d("DEBUG", "Camera has permission")
                            cameraController.capturePhoto()
                        } else {
                            Log.d("DEBUG", "No camera permission")
                            //TODO: add snackbar aka Toast here.
                        }
                    }
                    .clip(CircleShape)
                    .background(Color.Red), // Change the background color as needed
            )
        }
    } else {
        LoadingElement(loadingText = "Processing image")
    }
}