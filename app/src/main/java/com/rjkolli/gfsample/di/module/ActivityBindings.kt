package com.rjkolli.gfsample.di.module

import com.rjkolli.gfsample.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class ActivityBindings {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeAuthenticationActivity(): MainActivity
}
