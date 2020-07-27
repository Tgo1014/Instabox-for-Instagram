package tgo1014.instabox

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import tgo1014.instabox.common.di.interactorModule
import tgo1014.instabox.common.di.networkModule
import tgo1014.instabox.common.di.storageModule
import tgo1014.instabox.common.di.viewModelModule
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
                Timber.plant(Timber.DebugTree())
            }
            androidContext(this@App)
            modules(viewModelModule, networkModule, storageModule, interactorModule)
        }
    }

}