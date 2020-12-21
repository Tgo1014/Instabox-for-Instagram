package tgo1014.instabox

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.android.ext.koin.androidContext
import org.koin.test.AutoCloseKoinTest
import org.koin.test.category.CheckModuleTest
import tgo1014.instabox.common.di.interactorModule
import tgo1014.instabox.common.di.networkModule
import tgo1014.instabox.common.di.storageModule
import tgo1014.instabox.common.di.viewModelModule

@Category(CheckModuleTest::class)
class ModuleCheckTest : AutoCloseKoinTest() {

    @Test
    fun checkModules() = org.koin.test.check.checkModules {
        androidContext(InstrumentationRegistry.getInstrumentation().context)
        modules(viewModelModule, networkModule, storageModule, interactorModule)
    }
}