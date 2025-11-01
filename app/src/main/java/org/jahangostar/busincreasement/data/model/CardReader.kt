package org.jahangostar.busincreasement.data.model

data class CardReader(
    val id: Int,
    val uc: Int,
    val group: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val mode: Int,
    val credit: Long? = null,
    val plate: String? = "",
    val phone: String? = ""
)

data class Sector6TransactionData(
    val operationCount: Int,
    val opType: Int,
    val etebar: Long,
    val opYear: Int,
    val opMonth: Int,
    val opDay: Int,
    val rawYearMonthByte: Int,
    val remainingEtebar: Long,
    val did: Int,
    val opHour: Int,
    val opMinute: Int
)