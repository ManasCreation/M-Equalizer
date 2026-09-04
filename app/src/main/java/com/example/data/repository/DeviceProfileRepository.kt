package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.DeviceProfileDao
import com.example.data.model.DeviceProfile
import com.example.data.model.DeviceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DeviceProfileRepository(
    private val dao: DeviceProfileDao,
    private val scope: CoroutineScope
) {
    val allProfiles: Flow<List<DeviceProfile>> = dao.getAllProfiles()
    val currentProfile: Flow<DeviceProfile?> = dao.getCurrentProfile()

    init {
        scope.launch(Dispatchers.IO) {
            val count = dao.getCount()
            if (count == 0) {
                AppDatabase.populateInitialProfiles(dao)
            }
        }
    }

    suspend fun saveProfile(profile: DeviceProfile) {
        dao.insertOrUpdate(profile.copy(lastUpdated = System.currentTimeMillis()))
    }

    suspend fun selectProfile(profileId: String) {
        dao.setCurrentProfile(profileId)
    }

    suspend fun getCurrentProfileDirect(): DeviceProfile? {
        var profile = dao.getCurrentProfileDirect()
        if (profile == null) {
            val count = dao.getCount()
            if (count == 0) {
                AppDatabase.populateInitialProfiles(dao)
            }
            dao.setCurrentProfile(DeviceType.HEADPHONES.id)
            profile = dao.getCurrentProfileDirect()
        }
        return profile
    }
}
