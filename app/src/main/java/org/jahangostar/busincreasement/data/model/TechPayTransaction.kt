package org.jahangostar.busincreasement.data.model

data class TechPayTransaction(
    val status: String?,
    val amount: String?,
    val date: String?,
    val time: String?,
    val responseMessage: String?,
    val phoneNumber: String?,
    val responseCode: String?,
    val maskPan: String?,
    val bankName: String?,
    val terminalNo: String?,
    val trace: String?,
    val merchantId: String?,
    val acceptorName: String?,
    val acceptorPhone: String?,
    val version: String?,
    val reserved1: String?,
    val reserved2: String?,
    val referenceNo: String?,
    val transactionType: String?,
    val iban: String?
)
