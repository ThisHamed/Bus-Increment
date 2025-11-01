package org.jahangostar.busincreasement.data.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.jahangostar.busincreasement.data.model.Device
import org.jahangostar.busincreasement.data.model.ServerConfig

@Dao
interface DeviceDao {

    @Query("SELECT * FROM device_table")
    fun getDevice(): Flow<Device?>

    @Query("UPDATE device_table SET deviceId = :deviceId, uc = :uc, devicePassword = :devicePassword, organizationName = :organizationName")
    suspend fun updateDevice(deviceId: String, uc: String, devicePassword: String, organizationName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerConfig(serverConfig: ServerConfig)

    @Query("SELECT * FROM server_config")
    fun getServerConfig(): Flow<ServerConfig?>




}