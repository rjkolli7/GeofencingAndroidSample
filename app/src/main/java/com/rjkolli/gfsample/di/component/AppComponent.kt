package com.rjkolli.gfsample.di.component

import com.rjkolli.gfsample.GeofenceApp
import com.rjkolli.gfsample.di.module.ActivityBindings
import com.rjkolli.gfsample.di.module.AppModule
import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class,
        AppModule::class,
        ActivityBindings::class]
)
interface AppComponent {

    fun getPreferences() : SharedPreferenceHelper

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(@Named("app")application: GeofenceApp): Builder

        fun build(): AppComponent
    }

    fun inject(app: GeofenceApp)
}