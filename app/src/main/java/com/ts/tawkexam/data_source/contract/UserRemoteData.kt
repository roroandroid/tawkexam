package com.ts.tawkexam.data_source.contract

import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.data_source.remote.UserListApi
import io.reactivex.Single

class UserRemoteData (private val api: UserListApi) : UserDataContract.Remote {

    override fun getUsersInRemote(): Single<List<User>> = api.queryUsers(null)
    override fun getUserByIdInRemote(name: String?): Single<User> = api.getUser(name)

//    override fun getUsers() = postService.getUsers()
//
//    override fun getPosts() = postService.getPosts()
}