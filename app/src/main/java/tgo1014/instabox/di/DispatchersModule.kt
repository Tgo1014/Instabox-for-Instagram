package tgo1014.instabox.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    fun providesDispatchers() =
        CoroutinesDispatcherProvider(Dispatchers.Main, Dispatchers.Default, Dispatchers.IO)
}