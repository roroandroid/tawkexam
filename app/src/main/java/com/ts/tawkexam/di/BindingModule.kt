package com.ts.tawkexam.di

import com.ts.tawkexam.MainActivity
import com.ts.tawkexam.di.api.UserListApiModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BindingModule {

    @ContributesAndroidInjector(
        modules = [UserListApiModule::class,
            UserListFragmentBuildersModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}
