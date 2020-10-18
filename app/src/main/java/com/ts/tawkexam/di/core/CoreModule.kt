package com.ts.tawkexam.di.core

import com.ts.tawkexam.base.AppScheduler
import com.ts.tawkexam.base.Scheduler
import dagger.Module
import dagger.Provides

@Module
class CoreModule {

    @Provides
    fun scheduler(): Scheduler {
        return AppScheduler()
    }
}