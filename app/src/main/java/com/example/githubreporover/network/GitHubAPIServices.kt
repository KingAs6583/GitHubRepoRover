package com.example.githubreporover.network

import com.example.githubreporover.data.PublicRepo
import com.example.githubreporover.data.Repo
import com.example.githubreporover.data.StaredUsers
import com.example.githubreporover.data.UserDetails
import com.example.githubreporover.data.UserRepo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.github.com"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    // on below line we are calling add
    // Converter factory as Gson converter factory.
    // at last we are building our retrofit builder.
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface GitHubAPIServices {

    @GET("/user")
    suspend fun getUserDetails(
        @Header("Authorization") token: String
    ): UserDetails

    @GET("/repos")
    suspend fun getUserRepos(): ArrayList<UserRepo>

    @GET("/repos/{owner}/{repo}/stargazers")
    suspend fun getStargazers(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<StaredUsers>


    @GET("/search/repositories")
    suspend fun searchPublicRepos(
        @Query("q") query: String
    ): PublicRepo

    @GET("/users/{user_name}")
    suspend fun getUserDetailsByUserName(
        @Path("user_name") userName: String
    ): UserDetails

    @GET("/users/{user_name}/repos")
    suspend fun getPublicReposByUserName(
        @Path("user_name") userName: String
    ): ArrayList<Repo>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object GitHubAPI {
    val retrofitService: GitHubAPIServices by lazy { retrofit.create(GitHubAPIServices::class.java) }
}