package fi.notesnap.notesnap.data.state

data class NoteState(
    var title: String = "",
    var content: String = "",
    var locked: Boolean = false,
    val folderId: Long? = 0
)
