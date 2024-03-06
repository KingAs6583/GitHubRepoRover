package com.example.githubreporover.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.githubreporover.data.RepoEntity
import com.example.githubreporover.data.User

@Database(
    entities = [RepoEntity::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class RepoRoverRoomDatabase : RoomDatabase() {

    abstract fun repoRoverDao(): RepoRoverDao

    /**
     * Annotate INSTANCE with @Volatile.
     * The value of a volatile variable will never be cached, and all writes and reads will be done to and from the main memory.
     * This helps make sure the value of INSTANCE is always up-to-date and the same for all execution threads.
     * It means that changes made by one thread to INSTANCE are visible to all other threads immediately.
     */
    companion object {
        /**
         * The INSTANCE variable will keep a reference to the database, when one has been created.
         * This helps in maintaining a single instance of the database opened at a given time, which is an expensive resource to create and maintain.
         */
        @Volatile
        private var INSTANCE: RepoRoverRoomDatabase? = null
        fun getDatabase(context: Context): RepoRoverRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RepoRoverRoomDatabase::class.java,
                    "RepoRover_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                /**
                 * Multiple threads can potentially run into a race condition and ask for a database instance at the same time,
                 * resulting in two databases instead of one. Wrapping the code to get the database inside
                 * a synchronized block means that only one thread of execution at a time can enter this block of code,
                 * which makes sure the database only gets initialized once.
                 */
                INSTANCE = instance
                return instance
            }
        }
    }
}