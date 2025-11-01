package org.jahangostar.busincreasement.repository

import org.jahangostar.busincreasement.data.db.room.DeviceDao
import org.jahangostar.busincreasement.data.model.Device
import org.jahangostar.busincreasement.data.model.ServerConfig
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val deviceDao: DeviceDao
) {

    val getDevice = deviceDao.getDevice()

    suspend fun updateDevice(
        deviceId: String,
        uc: String,
        devicePassword: String,
        organizationName: String
    ) = deviceDao.updateDevice(
        deviceId = deviceId,
        uc = uc,
        devicePassword = devicePassword,
        organizationName = organizationName
    )

    suspend fun insertDevice(device: Device) = deviceDao.insertDevice(device)

    suspend fun insertServerConfig(serverConfig: ServerConfig) = deviceDao.insertServerConfig(serverConfig)

    val getServerConfig = deviceDao.getServerConfig()



}