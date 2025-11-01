package org.jahangostar.busincreasement.data.model

data class FullCardData(
    val sector0Data: Sector0DataResult, // Using a Result wrapper
    val sector5Data: Sector5DataResult,
    val sector6Data: Sector6DataResult,
    val rawDataErrors: List<String> = emptyList() // Errors from Mifare.readSectors or if sector data is missing
) {
    fun hasAnyData(): Boolean = sector0Data is Sector0DataResult.Success ||
                               sector5Data is Sector5DataResult.Success ||
                               sector6Data is Sector6DataResult.Success
    
    fun getAllParsingErrors(): List<String> {
        val errors = mutableListOf<String>()
        if (sector0Data is Sector0DataResult.Error) errors.add("Sector 0: ${sector0Data.message}")
        if (sector5Data is Sector5DataResult.Error) errors.add("Sector 5: ${sector5Data.message}")
        if (sector6Data is Sector6DataResult.Error) errors.add("Sector 6: ${sector6Data.message}")
        errors.addAll(rawDataErrors)
        return errors
    }
}

sealed interface Sector0DataResult {
    data class Success(
        val uc: Int,
        val cardId: Int
    ) : Sector0DataResult
    data class Error(val message: String) : Sector0DataResult
}

sealed interface Sector5DataResult {
    data class Success(
        val credit: Int,
        val payment: Int,
        val preEtebar: Int,
        val op: Int,
        val year: Int,    // 4-bit year (0-15)
        val month: Int,
        val day: Int,
        val hour: Int,
        val minute: Int,
        val etebar: Int,
        val deviceID: Int
    ) : Sector5DataResult
    data class Error(val message: String) : Sector5DataResult
}

data class TransactionRecord(
    val etebar: Int,
    val op: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val remEtebar: Int,
    val did: Int, //deviceId
    val hour: Int,
    val minute: Int
)

sealed interface Sector6DataResult {
    data class Success(val transactions: List<TransactionRecord>) : Sector6DataResult
    data class Error(val message: String) : Sector6DataResult
}