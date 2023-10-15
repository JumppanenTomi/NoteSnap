package fi.notesnap.notesnap.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.data.AppDatabase
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val noteDao = database.noteDao()
    private val folderDao = database.folderDao()

    fun getAllNotes(folder: Folder?): LiveData<List<Note>> {
        return if (folder == null) noteDao.getAllNotes() else noteDao.getNotesByFolder(folder.id)
    }

    fun getByFolderId(folderId: Long): LiveData<List<Note>> {
        return noteDao.getNotesByFolder(folderId)
    }

    fun insertNote(title: String, content: String, locked: Boolean, folderId: Long?) {
        val currentTime = System.currentTimeMillis()
        val note = Note(
            0, title, content, locked, folderId, createdAt = currentTime, updatedAt = currentTime
        )
        viewModelScope.launch { noteDao.insertNote(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { noteDao.updateNote(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { noteDao.deleteNote(note) }
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return folderDao.getAllFolders()
    }

    fun insertFolder(name: String, description: String) {
        val folder = Folder(0, name)
        viewModelScope.launch { folderDao.insertFolder(folder) }
    }
}