package com.ts.tawkexam.di

import com.ts.tawkexam.ui.profile.ProfileView
import com.ts.tawkexam.ui.user_list.main.UserListView
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UserListFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeUserListFragment(): UserListView

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileView


}