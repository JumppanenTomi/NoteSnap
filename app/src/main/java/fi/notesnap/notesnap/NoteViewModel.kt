    package fi.notesnap.notesnap


    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import fi.notesnap.notesnap.daos.NoteDao
    import fi.notesnap.notesnap.entities.Note
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch

    class NoteViewModel(
        private val noteDao: NoteDao,
        ): ViewModel() {

        var state = MutableStateFlow(NoteState())

        fun onEvent(event: NoteEvent, noteId: Long? = null) {
            when(event){
                is NoteEvent.SaveNote -> {
                    val title = state.value.title
                    val content = state.value.content
                    val locked = state.value.locked
                    val folderId = state.value.folderId
/*
                    val note = Note(
                        title = title,
                        content = content,
                        locked = locked,
                        folderId = 1
                    )
 */
                    viewModelScope.launch {
                        //noteDao.upsertNote(note)
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
                is NoteEvent.UpdateState->{

                    viewModelScope.launch(Dispatchers.IO)
                    {
                        val note = ( noteDao.getNoteById(noteId))
                        if (note!= null) {
                            val updatedState = state.value.copy(
                                //title = note.title,
                                //content = note.content,
                                //locked = note.locked,
                                //folderId = note.folderId
                            )

                            state.value = updatedState
                        }
                        else{
                            println("Note is Null")
                        }
                    }
                    }
                is NoteEvent.EmptyState->{
                    val cleanState = state.value.copy(
                        title = "",
                        content = "",
                        locked = false,
                        folderId = 1
                    )
                    state.value = cleanState

                    println("Empty State")
                    print(state)
                }
                }
            }
        }
