package org.jahangostar.busincreasement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jahangostar.busincreasement.data.model.Device
import org.jahangostar.busincreasement.data.model.ServerConfig
import org.jahangostar.busincreasement.repository.RoomRepository
import org.jahangostar.busincreasement.ui.screen.AppSettings
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deviceRepository: RoomRepository
) : ViewModel() {

    private val _device = MutableStateFlow<Device?>(null)
    val device = _device.asStateFlow()

    init {
        getDevice()
    }

    fun getDevice() {
        viewModelScope.launch {
            deviceRepository.getDevice.collectLatest {
                _device.value = it
            }
        }
    }


    fun saveDeviceSettings(currentSettings: AppSettings) {
        viewModelScope.launch {
            val existingDevice = _device.value

            if (existingDevice != null) {
                val deviceToUpdate = Device(
                    id = existingDevice.id,
                    deviceId = currentSettings.deviceId,
                    uc = currentSettings.uc,
                    devicePassword = currentSettings.devicePassword,
                    organizationName = currentSettings.organizationName
                )
                deviceRepository.updateDevice(
                    deviceId = deviceToUpdate.deviceId,
                    uc = deviceToUpdate.uc,
                    devicePassword = deviceToUpdate.devicePassword,
                    organizationName = deviceToUpdate.organizationName
                )
            } else {
                // Insert
                val newDevice = Device(
                    deviceId = currentSettings.deviceId,
                    uc = currentSettings.uc,
                    devicePassword = currentSettings.devicePassword,
                    organizationName = currentSettings.organizationName
                )
                deviceRepository.insertDevice(newDevice)
            }
        }
    }

}