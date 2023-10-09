package fi.notesnap.notesnap.machineLearning

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognizer(
    private val onDetectedTextUpdate: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        //TODO: Make check multiple times and then compare that results are same. And end only when over 50% of results are same
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                val detectedText = result.textBlocks.joinToString(separator = "\n") { textBlock ->
                    textBlock.text
                }
                Log.d("QQQ", result.text)
                onDetectedTextUpdate(detectedText)
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "TextAnalyzer",
                    "Text recognition failed with exception: ${exception.message}",
                    exception
                )
                onDetectedTextUpdate("")
            }
            .addOnCompleteListener {
                image.close()
            }

    }
}