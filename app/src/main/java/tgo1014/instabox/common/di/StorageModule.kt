package tgo1014.instabox.common.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tgo1014.instabox.common.managers.CookieManager
import tgo1014.instabox.common.managers.CookieManagerImpl
import tgo1014.instabox.common.managers.UserManager
import tgo1014.instabox.common.managers.UserManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    abstract fun bindCookieManager(cookieManagerImpl: CookieManagerImpl): CookieManager

    @Binds
    abstract fun bindUserManager(userManagerImpl: UserManagerImpl): UserManager

    companion object {

        @Singleton
        @Provides
        fun providesCookieManager(): android.webkit.CookieManager {
            return android.webkit.CookieManager.getInstance()
        }

        @Singleton
        @Provides
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
            // val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            // return EncryptedSharedPreferences.create(
            //     "box",
            //     masterKeyAlias,
            //     context.applicationContext,
            //     EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            //     EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        }
    }
}