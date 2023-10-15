package fi.notesnap.notesnap.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.data.AppDatabase
import fi.notesnap.notesnap.data.entities.Folder
import kotlinx.coroutines.launch

class FolderViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val folderDao = database.folderDao()

    fun getAllFolders(): LiveData<List<Folder>> {
        return folderDao.getAllFolders()
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch { folderDao.deleteFolder(folder) }
    }
}
