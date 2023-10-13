package fi.notesnap.notesnap.machineLearning

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

/**
 * This class detects objects from image using ML Kit
 */

class ContextRecognizer(private val onDetectedTextUpdate: (String) -> Unit) :
    ImageAnalysis.Analyzer {

    // Initialize the image labeler using default options
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        // Convert the ImageProxy to an InputImage for image labeling
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        // Launch a coroutine to perform image labeling asynchronously
        labeler.process(inputImage)
            .addOnSuccessListener { labels ->
                var bestMatch = ""
                var bestMatchConf = 0.00

                // Iterate through the detected labels
                for (label in labels) {
                    if (bestMatch == "") {
                        bestMatch = label.text
                    } else if (bestMatchConf < label.confidence.toDouble()) {
                        bestMatch = label.text
                        bestMatchConf = label.confidence.toDouble()
                    }
                    // Log the detected labels (for debugging)
                    Log.d("QQQ", label.text)
                }
                // Update the detected text via the provided callback
                onDetectedTextUpdate(bestMatch)
                // Close the ImageProxy
                image.close()
            }
            .addOnFailureListener { exception ->
                // Handle image labeling failure
                // Update the detected text as empty string and log the error
                onDetectedTextUpdate(" ")
                Log.e("ImageLabeling", "Image labeling failed", exception)
            }
            .addOnCompleteListener {
                // Always close the ImageProxy, regardless of success or failure
                image.close()
            }
    }
}
