package com.example.githubreporover.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubreporover.data.PublicRepo
import com.example.githubreporover.data.Repo
import com.example.githubreporover.network.GitHubAPI
import kotlinx.coroutines.launch

enum class GitHubApiStatus { LOADING, ERROR, DONE }


class RepoRoverViewModel : ViewModel() {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<GitHubApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<GitHubApiStatus> = _status

    private val _repo = MutableLiveData<ArrayList<Repo>>()
    val repo: LiveData<ArrayList<Repo>> = _repo

    private val _publicRepo = MutableLiveData<PublicRepo>()
    val publicRepo: LiveData<PublicRepo> = _publicRepo

    private val _starsCount = MutableLiveData<Int>()
    val starsCount = _starsCount

    var searchText : String? =  null

    fun getReposByUserName(userName: String) {
        viewModelScope.launch {
            _status.value = GitHubApiStatus.LOADING
            try {
                _repo.value = GitHubAPI.retrofitService.getPublicReposByUserName(userName)
                _status.value = GitHubApiStatus.DONE
            } catch (e: Exception) {
                _status.value = GitHubApiStatus.ERROR
                _repo.value = ArrayList()
            }
        }
    }

    fun findPublicRepo(query: String) {
        viewModelScope.launch {
            _status.value = GitHubApiStatus.LOADING
            try {
                _publicRepo.value = GitHubAPI.retrofitService.searchPublicRepos(query)
                _status.value = GitHubApiStatus.DONE
            } catch (e: Exception) {
                _status.value = GitHubApiStatus.ERROR
                e.printStackTrace()
            }
        }
    }

    fun getStarsCount(owner: String,repoName: String){
        viewModelScope.launch {
            _status.value = GitHubApiStatus.LOADING
            try {
                starsCount.value = GitHubAPI.retrofitService.getStargazers(owner,repoName).size
                _status.value = GitHubApiStatus.DONE
            } catch (e: Exception) {
                _status.value = GitHubApiStatus.ERROR
                starsCount.value = 0
                e.printStackTrace()
            }
        }
    }

}