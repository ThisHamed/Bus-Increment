package org.jahangostar.busincreasement.ui.screen.home

/**
 * Defines the state of all dialogs on the screen for cleaner management.
 */
sealed class DialogState {
    data object Hidden : DialogState()
    data object ShowPasswordPrompt : DialogState()
    data object ShowTransactions : DialogState()
    data object ShowWaitingForCard : DialogState()
    data object ShowGetTransactionSource : DialogState()
    data class ShowResult(val isSuccess: Boolean) : DialogState()
    data class ShowConfirmTopUp(val amount: Int) : DialogState()
}