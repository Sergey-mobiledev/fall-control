package com.fall.control.di

import androidx.room.Room
import com.fall.control.data.database.Database
import com.fall.control.data.repository.AccelerometerRepository
import com.fall.control.data.repository.FallHistoryRepository
import com.fall.control.data.repository.Repository
import com.fall.control.data.repository.SoundLevelMeterRepository
import com.fall.control.data.repository.TimerRepository
import com.fall.control.data.service.Billing
import com.fall.control.ui.home.HomeViewModel
import com.fall.control.ui.home.alert_dialog.AlertDialogViewModel
import com.fall.control.ui.home.buy_dialog.BuyViewModel
import com.fall.control.ui.home.first_start_dialog.FirstStartViewModel
import com.fall.control.ui.home.timer_dialog.TimerDialogViewModel
import com.fall.control.ui.home.xyz_info_dialog.XyzInfoViewModel
import com.fall.control.ui.main.MainViewModel
import com.fall.control.ui.menu.history.HistoryViewModel
import com.fall.control.ui.menu.history_1.HistoryOneViewModel
import com.fall.control.ui.menu.info.InfoViewModel
import com.fall.control.ui.menu.menu_home.MenuHomeViewModel
import com.fall.control.ui.menu.settings.SettingsViewModel
import com.fall.control.ui.start.StartViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        StartViewModel(get())
    }

    viewModel {
        HomeViewModel(get(), get(), get(), get())
    }

    viewModel {
        FirstStartViewModel(get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        HistoryViewModel(get(), get())
    }

    viewModel {
        MenuHomeViewModel(get())
    }

    viewModel {
        InfoViewModel(get())
    }

    viewModel {
        TimerDialogViewModel(get(), get())
    }

    viewModel {
        XyzInfoViewModel(get())
    }

    viewModel { parameters ->
        HistoryOneViewModel(get(), get(), parameters[0] )
    }

    viewModel {
        BuyViewModel(get(), get())
    }

    viewModel { parameters ->
        AlertDialogViewModel(get(), get(), parameters[0])
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), Database::class.java, "fall_control").build()
    }
    single { get<Database>().getUserSettingsDao }
    single { get<Database>().getFallHistoryDao }


}

val repositoryModule = module {
    single { TimerRepository(get(), get()) }
    single { SoundLevelMeterRepository(androidContext(), get(), get()) }
    single { AccelerometerRepository(androidContext(), get(), get()) }
    single { FallHistoryRepository(get(), get()) }
    single { Repository(get(), get(), get()) }
    single { Billing(androidContext()) }


}