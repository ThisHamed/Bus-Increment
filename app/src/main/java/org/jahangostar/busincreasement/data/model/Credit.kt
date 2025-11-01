package org.jahangostar.busincreasement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "default_credit")
data class Credit(
    @PrimaryKey val id: Int,
    val price: Int
)


