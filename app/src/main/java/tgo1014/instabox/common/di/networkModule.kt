package tgo1014.instabox.common.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.common.managers.UserManager
import tgo1014.instabox.common.network.ClarifaiApi
import tgo1014.instabox.common.network.InstagramApi
import tgo1014.instabox.common.utils.InstagramConstants

private const val RETROFIT_CLARIFAI = "CLARIFAI_RETROFIT"
private const val RETROFIT_INSTAGRAM = "INSTAGRAM_RETROFIT"

val networkModule = module {
    single { provideMoshi() }
    single { provideOkHttpClient(get()) }
    single(named(RETROFIT_CLARIFAI)) { provideClarifaiRetrofit(get(), get()) }
    single(named(RETROFIT_INSTAGRAM)) { provideInstagramRetrofit(get(), get()) }
    single { provideInstagramApi(get(named(RETROFIT_INSTAGRAM))) }
    single { provideClarifaiApi(get(named(RETROFIT_CLARIFAI))) }
}

private fun provideInstagramApi(retrofit: Retrofit) = retrofit.create<InstagramApi>()
private fun provideClarifaiApi(retrofit: Retrofit) = retrofit.create<ClarifaiApi>()

private fun provideOkHttpClient(userManager: UserManager) = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .header("Cookie", userManager.getFormattedUserAgent())
            .header(
                "User-Agent",
                InstagramConstants.USER_AGENT
            )
            .build()
        chain.proceed(request)
    }
    .apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }
    .build()

private fun provideMoshi() = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private fun provideClarifaiRetrofit(okHttpClient: OkHttpClient, moshi: Moshi) = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(BuildConfig.BASE_CLARIFAI_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

private fun provideInstagramRetrofit(okHttpClient: OkHttpClient, moshi: Moshi) = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(BuildConfig.BASE_INSTAGRAM_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()