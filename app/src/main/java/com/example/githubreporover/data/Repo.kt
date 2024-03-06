package com.example.githubreporover.data

import com.google.gson.annotations.SerializedName

//https://github1s.com/ in vs code

data class Repo(
    val id: Long,
    val name: String,
    val owner: Owner?,
    @SerializedName("html_url")
    val htmlUrl: String,
    var description: String?,
    @SerializedName("clone_url")
    val cloneUrl: String,
    val license: License?,
    val language: String?,
    var visibility: String,
){
    fun getParcelizeRepo(): ParcelizeRepo{
        return ParcelizeRepo(
            id = id,
            name = name,
            owner = owner,
            htmlUrl = htmlUrl,
            description = description,
            cloneUrl = cloneUrl,
            license = license,
            language = language,
            visibility = visibility
        )
    }
    fun getRepoEntity() : RepoEntity{
        return RepoEntity(
            id = id,
            name = name,
            ownerName = owner?.login,
            htmlUrl = htmlUrl,
            description = description,
            cloneUrl = cloneUrl,
            license = license?.name,
            language = language,
            visibility = visibility
        )
    }
}

data class UserRepo(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("clone_url")
    val cloneUrl: String,
    val visibility: String,
    val language: String
)

data class License(
    val name: String?,
    val url: String?
)

data class Owner(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)

data class PublicRepo(
    val items : ArrayList<Repo>
)