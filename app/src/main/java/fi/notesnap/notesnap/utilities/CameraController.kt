package fi.notesnap.notesnap.utilities

import android.content.Context
import android.util.Log.d
import android.util.Log.e
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import fi.notesnap.notesnap.machineLearning.ContextRecognizer
import fi.notesnap.notesnap.machineLearning.TextRecognizer

class CameraController(
    context: Context,
    onDetectedTitleUpdate: (String) -> Unit,
    onDetectedContentUpdate: (String) -> Unit,
) {
    private var imageCapture: ImageCapture? = null

    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    private val textRecognizer =
        TextRecognizer(
            onDetectedTextUpdate = onDetectedContentUpdate
        )
    private val contextRecognizer =
        ContextRecognizer(
            onDetectedTextUpdate = onDetectedTitleUpdate
        )

    fun startPreviewView(
        previewView: PreviewView,
        updatePreviewView: (PreviewView) -> Unit,
        context: Context,
        owner: LifecycleOwner,
    ) {
        cameraProviderFuture.addListener({
            val preview =
                Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture =
                ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).build()

            val camSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                updatePreviewView(previewView)
                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get()
                    .bindToLifecycle(owner, camSelector, preview, imageCapture)
            } catch (e: Exception) {
                e(
                    "fi.notesnap.notesnap.utilities.CameraController",
                    "Error initializing camera: ${e.message}"
                )
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto(
        context: Context,
    ) {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    e(
                        "fi.notesnap.notesnap.utilities.CameraController",
                        "Error capturing image: ${exception.message}"
                    )
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        d(
                            "fi.notesnap.notesnap.utilities.CameraController",
                            "Starting text recognition"
                        )
                        textRecognizer.analyze(image)
                        contextRecognizer.analyze(image)
                    } catch (e: Exception) {
                        e(
                            "fi.notesnap.notesnap.utilities.CameraController",
                            "Error during text recognition: ${e.message}"
                        )
                    } finally {
                        image.close()
                    }
                }
            }
        )
    }
}
