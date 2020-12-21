package tgo1014.instabox.common.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.common.managers.UserManager
import tgo1014.instabox.common.network.ClarifaiApi
import tgo1014.instabox.common.network.InstagramApi
import tgo1014.instabox.common.utils.InstagramConstants
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ClarifaiRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class InstagramRetrofit

    @Singleton
    @Provides
    fun provideInstagramApi(@InstagramRetrofit retrofit: Retrofit) = retrofit.create<InstagramApi>()

    @Singleton
    @Provides
    fun provideClarifaiApi(@ClarifaiRetrofit retrofit: Retrofit) = retrofit.create<ClarifaiApi>()

    @Singleton
    @Provides
    fun provideOkHttpClient(userManager: UserManager) = OkHttpClient.Builder()
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

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    @ClarifaiRetrofit
    fun provideClarifaiRetrofit(okHttpClient: OkHttpClient, moshi: Moshi) =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_CLARIFAI_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Singleton
    @Provides
    @InstagramRetrofit
    fun provideInstagramRetrofit(okHttpClient: OkHttpClient, moshi: Moshi) =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_INSTAGRAM_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
}
