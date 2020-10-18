package com.ts.tawkexam.data_source.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.ts.tawkexam.data_source.local.UserDatabase
import com.ts.tawkexam.data_source.model.User
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class UserListRemoteMediator(
    private val userDb: UserDatabase,
    private val userListApi: UserListApi
) : RxRemoteMediator<Int, User>() {

    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, User>
    ): Single<MediatorResult> {
        Timber.e("Load Type: " + loadType);
        val x = Single.just(loadType)
            .subscribeOn(Schedulers.io())
            .map {
                when (it) {
                    /*
                     If database is already populated, do not reload DB. As posts will be overriden
                     Could implement this if posts are stored in a separate table
                     */
                    LoadType.REFRESH -> {
                        val key = getRemoteKeyForFirstItem() ?: 0
                        if (key > 0) -1 else key
                    }
                    /*
                     List does not handle PREPEND
                    */
                    LoadType.PREPEND -> -1
                    /*
                    Load new data
                     */
                    LoadType.APPEND -> {
                        val id = getRemoteKeyForLastItem(state)
                        id ?: 0
                    }
                }
            }.flatMap { id ->
                if (id == -1) {
                    Single.just(MediatorResult.Success(endOfPaginationReached = true))
                } else {
                    /*
                    RxJava code to first query list of users then iteratively query the user's individual information using name.login
                     */
                    userListApi.queryUsers(
                        since = id
                    ).flattenAsObservable { it }
                        .flatMapSingle { getUserObservable(it) }
                        .toList()
                        .map { insertToDb(loadType, it) }
                        .map<MediatorResult> {
                            MediatorResult.Success(endOfPaginationReached = false)
                        }
                        .onErrorReturn {
                            MediatorResult.Error(it)
                        }
                }
            }
            .onErrorReturn {
                MediatorResult.Error(it)
            }

        return x
    }


    private fun getUserObservable(it: User): Single<User> {
        return userListApi.getUser(it.login)
    }


    private fun insertToDb(loadType: LoadType, data: List<User>): List<User> {
        /*
        Insert or replace data based on PK
         */
        userDb.userDao().upsertAll(data)
        return data
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, User>): Int? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { user ->
            userDb.userDao().getLastId()
        }
    }

    private fun getRemoteKeyForFirstItem(): Int? {
        return userDb.userDao().getFirstId()
    }

    //For Prepend, not used
    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.login?.let { login ->
                userDb.userDao().getFirstId()
            }
        }
    }

    companion object {
        const val INVALID_ID = -1
    }
}