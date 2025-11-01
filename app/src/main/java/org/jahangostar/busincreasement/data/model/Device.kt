package org.jahangostar.busincreasement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_table")
data class Device(
    @PrimaryKey
    val id: Int = 1,
    val deviceId: String,
    val uc: String,
    val devicePassword: String,
    val organizationName: String
)