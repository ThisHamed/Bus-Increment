package org.jahangostar.busincreasement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sardari.daterangepicker.BuildConfig
import java.io.Serializable

@Entity(tableName = "error")
data class ErrorLog(
    val date: Long,
    val type: ErrorType,
    val messageId: Int,
    val cause: String? = null,
    val data: String? = null,
    val version:String = BuildConfig.VERSION_NAME,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Serializable {
    constructor(date: Long, type: ErrorType, throwable: Throwable) : this(
        date,
        type,
        0,
        "msg: ${throwable.message}, stack: ${throwable.stackTraceToString()}"
    )

}


enum class ErrorType {
    CARD_ERROR, NETWORK_ERROR, JTDS,SQL_QUERY, MYSQL, QR, CRASH,SOCKET_PACKET,COPY
}

class QrException private constructor(val errorLog: ErrorLog, cause: Throwable?) : Throwable(cause) {

    constructor(date: Long, messageId: Int, data: String, cause: Throwable? = null) : this(
        ErrorLog(
            date,
            ErrorType.QR,
            messageId,
            null,
            data
        ),
        cause
    )
}