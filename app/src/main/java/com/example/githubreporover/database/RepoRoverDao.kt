package com.example.githubreporover.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.githubreporover.data.RepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoRoverDao {
    /**
     *The database operations can take a long time to execute, so they should run on a separate thread.
     * Make the function a suspend function, so that this function can be called from a coroutine.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repoEntity: RepoEntity)

    @Delete
    suspend fun delete(repoEntity: RepoEntity)

    @Update
    suspend fun update(repoEntity: RepoEntity)

    @Query("DELETE FROM Repositories")
    suspend fun deleteAllCacheRepos() : Int

    @Query("SELECT * FROM Repositories WHERE is_fav_repo = 0")
    fun getAllCacheRepos() : Flow<List<RepoEntity>>

    @Query("SELECT * FROM Repositories WHERE is_fav_repo = 1")
    fun getAllFavRepo() : Flow<List<RepoEntity>>

    @Query("DELETE FROM Repositories WHERE is_fav_repo = 0")
    suspend fun deleteNonFavRepo()

    @Query("Select * From Repositories Where is_fav_repo = 1 AND name Like :searchQuery")
    fun searchFavRepo(searchQuery: String): Flow<List<RepoEntity>>

}