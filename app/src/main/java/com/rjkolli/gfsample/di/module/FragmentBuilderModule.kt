package com.rjkolli.gfsample.di.module

import com.rjkolli.gfsample.ui.main.MainFragment
import com.rjkolli.gfsample.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment
}