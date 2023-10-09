package fi.notesnap.notesnap.machineLearning

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ContextRecognizer(private val onDetectedTextUpdate: (String) -> Unit) :
    ImageAnalysis.Analyzer {
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        // Convert the ImageProxy to an InputImage for image labeling
        val inputImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)

        // Launch a coroutine to perform image labeling asynchronously
        labeler.process(inputImage)
            .addOnSuccessListener { labels ->
                var bestMatch = ""
                var bestMatchConf = 0.00

                for (label in labels) {
                    if (bestMatch == "") {
                        bestMatch = label.text
                    } else if (bestMatchConf < label.confidence.toDouble()) {
                        bestMatch = label.text
                        bestMatchConf = label.confidence.toDouble()
                    }
                    Log.d("QQQ", label.text)
                }
                Log.d("QQQ", "best match was " + bestMatch)
                onDetectedTextUpdate(bestMatch)
                image.close()
            }
            .addOnFailureListener { exception ->
                // Handle image labeling failure
                onDetectedTextUpdate(" ")
                Log.e("ImageLabeling", "Image labeling failed", exception)
            }
            .addOnCompleteListener {
                image.close()
            }
    }
}