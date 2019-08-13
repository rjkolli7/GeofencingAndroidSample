package com.rjkolli.gfsample.di.module

import com.rjkolli.gfsample.GeofenceApp
import com.rjkolli.gfsample.helper.SharedPreferenceHelper
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Provides
    @Singleton
    fun providesSharedPrefernces(@Named("app") app: GeofenceApp) = SharedPreferenceHelper(app)

}