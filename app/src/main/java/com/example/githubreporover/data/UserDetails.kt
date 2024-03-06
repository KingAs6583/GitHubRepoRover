package com.example.githubreporover.data

data class UserDetails(
    val login: String?,
    val id: Long,
    val avatarUrl: String?,
    val name: String?,
    val bio: String?
)

data class StaredUsers(
    val login: String,
    val avatarUrl: String
)