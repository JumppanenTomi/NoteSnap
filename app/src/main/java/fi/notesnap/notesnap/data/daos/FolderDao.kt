package fi.notesnap.notesnap.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fi.notesnap.notesnap.data.entities.Folder

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders")
    fun getAllFolders(): LiveData<List<Folder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Update
    suspend fun updateFolder(folder: Folder)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderById(folderId: Long?): LiveData<Folder>


    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}