package fi.notesnap.notesnap.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fi.notesnap.notesnap.data.AppDatabase
import fi.notesnap.notesnap.data.entities.Folder
import fi.notesnap.notesnap.data.entities.Note

enum class LayoutMode(val label: String) {
    Small("Small"), Big("Big"), Card("Card")
}

enum class SortField(val label: String) {
    Title("Title"), Content("Content"), Time("Time")
}

enum class SortOrder { ASC, DESC }

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val noteDao = database.noteDao()
    private val folderDao = database.folderDao()

    var layoutMode = MutableLiveData<LayoutMode>(LayoutMode.Small)
    var sortField = MutableLiveData<SortField>(SortField.Title)
    var sortOrder = MutableLiveData<SortOrder>(SortOrder.ASC)

    var showFolderDialog = MutableLiveData<Boolean>(false)
    var showSortDialog = MutableLiveData<Boolean>(false)
    var folder = MutableLiveData<Folder?>(null)


    fun setLayoutMode(newLayoutMode: LayoutMode) {
        layoutMode.postValue(newLayoutMode)
    }

    fun setFolder(newFolder: Folder?) {
        folder.postValue(newFolder)
    }

    fun setSortParams(newSortField: SortField, newSortOrder: SortOrder) {
        sortField.postValue(newSortField)
        sortOrder.postValue(newSortOrder)
    }

    fun openSortDialog() {
        showSortDialog.postValue(true)
    }

    fun closeSortDialog() {
        showSortDialog.postValue(false)
    }

    fun getAllNotes(folder: Folder?): LiveData<List<Note>> {
        return if (folder == null) {
            noteDao.getAllNotes()
        } else {
            noteDao.getAllNotesByFolder(folder.id)
        }
    }

    fun openFolderDialog() {
        showFolderDialog.postValue(true)
    }

    fun closeFolderDialog() {
        showFolderDialog.postValue(false)
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return folderDao.getAllFolders()
    }
}