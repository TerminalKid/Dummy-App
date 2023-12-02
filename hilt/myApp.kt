package hilt

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import okhttp3.Call


//@Module
//@InstallIn(SingletonComponent::class)
//@HiltAndroidApp
class myApp : Application(),ImageLoaderFactory {


    companion object{

        lateinit var appModule: appModule

    }


    override fun onCreate() {
        super.onCreate()
        appModule = appMooduleImp()

    }

    override fun newImageLoader(): ImageLoader{

        return ImageLoader(this).newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache{

                MemoryCache.Builder(this)
                    .maxSizePercent(0.5)
                    .strongReferencesEnabled(true)
                    .build()

            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache{

                DiskCache.Builder()
                    .maxSizePercent(0.1)
                    .directory(cacheDir)
                    .build()

            }
            .logger(DebugLogger())
            .build()
    }


}



