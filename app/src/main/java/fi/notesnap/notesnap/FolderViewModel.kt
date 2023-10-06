package fi.notesnap.notesnap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.notesnap.notesnap.daos.NoteDao
import fi.notesnap.notesnap.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

