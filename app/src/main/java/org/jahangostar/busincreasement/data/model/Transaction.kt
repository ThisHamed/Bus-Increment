package org.jahangostar.busincreasement.data.model

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

/**
 * @param id unique id of transaction : Primary key for local database
 * @param cardId id of used card
 * @param operationId id of transaction make by server. In local device it's always 0
 * @param operatorId personal id of operator who does the transaction
 * @param operationType type of operation [1:increase 2:decrease]
 * @param regDate Date and Time of transaction base on Shamsi Calender
 * by format of YYYY/mm/dd-HH:MM:ss = (1401/06/15-11:52:25)
 * */
@Entity(tableName = "transaction")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Int,
    val uc: Int,
    val deviceId: Int,
    val operatorId: Int = 0,
    val operationId: Int = 0,
    val operationType: Int, //1:increase 2:decrease
    val preCredit: Int,
    val price: Int,
    val finalCredit: Int,
    val regDate: PersianDate,
    @ColumnInfo(name = "has_set") val hasSet: Boolean = false
) {
    override fun toString(): String {
        return "id: " + id.toString() +
                ", cardId: " + cardId.toString() +
                ", deviceId: " + deviceId.toString() +
                ", operatorId: " + operatorId.toString() +
                ", operationId: " + operationId.toString() +
                ", operationType: " + operationType.toString() +
                ", preCredit: " + preCredit.toString() +
                ", price: " + price.toString() +
                ", finalCredit: " + finalCredit.toString() +
                ", regDate: " + regDate.toString() +
                ", hasSet: " + hasSet.toString() +
                " " + regDate.toShowingTime()
    }

    fun PersianDate.toShowingTime(): String {
        return PersianDateFormat("His").format(this).also {
            Log.d("1212", "$this toServerTimeFormat: $it")
        }
    }

}