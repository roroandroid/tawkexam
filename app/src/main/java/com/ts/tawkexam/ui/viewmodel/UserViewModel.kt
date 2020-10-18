package com.ts.tawkexam.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.rxjava2.cachedIn
import com.ts.tawkexam.base.Outcome
import com.ts.tawkexam.data_source.model.User
import com.ts.tawkexam.data_source.contract.UserDataContract
import com.ts.tawkexam.utils.toLiveData
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class UserViewModel(
    private val repository: UserDataContract.Repository,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    fun getAllUsers(): Flowable<PagingData<User>> {
        return repository
            .getAllUsers()
            .cachedIn(viewModelScope)
    }

    val userListSearchOutcome: LiveData<Outcome<List<User>>> by lazy {
        //Convert publish subject to livedata
        repository.userListFetchOutcome.toLiveData(compositeDisposable)
    }

    fun updateNote(id: Int, text: String) {
        repository.updateNoteInLocalWithId(id, text)
    }
    fun searchUserWithKeyword(searchText: String) {
        repository.fetchUsersInLocalWithKeyword(searchText)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}