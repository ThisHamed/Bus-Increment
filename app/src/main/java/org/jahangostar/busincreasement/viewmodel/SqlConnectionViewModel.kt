package org.jahangostar.busincreasement.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jahangostar.busincreasement.data.db.sql.SqlConnection.Companion.mapToSqlMessage
import org.jahangostar.busincreasement.data.db.sql.SqlConnection.Companion.setupNewConnection
import org.jahangostar.busincreasement.data.model.NetworkState
import org.jahangostar.busincreasement.data.model.ServerConfig
import org.jahangostar.busincreasement.data.model.SqlServerEvent
import org.jahangostar.busincreasement.repository.RoomRepository
import org.jahangostar.busincreasement.repository.SqlServerRepository
import org.jahangostar.busincreasement.ui.screen.SqlUiState
import javax.inject.Inject

@HiltViewModel
class SqlConnectionViewModel @Inject constructor(
    private val repository: SqlServerRepository,
    private val deviceRepository: RoomRepository
) : ViewModel() {

    private val TAG = "PTAG_SqlServerViewModel"


    /**
     * A public flow that allows the UI to observe the real-time connection state of the database.
     */
    val sqlState = repository.connectionState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkState.Disconnected()
    )

    private val _serverConfig = MutableStateFlow<ServerConfig?>(null)
    val serverConfig = _serverConfig.asStateFlow()

    private val _serverMessage = MutableStateFlow<String?>(null)
    val serverMessage = _serverMessage.asStateFlow()

    init {
        getServerConfig()
    }

    fun onEvent(event: SqlServerEvent) {
        when (event) {
            is SqlServerEvent.TestConnection -> testConnection()
            is SqlServerEvent.ServerMessageShown -> resetServerStatus()
        }
    }

    fun resetServerStatus() {
        viewModelScope.launch {
            _serverMessage.emit(null)
        }
    }

    fun getServerConfig() {
        viewModelScope.launch {
            deviceRepository.getServerConfig.collectLatest {
                _serverConfig.value = it
            }
        }
    }

    fun insertServerConfig(serverConfig: ServerConfig) {
        viewModelScope.launch {
            deviceRepository.insertServerConfig(serverConfig)
            delay(500)
            testConnection()
        }
    }

    /**
     * Attempts to connect to the database using the currently saved configuration.
     * This function should be called explicitly by the UI (e.g., on a button press or in a LaunchedEffect).
     */
    fun connect() {
        viewModelScope.launch {
            delay(500)
            val config = serverConfig.first()
            delay(600)
            if (config?.isEmpty() == true || config == null) {
                Log.w(TAG, "Connection attempt failed: Server config is empty.")
                repository.destroyConnection(
                    NetworkState.Disconnected(
                        IllegalArgumentException("پیکربندی سرور نامعتبر است.")
                    )
                )
                return@launch
            }
            Log.d(TAG, "Attempting to connect with config: $config")
            repository.initConnection(config)
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            val conf = serverConfig.value
            if (conf == null || conf.isEmpty()) {
                _serverMessage.value = "پیکربندی سرور یافت نشد."
                return@launch
            }
            _serverMessage.value = "در حال تست ارتباط..."
            delay(500)
            val result = isConnectedToServer(conf)
            _serverMessage.value = result
        }
    }

    private suspend fun isConnectedToServer(conf: ServerConfig): String =
        withContext(Dispatchers.IO) {
            setupNewConnection(conf)
                .fold(
                    onSuccess = {
                        it.close()
                        "ارتباط با سرور برقرار است"
                    },
                    onFailure = {
                        it.mapToSqlMessage()
                    }
                )
        }

}