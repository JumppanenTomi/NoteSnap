package fi.notesnap.notesnap.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import fi.notesnap.notesnap.entities.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE FolderId = :folderId")
    fun getNotesByFolder(folderId: Long): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Long?): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Upsert
    suspend fun upsertNote(note: Note)
}