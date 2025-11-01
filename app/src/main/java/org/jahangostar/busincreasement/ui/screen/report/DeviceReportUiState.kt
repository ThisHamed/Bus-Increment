package org.jahangostar.busincreasement.ui.screen.report

import org.jahangostar.busincreasement.data.model.Transaction
import saman.zamani.persiandate.PersianDate

data class DeviceReportUiState(
    val startDate: PersianDate = PersianDate(),
    val endDate: PersianDate = PersianDate(),
    val totalCharge: Long = 0,
    val totalGift: Long = 0,
    val totalPriceBack: Long = 0,
    val unsyncedTransactions: List<Transaction> = emptyList(),
    val isUnsyncedDialogVisible: Boolean = false,
    val isSyncing: Boolean = false,
    val snackbarMessage: String? = null
)