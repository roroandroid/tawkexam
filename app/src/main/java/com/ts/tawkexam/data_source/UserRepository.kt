package com.ts.tawkexam.data_source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.ts.tawkexam.base.Outcome
import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.data_source.contract.UserDataContract
import com.ts.tawkexam.data_source.remote.UserListRemoteMediator
import com.ts.tawkexam.utils.failed
import com.ts.tawkexam.utils.success
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class UserRepository(
    private val local: UserDataContract.Local,
    private val remoteMediator: UserListRemoteMediator,
    private val compositeDisposable: CompositeDisposable
) : UserDataContract.Repository {

    override fun getAllUsers(): Flowable<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                local.getAllUsers()
            }
        ).flowable
    }



    override val userListFetchOutcome: PublishSubject<Outcome<List<User>>> =
        PublishSubject.create<Outcome<List<User>>>()


    override fun fetchUsersInLocalWithKeyword(searchText: String) {
        val fetchUsersInLocalWithKeyword = local.getUsersInLocalWithKeyword(searchText)
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe({ users ->
                when (users.size) {
                    0 -> {
                        userListFetchOutcome.failed(Throwable("No users found with name"))
                    }
                    else -> {
                        Timber.e("Returned users $users")
                        userListFetchOutcome.success(users)
                    }
                }
            }, { error ->
                handleError(error)
            })
        compositeDisposable.add(fetchUsersInLocalWithKeyword)
    }

    override fun updateNoteInLocalWithId(id: Int, text: String) {
        local.updateNoteInLocal(id, text)
    }


    override fun handleError(error: Throwable) {
        Timber.e("Error $error")
    }

}