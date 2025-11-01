package org.jahangostar.busincreasement.ui.screen.home

import android.content.Context

/**
 * Defines all the events that the UI can send to the ViewModel.
 */
sealed class HomeScreenEvent {
    data class ReadCardInfo(val context: Context) : HomeScreenEvent()
    data class GetHistoryFromCard(val context: Context) : HomeScreenEvent()
    data class TopUpCard(val context: Context, val amount: Int) : HomeScreenEvent()
    data class CustomAmountEntered(val amount: String) : HomeScreenEvent()
    data object ShowGetHistoryDialog : HomeScreenEvent()
    data object RequestSettingsAccess : HomeScreenEvent()
    data class PredefinedAmountSelected(val amount: Int) : HomeScreenEvent()
    data class ConfirmCashPayment(val context: Context, val amount: Int) : HomeScreenEvent()
    data class GetHistoryFromServer(val context: Context) : HomeScreenEvent()
    data object DismissDialog : HomeScreenEvent()
    data object SnackbarShown : HomeScreenEvent()
    data object CustomAmountConfirmed : HomeScreenEvent()
}