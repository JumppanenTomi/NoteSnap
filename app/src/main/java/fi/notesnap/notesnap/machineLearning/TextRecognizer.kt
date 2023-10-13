package fi.notesnap.notesnap.machineLearning

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * this class detects text from images using ML kit
 */

class TextRecognizer(
    private val onDetectedTextUpdate: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    // Initialize the text recognizer using default options
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        // Convert the ImageProxy to an InputImage for text recognition
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        // Process the input image for text recognition
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                // Extract detected text from result's text blocks
                val detectedText = result.textBlocks.joinToString(separator = "\n") { textBlock ->
                    textBlock.text
                }
                // Update the detected text via the provided callback
                onDetectedTextUpdate(detectedText)
            }
            .addOnFailureListener { exception ->
                // Handle text recognition failure, log the error, and update the detected text as an empty string
                Log.e(
                    "TextAnalyzer",
                    "Text recognition failed with exception: ${exception.message}",
                    exception
                )
                onDetectedTextUpdate("")
            }
            .addOnCompleteListener {
                // Always close the ImageProxy, regardless of success or failure
                image.close()
            }
    }
}
