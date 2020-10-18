package com.ts.tawkexam.di

import android.app.Application
import com.ts.tawkexam.ApplicationClass
import com.ts.tawkexam.di.api.UserListApiModule
import com.ts.tawkexam.di.core.*
import com.ts.tawkexam.di.core.ContextModule
import com.ts.tawkexam.di.network.NetworkModule
import com.ts.tawkexam.di.repo.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules = [AndroidInjectionModule::class,
        NetworkModule::class,
        ContextModule::class,
        RepositoryModule::class,
        BindingModule::class,
        UserListApiModule::class,
        CoreModule::class]
)
interface ApplicationComponent : AndroidInjector<ApplicationClass> {
    @Component.Builder
    interface Builder {


        @BindsInstance
        fun application(application: Application): ApplicationComponent.Builder

        fun build(): ApplicationComponent
    }
}