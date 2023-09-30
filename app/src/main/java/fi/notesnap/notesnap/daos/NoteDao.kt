package fi.notesnap.notesnap.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.notesnap.notesnap.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE FolderId = :folderId")
    fun getNotesByFolder(folderId: Long): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)
}