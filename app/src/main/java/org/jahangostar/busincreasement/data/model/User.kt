package org.jahangostar.busincreasement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user")
@Serializable
data class User(
    @PrimaryKey val id: Int,
    val pass: String,
    val name: String,
)