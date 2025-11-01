package org.jahangostar.busincreasement.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jahangostar.busincreasement.data.db.sql.SqlResponse
import org.jahangostar.busincreasement.data.model.Balance
import org.jahangostar.busincreasement.data.model.Sector0DataResult
import org.jahangostar.busincreasement.data.model.Sector5DataResult
import org.jahangostar.busincreasement.data.model.Sector6DataResult
import org.jahangostar.busincreasement.data.model.SqlServerUiState
import org.jahangostar.busincreasement.data.model.Transaction
import org.jahangostar.busincreasement.repository.RoomRepository
import org.jahangostar.busincreasement.repository.SqlServerRepository
import org.jahangostar.busincreasement.ui.screen.home.DialogState
import org.jahangostar.busincreasement.ui.screen.home.HomeScreenEvent
import org.jahangostar.busincreasement.ui.screen.home.HomeScreenUiState
import org.jahangostar.busincreasement.util.CardUtils
import org.jahangostar.busincreasement.util.Constants.formatCreditValue
import org.jahangostar.busincreasement.util.Constants.fromCardTransaction
import org.jahangostar.busincreasement.util.Mifare
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

/**
 * ViewModel for the HomeScreen, annotated for Hilt dependency injection.
 * Manages all UI state and business logic, completely decoupling the UI.
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val sqlServerRepository: SqlServerRepository,
    private val repository: RoomRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeScreenUiState())
        private set

    private val _uiState = MutableStateFlow(SqlServerUiState())

    private val balanceFlow = MutableStateFlow(0)
    private val deviceId = MutableStateFlow(0)

    val operatorId = MutableStateFlow(0)


    init {
        observeLocalBalance()
        observeLocalDevice()
    }


    /**
     * The single entry point for all events from the UI.
     */
    fun onEvent(event: HomeScreenEvent, uc: Int) {
        when (event) {
            is HomeScreenEvent.CustomAmountConfirmed -> {
                val amount = uiState.customAmount.toIntOrNull()
                uiState = if (amount != null && amount > 0) {
                    uiState.copy(
                        dialogState = DialogState.ShowConfirmTopUp(amount),
                        customAmount = ""
                    )
                } else {
                    uiState.copy(snackbarMessage = "لطفاً مبلغ معتبری وارد کنید.")
                }
            }

            is HomeScreenEvent.ReadCardInfo -> readCardInfo(event.context, uc)
            is HomeScreenEvent.GetHistoryFromCard -> getHistoryFromCard(event.context, uc)
            is HomeScreenEvent.TopUpCard -> handleCardTopUp(event.context, event.amount, uc)
            is HomeScreenEvent.CustomAmountEntered -> handleCustomAmountInput(event.amount)
            is HomeScreenEvent.PredefinedAmountSelected -> uiState =
                uiState.copy(dialogState = DialogState.ShowConfirmTopUp(event.amount))

            is HomeScreenEvent.ShowGetHistoryDialog -> uiState =
                uiState.copy(dialogState = DialogState.ShowGetTransactionSource)

            is HomeScreenEvent.RequestSettingsAccess -> uiState =
                uiState.copy(dialogState = DialogState.ShowPasswordPrompt)

            is HomeScreenEvent.ConfirmCashPayment -> handleCardTopUp(
                event.context,
                event.amount,
                uc
            )

            is HomeScreenEvent.GetHistoryFromServer -> {
                getHistoryFromServer(event.context, uc)
            }

            is HomeScreenEvent.DismissDialog -> uiState =
                uiState.copy(dialogState = DialogState.Hidden)

            is HomeScreenEvent.SnackbarShown -> uiState = uiState.copy(snackbarMessage = null)
        }
    }

    private fun getHistoryFromServer(context: Context, uc: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(dialogState = DialogState.ShowWaitingForCard)
            try {
                val parsedData = CardUtils.readAndParseCardData(context)

                val sector0Result = parsedData.sector0Data
                if (sector0Result !is Sector0DataResult.Success) {
                    uiState = uiState.copy(snackbarMessage = "خطا در خواندن اطلاعات اصلی کارت.")
                    uiState = uiState.copy(dialogState = DialogState.Hidden)
                    return@launch
                }

                if (sector0Result.uc != uc) {
                    uiState =
                        uiState.copy(snackbarMessage = "سازمان کارت اشتباه است. لطفاً کارت صحیح را قرار دهید.")
                    uiState = uiState.copy(dialogState = DialogState.Hidden)
                    return@launch
                }
                fetchRemoteTransactions(sector0Result.cardId, sector0Result.uc)
                uiState = uiState.copy(dialogState = DialogState.Hidden)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    dialogState = DialogState.Hidden,
                    snackbarMessage = "خطا: ${e.message}"
                )
            }
        }
    }

    private fun handleCustomAmountInput(newAmount: String) {
        val digitsOnly = newAmount.filter { it.isDigit() }
        uiState = if (digitsOnly.isEmpty() || (digitsOnly.toLongOrNull() ?: 0L) <= 2_000_000) {
            uiState.copy(customAmount = digitsOnly)
        } else {
            uiState.copy(snackbarMessage = "مبلغ وارد شده نمی‌تواند بیشتر از ۲,۰۰۰,۰۰۰ تومان باشد.")
        }
    }

    private fun readCardInfo(context: Context, uc: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(dialogState = DialogState.ShowWaitingForCard)
            try {
                val parsedData = CardUtils.readAndParseCardData(context)

                val sector0Result = parsedData.sector0Data
                if (sector0Result !is Sector0DataResult.Success) {
                    uiState = uiState.copy(snackbarMessage = "خطا در خواندن اطلاعات اصلی کارت.")
                    uiState = uiState.copy(dialogState = DialogState.Hidden)
                    return@launch
                }

                if (sector0Result.uc != uc) {
                    uiState =
                        uiState.copy(snackbarMessage = "سازمان کارت اشتباه است. لطفاً کارت صحیح را قرار دهید.")
                    uiState = uiState.copy(dialogState = DialogState.Hidden)
                    return@launch
                }

                uiState = when (val result = parsedData.sector5Data) {
                    is Sector5DataResult.Success -> {
                        uiState.copy(
                            currentCredit = result.credit,
                            snackbarMessage = "اعتبار کارت: ${formatCreditValue(result.credit)} تومان"
                        )
                    }

                    is Sector5DataResult.Error -> {
                        uiState.copy(snackbarMessage = "خطا در خواندن اعتبار: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(snackbarMessage = "خطا: ${e.message}")
            } finally {
                uiState = uiState.copy(dialogState = DialogState.Hidden)
            }
        }
    }

    private fun getHistoryFromCard(context: Context, uc: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(dialogState = DialogState.ShowWaitingForCard)
            try {
                val parsedData = CardUtils.readAndParseCardData(context)

                val sector0Result = parsedData.sector0Data
                if (sector0Result !is Sector0DataResult.Success) {
                    uiState = uiState.copy(snackbarMessage = "خطا در خواندن اطلاعات اصلی کارت.")
                    uiState = uiState.copy(dialogState = DialogState.Hidden)
                    return@launch
                }

                if (sector0Result.uc != uc) {
                    uiState =
                        uiState.copy(snackbarMessage = "سازمان کارت اشتباه است. لطفاً کارت صحیح را قرار دهید.")
                    uiState = uiState.copy(dialogState = DialogState.Hidden)
                    return@launch
                }

                when (val result = parsedData.sector6Data) {
                    is Sector6DataResult.Success -> {
                        val transactions = result.transactions.filter { it.did != 0 }
                        uiState = if (transactions.isNotEmpty()) {
                            uiState.copy(
                                transactionList = transactions,
                                dialogState = DialogState.ShowTransactions
                            )
                        } else {
                            uiState.copy(
                                dialogState = DialogState.Hidden,
                                snackbarMessage = "تاریخچه تراکنش‌ها در کارت خالی است."
                            )
                        }
                    }

                    is Sector6DataResult.Error -> {
                        uiState = uiState.copy(
                            dialogState = DialogState.Hidden,
                            snackbarMessage = "خطا در خواندن تاریخچه: ${result.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    dialogState = DialogState.Hidden,
                    snackbarMessage = "خطا: ${e.message}"
                )
            }
        }
    }

    private fun handleCardTopUp(context: Context, paymentAmount: Int, uc: Int) {

        if (uiState.isCardOperationInProgress) {
            _uiState.update { it.copy(snackbarMessage = "عملیات دیگری در حال انجام است. لطفاً منتظر بمانید.") }
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                dialogState = DialogState.ShowWaitingForCard,
                isCardOperationInProgress = true
            )
            try {
                val parsedData = CardUtils.readAndParseCardData(context)

                val sector0Result = parsedData.sector0Data
                if (sector0Result !is Sector0DataResult.Success || sector0Result.uc != uc) {
                    uiState = uiState.copy(
                        dialogState = DialogState.Hidden,
                        snackbarMessage = if (sector0Result !is Sector0DataResult.Success) "خطا در خواندن اطلاعات اصلی کارت." else "سازمان کارت اشتباه است."
                    )
                    return@launch
                }

                val preCreditResult = parsedData.sector5Data
                if (preCreditResult !is Sector5DataResult.Success) {
                    val errorMsg =
                        (preCreditResult as? Sector5DataResult.Error)?.message ?: "خطای نامشخص"
                    uiState = uiState.copy(
                        dialogState = DialogState.Hidden,
                        snackbarMessage = "خطا در خواندن اعتبار اولیه: $errorMsg"
                    )
                    return@launch
                }
                val preCredit = preCreditResult.credit

                if (balanceFlow.value < paymentAmount) {
                    uiState = uiState.copy(
                        dialogState = DialogState.ShowResult(isSuccess = false),
                        snackbarMessage = "موجودی اعتبار دستگاه برای انجام این تراکنش کافی نیست."
                    )
                    return@launch
                }

                val newCredit = preCredit + paymentAmount
                val deviceID = deviceId.value
                val now = PersianDate()

                val writeResult = Mifare.writeCreditTransaction(
                    context = context,
                    preCredit = preCredit,
                    payment = paymentAmount,
                    deviceID = deviceID,
                    year = now.shYear.toString().takeLast(2).toInt(),
                    month = now.shMonth,
                    day = now.shDay,
                    hour = now.hour,
                    minute = now.minute
                )

                if (writeResult == 0) {
                    Log.d(
                        "handleCardTopUp",
                        "NFC write successful. Proceeding with database updates."
                    )

                    uiState = uiState.copy(
                        currentCredit = newCredit,
                        dialogState = DialogState.ShowResult(isSuccess = true)
                    )

                    try {
                        val balanceUpdateJob = async {
                            sqlServerRepository.insertBalance(Balance(balance = balanceFlow.value - paymentAmount))
                        }

                        val transactionSaveJob = async {
                            val regDate = PersianDate(System.currentTimeMillis())
                            saveTransaction(
                                Transaction(
                                    cardId = sector0Result.cardId,
                                    uc = sector0Result.uc,
                                    deviceId = deviceID,
                                    operatorId = operatorId.value,
                                    operationType = 1,
                                    preCredit = preCredit,
                                    price = paymentAmount,
                                    finalCredit = newCredit,
                                    regDate = regDate
                                )
                            )
                        }

                        awaitAll(balanceUpdateJob, transactionSaveJob)
                        Log.d(
                            "handleCardTopUp",
                            "Local balance update and transaction save jobs completed."
                        )

                    } catch (dbError: Exception) {
                        Log.e(
                            "handleCardTopUp",
                            "Error during background DB operations after successful NFC write.",
                            dbError
                        )
                        uiState =
                            uiState.copy(snackbarMessage = "کارت با موفقیت شارژ شد، اما در ثبت اطلاعات در پایگاه داده محلی خطایی رخ داد.")
                    }

                } else {
                    Log.e(
                        "handleCardTopUp",
                        "NFC write failed with code: $writeResult. Aborting database updates."
                    )
                    uiState = uiState.copy(dialogState = DialogState.ShowResult(isSuccess = false))
                }
            } catch (e: Exception) {
                Log.e("handleCardTopUp", "An unexpected error occurred during top-up.", e)
                uiState = uiState.copy(
                    dialogState = DialogState.ShowResult(isSuccess = false),
                    snackbarMessage = "یک خطای غیرمنتظره رخ داد: ${e.message}"
                )
            } finally {
                uiState = uiState.copy(
                    isCardOperationInProgress = false,
                    dialogState = if (uiState.dialogState is DialogState.ShowWaitingForCard) DialogState.Hidden else uiState.dialogState
                )
            }

        }
    }

    fun updateOperatorId(id: Int) {
        viewModelScope.launch {
            operatorId.emit(id)
        }
    }

    private fun saveTransaction(transaction: Transaction) {

        if (_uiState.value.isSavingTransaction) {
            Log.w("saveTransaction", "Save operation already in progress. New request ignored.")
            return
        }

        _uiState.update {
            it.copy(
                isSavingTransaction = true,
                transactionError = null,
                transactionSaved = false
            )
        }
        viewModelScope.launch {
            try {
                val response = sqlServerRepository.saveRemoteTransaction(transaction)

                if (response is SqlResponse.Success) {
                    _uiState.update {
                        it.copy(transactionSaved = true)
                    }
                } else {
                    val errorMessage =
                        (response as? SqlResponse.Failure)?.message ?: "خطا در ارسال تراکنش"
                    _uiState.update {
                        it.copy(transactionError = errorMessage)
                    }
                }
            } catch (e: Exception) {
                Log.e("saveTransaction", "An unexpected error occurred", e)
                _uiState.update {
                    it.copy(transactionError = "خطای غیرمنتظره: ${e.message}")
                }
            } finally {
                _uiState.update {
                    it.copy(isSavingTransaction = false)
                }
            }
        }
    }


    fun fetchRemoteTransactions(cardId: Int, uc: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTransactions = true, snackbarMessage = null) }
            sqlServerRepository.fetchRemoteTransactionsForCard(cardId, uc)
                .onSuccess { transactions ->
                    Log.d("Transactions", transactions.toString())
                    val transactionList = transactions.map { fromCardTransaction(it) }
                    uiState = uiState.copy(
                        transactionList = transactionList,
                        dialogState = DialogState.ShowTransactions
                    )
                    _uiState.update {
                        it.copy(
                            isLoadingTransactions = false,
                            remoteTransactions = transactions,
                            snackbarMessage = if (transactions.isEmpty()) "تاریخچه تراکنش سرور خالی است." else null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingTransactions = false,
                            snackbarMessage = "خطا در دریافت تاریخچه از سرور: ${error.message}"
                        )
                    }
                }
        }
    }

    private fun observeLocalBalance() {
        viewModelScope.launch {
            sqlServerRepository.localBalance.collect { balance ->
                if (balance != balanceFlow.value && balance != null) {
                    balanceFlow.emit(balance)
                }
            }
        }
    }

    private fun observeLocalDevice() {
        viewModelScope.launch {
            repository.getDevice.collect { device ->
                if (device != null) {
                    deviceId.emit(device.deviceId.toInt())
                }
            }
        }
    }
}
