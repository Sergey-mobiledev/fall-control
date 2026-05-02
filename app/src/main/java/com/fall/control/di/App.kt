package com.fall.control.di

import android.app.Application
import com.fall.control.data.database.UserSettingsDao
import com.fall.control.data.model.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    viewModelModule,
                    databaseModule,
                    repositoryModule
                )
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val userSettingsDao = GlobalContext.get().get<UserSettingsDao>()
            if (userSettingsDao.getUser() == null) {
                userSettingsDao.addUser(UserSettings())
            }
        }
    }
}