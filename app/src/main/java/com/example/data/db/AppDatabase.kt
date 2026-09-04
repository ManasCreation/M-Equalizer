package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DeviceProfile
import com.example.data.model.DeviceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DeviceProfile::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceProfileDao(): DeviceProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "m_equalizer_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialProfiles(database.deviceProfileDao())
                    }
                }
            }
        }

        suspend fun populateInitialProfiles(dao: DeviceProfileDao) {
            val defaultProfiles = listOf(
                DeviceProfile(
                    id = DeviceType.HEADPHONES.id,
                    name = "Headphones",
                    deviceType = DeviceType.HEADPHONES.name,
                    isEnabled = true,
                    eqBands = listOf(3, 2, 1, 0, -1, 1, 2, 3, 4, 3), // Studio Master
                    bassBoost = 35,
                    loudnessGain = 25,
                    spatialSurround = 40,
                    reverbPreset = 1,
                    limiterEnabled = true,
                    agcEnabled = true,
                    presetName = "Studio Master",
                    isCurrent = true,
                    lastUpdated = System.currentTimeMillis()
                ),
                DeviceProfile(
                    id = DeviceType.EARBUDS.id,
                    name = "Earbuds",
                    deviceType = DeviceType.EARBUDS.name,
                    isEnabled = true,
                    eqBands = listOf(6, 5, 3, 1, 0, 1, 3, 4, 5, 4), // Punchy dynamic
                    bassBoost = 50,
                    loudnessGain = 30,
                    spatialSurround = 50,
                    reverbPreset = 1,
                    limiterEnabled = true,
                    agcEnabled = true,
                    presetName = "Vocal & Bass Boost",
                    isCurrent = false,
                    lastUpdated = System.currentTimeMillis()
                ),
                DeviceProfile(
                    id = DeviceType.CAR_STEREO.id,
                    name = "Car Stereo",
                    deviceType = DeviceType.CAR_STEREO.name,
                    isEnabled = true,
                    eqBands = listOf(8, 7, 5, 1, 0, 2, 4, 5, 6, 5), // High impact car sound
                    bassBoost = 65,
                    loudnessGain = 45,
                    spatialSurround = 60,
                    reverbPreset = 0,
                    limiterEnabled = true,
                    agcEnabled = true,
                    presetName = "Car Punch",
                    isCurrent = false,
                    lastUpdated = System.currentTimeMillis()
                ),
                DeviceProfile(
                    id = DeviceType.SPEAKERS.id,
                    name = "Speakers",
                    deviceType = DeviceType.SPEAKERS.name,
                    isEnabled = true,
                    eqBands = listOf(4, 3, 2, 1, 0, 1, 2, 4, 4, 3), // Balanced room fill
                    bassBoost = 30,
                    loudnessGain = 20,
                    spatialSurround = 30,
                    reverbPreset = 2,
                    limiterEnabled = true,
                    agcEnabled = true,
                    presetName = "Room Balance",
                    isCurrent = false,
                    lastUpdated = System.currentTimeMillis()
                )
            )
            dao.insertAll(defaultProfiles)
        }
    }
}
