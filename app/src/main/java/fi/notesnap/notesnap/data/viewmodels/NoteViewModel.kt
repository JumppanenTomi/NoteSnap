package fi.notesnap.notesnap.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.data.AppDatabase
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note
import fi.notesnap.notesnap.data.state.NoteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NoteViewModelV2(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val noteDao = database.noteDao()
    private val folderDao = database.folderDao()
    var state = MutableStateFlow(NoteState())


    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun insertNote(title: String, content: String) {
        val currentTime = System.currentTimeMillis()
        val note = Note(0, title, content, false, null, currentTime, currentTime)
        viewModelScope.launch { noteDao.insertNote(note) }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { noteDao.deleteNoteById(id) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { noteDao.updateNote(note) }
    }

    fun getNoteById(noteId: Long): LiveData<Note> {
        return noteDao.getNoteById(noteId)
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return folderDao.getAllFolders()
    }

    fun getFolderById(id: MutableState<Long?>): LiveData<Folder> {
        return folderDao.getFolderById(state.value.folderId)
    }


    /*
    fun getNoteById(noteId: Long): LiveData<Note?> {
        return noteDao.getNoteById(noteId)
    }

     */
}