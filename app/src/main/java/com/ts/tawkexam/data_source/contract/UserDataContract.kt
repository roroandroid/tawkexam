package com.ts.tawkexam.data_source.contract

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.ts.tawkexam.base.Outcome
import com.ts.tawkexam.data_source.model.User
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class UserDataContract {
    interface Repository {
        fun getAllUsers(): Flowable<PagingData<User>>
        val userListFetchOutcome: PublishSubject<Outcome<List<User>>>
        fun handleError(error: Throwable)
        fun fetchUsersInLocalWithKeyword(searchText: String)
        fun updateNoteInLocalWithId(id: Int, text: String)
    }

    interface Local {
        fun getAllUsers(): PagingSource<Int, User>
        fun addUsers(users: List<User>)
        fun getUsersInLocalWithKeyword(searchText: String): Single<List<User>>
        fun updateNoteInLocal(id: Int, note: String)
    }

    interface Remote {
        fun getUsersInRemote(): Single<List<User>>
        fun getUserByIdInRemote(name: String?): Single<User>
    }
}