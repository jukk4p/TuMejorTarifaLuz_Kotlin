package com.tumejortarifaluz

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder

@HiltAndroidApp
class TuMejorTarifaLuzApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        // Enable Crashlytics crash reporting
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}
