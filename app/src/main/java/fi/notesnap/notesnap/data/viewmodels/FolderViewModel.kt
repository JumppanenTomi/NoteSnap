package fi.notesnap.notesnap.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.data.daos.NoteDao
import fi.notesnap.notesnap.data.state.FolderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderViewModel(
    dao: NoteDao,
    folderId: Long,
) : ViewModel() {
    val state = MutableStateFlow(FolderState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val notesLiveData = dao.getNotesByFolder(folderId)

            withContext(Dispatchers.Main) {
                notesLiveData.observeForever { notes ->
                    state.value = state.value.copy(notes = notes)
                }
            }
        }
    }
}

