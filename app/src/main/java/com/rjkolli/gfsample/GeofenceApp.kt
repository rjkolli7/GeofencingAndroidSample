package com.rjkolli.gfsample

import android.app.Activity
import android.app.Application
import com.rjkolli.gfsample.di.injector.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class GeofenceApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): DispatchingAndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }
}