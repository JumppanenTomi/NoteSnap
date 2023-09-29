package fi.notesnap.notesnap.machineLearning

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ImageLabelRecognition(private val coroutineScope: CoroutineScope) : ImageAnalysis.Analyzer {
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        // Convert the ImageProxy to an InputImage for image labeling
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        // Launch a coroutine to perform image labeling asynchronously
        coroutineScope.launch {
            labeler.process(inputImage)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        val index = label.index
                        Log.d(
                            "QQQ",
                            text + confidence.toString() + index.toString()
                        )
                    }
                    // Close the image proxy after processing
                    image.close()
                }
                .addOnFailureListener { exception ->
                    // Handle image labeling failure
                    Log.e("ImageLabeling", "Image labeling failed", exception)
                    // Close the image proxy after processing
                    image.close()
                }
        }
    }
}