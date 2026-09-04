package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_profiles")
data class DeviceProfile(
    @PrimaryKey
    val id: String, // "headphones", "earbuds", "car_stereo", "speakers", or UUID
    val name: String,
    val deviceType: String, // HEADPHONES, EARBUDS, CAR_STEREO, SPEAKERS, CUSTOM
    val isEnabled: Boolean = true,
    val eqBands: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), // 10 bands (-15 to +15 dB)
    val bassBoost: Int = 20, // 0 to 100
    val loudnessGain: Int = 15, // 0 to 100
    val spatialSurround: Int = 25, // 0 to 100
    val reverbPreset: Int = 0, // 0: None, 1: Small Room, 2: Medium Room, 3: Large Room, 4: Medium Hall, 5: Large Hall, 6: Plate
    val limiterEnabled: Boolean = true, // Anti-clipping brickwall limiter
    val agcEnabled: Boolean = true, // Automatic Gain Control headroom compensation
    val presetName: String = "Studio Master",
    val isCurrent: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
