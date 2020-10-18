package com.ts.tawkexam.di.core

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
internal abstract class ContextModule {
    @Binds // @Binds, binds the Application instance to Context
    abstract fun bindContext(application: Application?): Context?

}
