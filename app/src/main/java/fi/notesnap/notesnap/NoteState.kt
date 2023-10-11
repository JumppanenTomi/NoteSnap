package fi.notesnap.notesnap

data class NoteState(
    var title: String = "",
    var content: String = "",
    var locked: Boolean = false,
    var folderId: Long? = 0
)
