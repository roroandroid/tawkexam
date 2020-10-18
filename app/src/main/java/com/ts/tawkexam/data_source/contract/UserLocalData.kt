package com.ts.tawkexam.data_source.contract

import android.util.Log
import androidx.paging.PagingSource
import com.ts.tawkexam.base.Scheduler
import com.ts.tawkexam.data_source.local.UserDatabase
import com.ts.tawkexam.data_source.model.User
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class UserLocalData(private val userDb: UserDatabase, private val scheduler: Scheduler) :
    UserDataContract.Local {
    override fun getAllUsers(): PagingSource<Int, User> {
        return userDb.userDao().getAllUsers()
    }


    override fun addUsers(users: List<User>) {
        Completable.fromAction {
            userDb.userDao().upsertAll(users)
        }.subscribeOn(scheduler.io())
            .subscribe {
                Log.e("Complete", " Add Users")
            }
    }

    override fun getUsersInLocalWithKeyword(searchText: String): Single<List<User>> {
        Timber.e("getUsersInLocalWithKeyword $searchText")
        val keyword = "%$searchText%"
        return userDb.userDao().searchUserByKeyword(keyword)

    }

    override fun updateNoteInLocal(id: Int, note: String) {
        Completable.fromAction {
            userDb.userDao().updateNoteInDb(id, note)
        }.subscribeOn(scheduler.io())
            .subscribe {
                Log.e("Complete", " Update Post")
            }
    }

//    override fun getCommentsForPost(postId: Int): Flowable<List<Comment>> {
//        return postDb.commentDao().getForPost(postId)
//    }
//
//    override fun saveComments(comments: List<Comment>) {
//        Completable.fromAction {
//            postDb.commentDao().upsertAll(comments)
//        }
//            .performOnBack(scheduler)
//            .subscribe()
//    }
}