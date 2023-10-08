package fi.notesnap.notesnap.machineLearning

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TextRecognizer(
    private val coroutineScope: CoroutineScope,
    private val onDetectedTextUpdate: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        //TODO: Make check multiple times and then compare that results are same. And end only when over 50% of results are same
        coroutineScope.launch {
            recognizer.process(inputImage)
                .addOnSuccessListener { result ->
                    Log.d("QQQ", result.text)
                    onDetectedTextUpdate(result.text)
                    image.close()
                }
                .addOnFailureListener { exception ->
                    // Handle text recognition failure
                    Log.e("TextAnalyzer", "Text recognition failed", exception)
                    // Close the image proxy after processing
                    image.close()
                }
        }
    }
}