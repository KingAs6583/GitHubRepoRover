package com.example.githubreporover.model

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.githubreporover.data.UserDetails
import com.example.githubreporover.network.GitHubAPI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel(activity: Activity) : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<GitHubApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<GitHubApiStatus> = _status

    private val _sharedPreferences: SharedPreferences = activity.getSharedPreferences(
        "com.example.githubreporover.setting",
        Context.MODE_PRIVATE
    )
    val sharedPreferences = _sharedPreferences

    private  val _auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val currentUserDetails = _auth

    private  val _editor:SharedPreferences.Editor = sharedPreferences.edit()
    val edit = _editor

    val githubUserName = sharedPreferences.getString("UserId","")

    private val _userDetails = MutableLiveData<UserDetails>()
    val userDetails: LiveData<UserDetails> = _userDetails


    fun getUserDetails(userName: String){
        viewModelScope.launch {
            _status.value = GitHubApiStatus.LOADING
            try {
                _userDetails.value = GitHubAPI.retrofitService.getUserDetailsByUserName(userName)
                _status.value = GitHubApiStatus.DONE
            }catch (e: Exception){
                _status.value = GitHubApiStatus.ERROR
            }
        }
    }

}

/**
 * To handle LoginViewModel args we require ViewModelFactory
 */
class LoginViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
