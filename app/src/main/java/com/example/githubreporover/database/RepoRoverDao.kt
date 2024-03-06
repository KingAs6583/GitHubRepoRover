package com.example.githubreporover.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubreporover.data.RepoEntity
import com.example.githubreporover.data.User
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoRoverDao {
    /**
     *The database operations can take a long time to execute, so they should run on a separate thread.
     * Make the function a suspend function, so that this function can be called from a coroutine.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repoEntity: RepoEntity)

    @Query("DELETE FROM Repositories")
    suspend fun deleteAllCacheRepos() : Int

    @Query("SELECT * FROM Repositories")
    fun getAllCacheRepos() : Flow<List<RepoEntity>>

}