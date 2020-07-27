package tgo1014.instabox.common.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import tgo1014.instabox.common.managers.*

val storageModule = module {
    single { android.webkit.CookieManager.getInstance() }
    single { provideSharedPreferences(get()) }
    single<AdManager> { (activity: Activity) -> AdManagerImpl(activity) }
    singleBy<UserManager, UserManagerImpl>()
    singleBy<CookieManager, CookieManagerImpl>()
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
//    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//    return EncryptedSharedPreferences.create(
//        "box",
//        masterKeyAlias,
//        context.applicationContext,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )
}