package tgo1014.instabox.common.di

import org.koin.dsl.module
import tgo1014.instabox.feed.interactors.ActionOnFeedItemInteractor
import tgo1014.instabox.feed.interactors.GetArchivedPhotosInteractor
import tgo1014.instabox.feed.interactors.GetSelfFeedInteractor

val interactorModule = module {
    single { GetArchivedPhotosInteractor(get()) }
    single { ActionOnFeedItemInteractor(get()) }
    single { GetSelfFeedInteractor(get(), get()) }
}