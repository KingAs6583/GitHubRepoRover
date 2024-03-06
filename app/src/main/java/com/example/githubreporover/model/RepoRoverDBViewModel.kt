package com.example.githubreporover.model

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.githubreporover.data.Repo
import com.example.githubreporover.data.RepoEntity
import com.example.githubreporover.database.RepoRoverDao
import kotlinx.coroutines.launch


/**
 * View Model to keep a reference to the Links Url repository and an up-to-date list of all items.
 */
class RepoRoverDBViewModel(private val repoRoverDao: RepoRoverDao) : ViewModel() {

    private fun insertRepo(repoEntity: RepoEntity) {
        viewModelScope.launch {
            repoRoverDao.insert(repoEntity)
        }
    }

    private fun deleteAll(){
        viewModelScope.launch {
            repoRoverDao.deleteAllCacheRepos()
        }
    }

    private fun getAllRepo() : LiveData<List<RepoEntity>>{
        return repoRoverDao.getAllCacheRepos().asLiveData()

    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun insertCacheRepo(repoEntity: RepoEntity){
        insertRepo(repoEntity)
    }

    fun deleteAllCacheRepo(){
        deleteAll()
    }

    fun getAllCacheRepo(): LiveData<List<RepoEntity>>{
        return getAllRepo()
    }

}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class RepoRoverDBViewModelFactory(private val repoRoverDao: RepoRoverDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepoRoverDBViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RepoRoverDBViewModel(repoRoverDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
