package fi.notesnap.notesnap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.entities.Folder
import fi.notesnap.notesnap.entities.Note
import kotlinx.coroutines.launch

class NoteViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    fun getAllFolders(): LiveData<List<Folder>> = appDatabase.folderDao().getAllFolders()

    fun getNotesByFolder(folderId: Long): LiveData<List<Note>> = appDatabase.noteDao().getNotesByFolder(folderId)

    fun createFolder(folderName: String) {
        viewModelScope.launch {
            val folder = Folder(name = folderName)
            appDatabase.folderDao().insertFolder(folder)
        }
    }

}