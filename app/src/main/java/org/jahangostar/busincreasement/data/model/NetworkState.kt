package org.jahangostar.busincreasement.data.model

import android.content.Context
import org.jahangostar.busincreasement.R
import org.jahangostar.busincreasement.data.db.sql.SqlConnection.Companion.mapToSqlMessage

sealed class NetworkState {
    data object Connected : NetworkState()
    data object Connecting : NetworkState()
    data class Disconnected(val error: Throwable? = null) : NetworkState()
    data object NoNetwork : NetworkState()

    val drawableId
        get() = when (this) {
            Connected -> R.drawable.icon_wifi_connected
            Connecting -> R.drawable.icon_wifi_connecting
            is Disconnected -> R.drawable.icon_wifi_disconnected
            NoNetwork -> R.drawable.icon_wifi_null
        }


    fun getMessage(context: Context) = when (this) {
        Connected -> context.getString(R.string.server_connected)
        Connecting -> context.getString(R.string.connecting)
        is Disconnected -> error?.mapToSqlMessage()
            ?: context.getString(R.string.disconnected)

        NoNetwork -> context.getString(R.string.no_network2)
    }
}