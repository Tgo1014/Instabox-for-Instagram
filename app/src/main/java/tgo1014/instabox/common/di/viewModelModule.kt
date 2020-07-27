package tgo1014.instabox.common.di

import kotlinx.coroutines.Dispatchers
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module
import tgo1014.instabox.common.CoroutinesDispatcherProvider
import tgo1014.instabox.feed.FeedViewModel
import tgo1014.instabox.login.LoginViewModel
import tgo1014.instabox.main.MainViewModel
import tgo1014.instabox.pickpicture.PickPictureViewModel

val viewModelModule = module {
    viewModel<PickPictureViewModel>()
    viewModel<LoginViewModel>()
    viewModel<FeedViewModel>()
    viewModel<MainViewModel>()
    single { CoroutinesDispatcherProvider(Dispatchers.Main, Dispatchers.Default, Dispatchers.IO) }
}