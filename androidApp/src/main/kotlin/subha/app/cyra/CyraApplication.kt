package subha.app.cyra

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import subha.app.cyra.core.di.androidPlatformModule
import subha.app.cyra.core.di.initKoin

class CyraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(androidPlatformModule) {
            androidLogger()
            androidContext(this@CyraApplication)
        }
    }
}
