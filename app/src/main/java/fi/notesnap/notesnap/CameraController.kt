package fi.notesnap.notesnap

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import fi.notesnap.notesnap.machineLearning.TextRecognizer
import kotlinx.coroutines.launch

class CameraController(
    private var context: Context,
    private var owner: LifecycleOwner,
    private var onDetectedTextUpdate: (String) -> Unit,
) {
    private var imageCapture: ImageCapture? = null

    fun startPreviewView(): PreviewView {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val previewView = PreviewView(context)
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder().build()
        val camSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        try {
            cameraProviderFuture.get().bindToLifecycle(owner, camSelector, preview, imageCapture)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return previewView
    }

    fun capturePhoto() = owner.lifecycleScope.launch {
        val imageCapture = imageCapture ?: return@launch

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                owner.lifecycleScope.launch {
                    val textRecognizer =
                        TextRecognizer(
                            coroutineScope = this,
                            onDetectedTextUpdate = onDetectedTextUpdate
                        )
                    textRecognizer.analyze(image)
                }
            }
        })
    }
}