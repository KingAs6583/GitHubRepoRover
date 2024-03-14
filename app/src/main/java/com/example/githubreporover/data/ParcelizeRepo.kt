package com.example.githubreporover.data


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ParcelizeRepo(
    val id: Long,
    val name: String,
    val owner: @RawValue Owner?,
    val htmlUrl: String,
    val description: String?,
    val cloneUrl: String,
    val license: @RawValue License?,
    val language: String?,
    val visibility: String
) : Parcelable {
    fun getRepo(): Repo {
        return Repo(
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

    fun getRepoEntity(): RepoEntity {
        return RepoEntity(
            id = id,
            name = name,
            ownerName = owner?.login,
            htmlUrl = htmlUrl,
            description = description,
            cloneUrl = cloneUrl,
            license = license?.name,
            language = language,
            visibility = visibility,
        )
    }
}

@Parcelize
data class ParcelizeUserDetails(
    val login: String?,
    val id: Long,
    val avatarUrl: String?,
    val name: String?,
    val bio: String?
) : Parcelable

