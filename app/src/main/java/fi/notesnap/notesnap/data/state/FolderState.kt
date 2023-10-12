package fi.notesnap.notesnap.data.state

import fi.notesnap.notesnap.data.entities.Note

data class FolderState(
    var notes: List<Note> = emptyList(),
    val title: String = "",
    val content: String = "",
    val folderId: Long = 0,
    val locked: Boolean = false
)