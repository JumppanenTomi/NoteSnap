package fi.notesnap.notesnap.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.AppDatabase
import fi.notesnap.notesnap.entities.Folder
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val folderDao = database.folderDao()

    fun getAllFolders(): LiveData<List<Folder>> {
        return folderDao.getAllFolders()
    }

    fun insertFolder(name: String) {
        val folder = Folder(name = name)
        viewModelScope.launch {
            try {
                folderDao.insertFolder(folder)
            } catch (e: Exception) {
                Log.e("FolderViewModel", "Error inserting folder", e)
            }
        }
    }

    fun updateFolder(folder: Folder) {
        viewModelScope.launch { folderDao.insertFolder(folder) }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch { folderDao.deleteFolder(folder) }
    }
}
