package com.example.githubreporover

import android.app.Application
import com.example.githubreporover.database.RepoRoverRoomDatabase
import com.google.firebase.FirebaseApp

class GitHubRepoRoverApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    val database: RepoRoverRoomDatabase by lazy {
        RepoRoverRoomDatabase.getDatabase(
            this
        )
    }
}