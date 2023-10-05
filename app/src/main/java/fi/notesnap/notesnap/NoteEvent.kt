package fi.notesnap.notesnap

interface NoteEvent {
    object SaveNote:NoteEvent
    data class SetTitle(val title: String) : NoteEvent
    data class SetContent(val content: String) : NoteEvent
    data class SetLocked(val locked: Boolean) : NoteEvent
    data class SetFolderId (val folderId: Long) : NoteEvent

}