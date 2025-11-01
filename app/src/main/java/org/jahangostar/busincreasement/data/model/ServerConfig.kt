package org.jahangostar.busincreasement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_config")
data class ServerConfig(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "server_ip") val serverIp: String,
    @ColumnInfo(name = "sql_user") val sqlUser: String,
    @ColumnInfo(name = "sql_password") val sqlPassword: String,
) {
    fun isEmpty(): Boolean {
        return serverIp.isBlank() || sqlUser.isBlank() || sqlPassword.isBlank()
    }
}

