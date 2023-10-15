package fi.notesnap.notesnap.machineLearning

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

/**
 * This mess actually receives string, detects language from it, downloads that and selected target language, translates it and then returns translated string
 */


fun translateString(
    translateToCode: String,
    originalText: String,
    onTranslated: (String) -> Unit
): String {
    // Initialize a LanguageIdentifier to detect the language of the original text
    val languageIdentifier = LanguageIdentification.getClient()
    var translatedText: String = originalText

    // Identify the language of the original text
    languageIdentifier.identifyLanguage(originalText)
        .addOnSuccessListener { translateFromCode ->
            Log.d("Translator", translateFromCode)

            // Check if the language is not undetermined (und)
            if (translateFromCode != "und") {
                // Perform translation if a language is identified
                performTranslation(translateFromCode, translateToCode, originalText, onTranslated)
            } else {
                // If language cannot be determined, return the original text
                onTranslated(originalText)
            }
        }
        .addOnFailureListener {
            Log.e("Translator", "Language detection failed")
            onTranslated(originalText)
        }
    return translatedText
}

// Function to perform translation
private fun performTranslation(
    translateFromCode: String,
    translateToCode: String,
    originalText: String,
    onTranslated: (String) -> Unit
) {
    // Define download conditions for translation model (e.g., requiring Wi-Fi)
    val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    // Configure TranslatorOptions with source and target languages
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(translateFromCode)
        .setTargetLanguage(translateToCode)
        .build()

    // Initialize the translator
    val translator: Translator = Translation.getClient(options)

    // Download the translation model if needed based on conditions
    translator.downloadModelIfNeeded(conditions)
        .addOnSuccessListener {
            Log.d("Translator", "Language model downloaded")

            // Translate the original text
            translator.translate(originalText)
                .addOnSuccessListener { translatedText ->
                    Log.d("Translator", translatedText)
                    // Callback with the translated text
                    onTranslated(translatedText)
                }
                .addOnFailureListener { exception ->
                    Log.d("Translator", exception.toString())
                    // If translation fails, return the original text
                    onTranslated(originalText)
                }
        }
        .addOnFailureListener { exception ->
            Log.e("Translator", "Model download failed: ${exception.message}", exception)
            // If model download fails, return the original text
            onTranslated(originalText)
        }
}
