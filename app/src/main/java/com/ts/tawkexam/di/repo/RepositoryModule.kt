package com.ts.tawkexam.di.repo

import android.content.Context
import com.ts.tawkexam.base.Scheduler
import com.ts.tawkexam.data_source.local.UserDatabase
import com.ts.tawkexam.data_source.remote.UserListApi
import com.ts.tawkexam.data_source.UserRepository
import com.ts.tawkexam.data_source.contract.UserDataContract
import com.ts.tawkexam.data_source.contract.UserLocalData
import com.ts.tawkexam.data_source.contract.UserRemoteData
import com.ts.tawkexam.data_source.remote.UserListRemoteMediator
import com.ts.tawkexam.ui.viewmodel.UserViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class RepositoryModule {

    @Provides
    fun userDb(context: Context): UserDatabase = UserDatabase.getInstance(context)

    @Provides
    fun userViewModelFactory(repository: UserDataContract.Repository,compositeDisposable: CompositeDisposable): UserViewModelFactory
            = UserViewModelFactory(repository,compositeDisposable)

    @Provides
    fun userRepo(local: UserDataContract.Local, remoteMediator: UserListRemoteMediator,
                 compositeDisposable: CompositeDisposable): UserDataContract.Repository
            = UserRepository(
        local,
        remoteMediator,
        compositeDisposable
    )

    @Provides
    fun remoteData(api: UserListApi): UserDataContract.Remote = UserRemoteData(api)

    @Provides
    fun localData(userDb: UserDatabase, scheduler: Scheduler): UserDataContract.Local = UserLocalData(userDb, scheduler)


    @Provides
    fun remoteMediator(userDb: UserDatabase, api: UserListApi): UserListRemoteMediator =
        UserListRemoteMediator(userDb, api)

    @Provides
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()
}