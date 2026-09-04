package com.example

import android.app.Application
import com.example.audio.AudioEngine
import com.example.data.db.AppDatabase
import com.example.data.repository.DeviceProfileRepository
import com.example.service.EqualizerForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MEqualizerApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { DeviceProfileRepository(database.deviceProfileDao(), applicationScope) }
    val audioEngine by lazy { AudioEngine.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        // Start background service to maintain low-latency audio processing
        EqualizerForegroundService.startService(this)
    }
}
