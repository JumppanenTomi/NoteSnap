package fi.notesnap.notesnap.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.AppDatabase
import fi.notesnap.notesnap.entities.Note
import kotlinx.coroutines.launch

class NoteViewModelV2(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val noteDao = database.noteDao()

    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun insertNote(title: String, content: String) {
        val currentTime = System.currentTimeMillis()
        val note = Note(0, title, content, false, null, currentTime, currentTime)
        viewModelScope.launch { noteDao.insertNote(note) }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { noteDao.updateNote(note) }
    }

    fun getNoteById(noteId: Long): LiveData<Note> {
        return noteDao.getNoteById(noteId)
    }

    /*
    fun getNoteById(noteId: Long): LiveData<Note?> {
        return noteDao.getNoteById(noteId)
    }

     */
}