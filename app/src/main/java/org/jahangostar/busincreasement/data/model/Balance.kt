package org.jahangostar.busincreasement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance")
data class Balance(
    @PrimaryKey val id: Int = 1,
    val balance: Int
)