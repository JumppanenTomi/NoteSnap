package fi.notesnap.notesnap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fi.notesnap.notesnap.CameraUtilities.REQUIRED_PERMISSIONS

@Composable
fun CameraCompose(
    context: Context,
    cameraController: CameraController,
    onCaptureClick: () -> Unit,
) {
    var hasCamPermission by remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) ==
                        PackageManager.PERMISSION_GRANTED
            })
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            hasCamPermission = granted.size == 2
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCamPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { cameraController.startPreviewView() }
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(), Arrangement.Bottom, Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onCaptureClick
        ) {
            Text(text = "Capture")
        }
    }
}