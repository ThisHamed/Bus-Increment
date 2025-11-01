package org.jahangostar.busincreasement.ui.screen.home

import org.jahangostar.busincreasement.data.model.TransactionRecord

/**
 * Represents all the mutable state for the HomeScreen UI.
 */
data class HomeScreenUiState(
    val currentCredit: Int = 0,
    val transactionList: List<TransactionRecord> = emptyList(),
    val selectedTopUpAmount: Int? = null,
    val customAmount: String = "",
    val dialogState: DialogState = DialogState.Hidden,
    val isCardOperationInProgress: Boolean = false,
    val snackbarMessage: String? = null
)