package com.example.githubreporover.data

import androidx.room.*

@Entity("Repositories")
data class RepoEntity(
    @PrimaryKey(autoGenerate = false) val id: Long,
    val name: String,
    @ColumnInfo(name = "owner_name")
    val ownerName: String?,
    @ColumnInfo(name = "html_url")
    val htmlUrl: String,
    var description: String?,
    @ColumnInfo(name = "clone_url")
    val cloneUrl: String,
    val license: String?,
    val language: String?,
    val visibility: String,

){
    fun getRepo(): Repo{
        return Repo(
            id = id,
            name = name,
            owner = Owner(login = ownerName!!, avatarUrl = ""),
            htmlUrl = htmlUrl,
            description = description,
            cloneUrl = cloneUrl,
            license = License(name = license, url = ""),
            language = language,
            visibility = visibility
        )
    }
}

@Entity
data class User(@PrimaryKey @ColumnInfo(name="name") val name : String)