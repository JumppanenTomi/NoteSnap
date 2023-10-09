import android.content.Context
import android.util.Log.d
import android.util.Log.e
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
import fi.notesnap.notesnap.machineLearning.TextRecognizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CameraController(
    private val context: Context,
    private val owner: LifecycleOwner,
    private val onDetectedTextUpdate: (String) -> Unit,
    private val coroutineScope: CoroutineScope
) {
    private var imageCapture: ImageCapture? = null

    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    private val textRecognizer =
        TextRecognizer(
            coroutineScope = coroutineScope,
            onDetectedTextUpdate = onDetectedTextUpdate,
            resetCamera = ::unbindCameraProvider
        )

    fun startPreviewView(): PreviewView {
        // Initialize camera preview and return a PreviewView
        val previewView = PreviewView(context)
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).build()

        val camSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        try {
            cameraProviderFuture.get().bindToLifecycle(owner, camSelector, preview, imageCapture)
        } catch (e: Exception) {
            e("CameraController", "Error initializing camera: ${e.message}")
        }
        return previewView
    }

    fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    unbindCameraProvider()
                    e("CameraController", "Error capturing image: ${exception.message}")
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    coroutineScope.launch {
                        try {
                            d("CameraController", "Starting text recognition")
                            textRecognizer.analyze(image)
                        } catch (e: Exception) {
                            e("CameraController", "Error during text recognition: ${e.message}")
                        } finally {
                            image.close()
                        }
                    }
                }
            }
        )
    }

    fun unbindCameraProvider() {
        d("DEBUG", "Unbinding cameraprovider")
        cameraProviderFuture.get().unbindAll()
    }
}
