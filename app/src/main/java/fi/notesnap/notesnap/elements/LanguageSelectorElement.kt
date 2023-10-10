package fi.notesnap.notesnap.elements

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mlkit.nl.translate.TranslateLanguage
import fi.notesnap.notesnap.utilities.getLanguageName

@Composable
fun LanguageSelector(onLanguageChange: (String) -> Unit) {
    var selectedLanguage by remember { mutableStateOf(TranslateLanguage.ENGLISH) }
    val availableLanguages = TranslateLanguage.getAllLanguages()
    var expanded by remember { mutableStateOf(false) }

    Button(onClick = { expanded = !expanded }) {
        Text(text = getLanguageName(selectedLanguage))
    }

    DropdownMenu(
        modifier = Modifier.height(200.dp),
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        availableLanguages.forEach { language ->
            DropdownMenuItem(
                text = { Text(text = getLanguageName(language)) },
                onClick = {
                    selectedLanguage = language; expanded = false; onLanguageChange(
                    selectedLanguage
                )
                }
            )
        }
    }
}