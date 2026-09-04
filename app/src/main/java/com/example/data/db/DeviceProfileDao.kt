package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeviceProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceProfileDao {
    @Query("SELECT * FROM device_profiles ORDER BY lastUpdated DESC")
    fun getAllProfiles(): Flow<List<DeviceProfile>>

    @Query("SELECT * FROM device_profiles WHERE id = :id LIMIT 1")
    fun getProfileById(id: String): Flow<DeviceProfile?>

    @Query("SELECT * FROM device_profiles WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentProfile(): Flow<DeviceProfile?>

    @Query("SELECT * FROM device_profiles WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentProfileDirect(): DeviceProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: DeviceProfile)

    @Update
    suspend fun update(profile: DeviceProfile)

    @Query("UPDATE device_profiles SET isCurrent = CASE WHEN id = :profileId THEN 1 ELSE 0 END")
    suspend fun setCurrentProfile(profileId: String)

    @Query("SELECT COUNT(*) FROM device_profiles")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(profiles: List<DeviceProfile>)
}
