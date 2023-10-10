package fi.notesnap.notesnap.machineLearning

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

fun translateString(
    translateToCode: String,
    originalText: String,
    onTranslated: (String) -> Unit
): String {
    val languageIdentifier = LanguageIdentification.getClient()
    var translatedText: String = originalText

    languageIdentifier.identifyLanguage(originalText)
        .addOnSuccessListener { translateFromCode ->
            Log.d("Translator", translateFromCode)
            if (translateFromCode != "und") {
                performTranslation(translateFromCode, translateToCode, originalText, onTranslated)
            } else {
                onTranslated(originalText)
            }
        }
        .addOnFailureListener {
            Log.e("Translator", "Language detection failed")
            onTranslated(originalText)
        }
    return translatedText
}

private fun performTranslation(
    translateFromCode: String,
    translateToCode: String,
    originalText: String,
    onTranslated: (String) -> Unit
) {
    var conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    val options = TranslatorOptions.Builder()
        .setSourceLanguage(translateFromCode)
        .setTargetLanguage(translateToCode)
        .build()

    val translator: Translator = Translation.getClient(options)

    translator.downloadModelIfNeeded(conditions)
        .addOnSuccessListener {
            Log.d("Translator", "language downloaded")
            translator.translate(originalText)
                .addOnSuccessListener { translatedText ->
                    Log.d("Translator", translatedText)
                    onTranslated(translatedText)
                }
                .addOnFailureListener { exception ->
                    Log.d("Translator", exception.toString())
                    onTranslated(originalText)
                }
        }
        .addOnFailureListener { exception ->
            Log.e("Translator", "Model download failed: ${exception.message}", exception)
            onTranslated(originalText)
        }
}