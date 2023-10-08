package fi.notesnap.notesnap.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import fi.notesnap.notesnap.entities.Folder

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders")
    fun getAllFolders(): LiveData<List<Folder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderById(folderId: Long): LiveData<Folder?>

    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}