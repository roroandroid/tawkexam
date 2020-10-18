package com.ts.tawkexam

import com.ts.tawkexam.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree

class ApplicationClass : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder().application(this).build()
    }


    override fun onCreate() {
        super.onCreate()
            Timber.plant(DebugTree())
    }


}