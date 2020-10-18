package com.ts.tawkexam.di.api

import com.ts.tawkexam.data_source.remote.UserListApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class UserListApiModule {
    @Provides
    fun provideUserListApi(retrofit: Retrofit): UserListApi {
        return retrofit.create<UserListApi>(
            UserListApi::class.java)
    }
}
