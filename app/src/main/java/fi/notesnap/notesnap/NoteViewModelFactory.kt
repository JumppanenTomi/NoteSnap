package fi.notesnap.notesnap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(appDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
