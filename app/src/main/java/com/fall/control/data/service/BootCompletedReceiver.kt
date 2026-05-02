package com.fall.control.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fall.control.data.database.UserSettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val userSettingsDao = GlobalContext.get().get<UserSettingsDao>()
                val settings = userSettingsDao.getUser()
                if (settings?.isActiveControlMode == true) {
                    FallDetectionService.startOrSync(context.applicationContext)
                }
            }
            pendingResult.finish()
        }
    }
}
