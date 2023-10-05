    package fi.notesnap.notesnap.entities


    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import fi.notesnap.notesnap.NoteEvent
    import fi.notesnap.notesnap.NoteState
    import fi.notesnap.notesnap.daos.FolderDao
    import fi.notesnap.notesnap.daos.NoteDao
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch

    class NoteViewModel(
        private val noteDao: NoteDao,


        ): ViewModel() {
        val state = MutableStateFlow(NoteState())

        fun onEvent(event: NoteEvent) {
            when(event){
                is NoteEvent.SaveNote -> {
                    val title = state.value.title
                    val content = state.value.content
                    val locked = state.value.locked
                    val folderId = state.value.folderId

                    val note = Note(
                        title = title,
                        content = content,
                        locked = locked,
                        folderId = folderId
                    )
                    viewModelScope.launch {
                        noteDao.insertNote(note)
                    }

                }
                is NoteEvent.SetTitle -> {
                    state.update {
                        it.copy(title = event.title)
                    }
                }
                is NoteEvent.SetContent -> {
                    state.update{
                        it.copy(content = event.content)
                    }
                }
                is NoteEvent.SetLocked ->{
                    state.update {
                        it.copy(locked = event.locked)
                    }
                }
                is NoteEvent.SetFolderId ->{
                    state.update {
                        it.copy(folderId = event.folderId)
                    }
                }
            }
        }
    }