package com.ts.tawkexam.data_source.remote

import com.ts.tawkexam.data_source.model.User
import io.reactivex.Single
import retrofit2.http.*

interface UserListApi {

    @GET("/users")
    fun queryUsers(
        @Query("since") since: Int?
    ): Single<List<User>>

    @GET("/users/{name}")
    fun getUser(@Path("name") name: String?): Single<User>

}