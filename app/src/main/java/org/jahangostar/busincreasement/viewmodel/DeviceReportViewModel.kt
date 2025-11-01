package org.jahangostar.busincreasement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jahangostar.busincreasement.data.model.Transaction
import org.jahangostar.busincreasement.repository.SqlServerRepository
import org.jahangostar.busincreasement.ui.screen.report.DeviceReportUiState
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

@HiltViewModel
class DeviceReportViewModel @Inject constructor(
    private val repository: SqlServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceReportUiState())
    val uiState: StateFlow<DeviceReportUiState> = _uiState.asStateFlow()

    private val dateRangeFlow =
        combine(_uiState.map { it.startDate }, _uiState.map { it.endDate }) { start, end ->
            start.time to end.time
        }.distinctUntilChanged()

    val transactionsPager: StateFlow<Flow<PagingData<Transaction>>> = MutableStateFlow(
        createPager(PersianDate().time, PersianDate().time)
    )


    val unsyncedTransactionsCount: StateFlow<Int> = repository.unsyncedTransactionsCount
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        viewModelScope.launch {
            dateRangeFlow.collect { (start, end) ->
                updateSummaries(start, end)
                (transactionsPager as MutableStateFlow).value = createPager(start, end)
            }
        }
    }

    fun onDateRangeChanged(start: PersianDate, end: PersianDate) {
        _uiState.value = _uiState.value.copy(startDate = start, endDate = end)
    }

    private fun createPager(start: Long, end: Long): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { repository.getLocalTransactionsPaged(start, end) }
        ).flow.cachedIn(viewModelScope)
    }

    suspend fun updateSummaries(start: Long, end: Long) {
        val charge = repository.calculateTotalCharge(start, end) ?: 0
        _uiState.value = _uiState.value.copy(
            totalCharge = charge,
        )
    }

    fun showUnsyncedTransactions() {
        viewModelScope.launch {
            val transactions = repository.getUnsyncedLocalTransactions()
            _uiState.value = _uiState.value.copy(
                unsyncedTransactions = transactions,
                isUnsyncedDialogVisible = true
            )
        }
    }

    fun dismissUnsyncedTransactionsDialog() {
        _uiState.value = _uiState.value.copy(isUnsyncedDialogVisible = false)
    }

    fun clearAllLocalTransactions() {
        viewModelScope.launch {
            repository.clearLocalTransactions()
            updateSummaries(uiState.value.startDate.time, uiState.value.endDate.time)
        }
    }

    /**
     * Fetches all unsynced transactions from the local DB and attempts to post them to the server.
     * Updates the UI with the Farsi result message.
     */
    fun postUnsentTransactions() {
        // Prevent starting a new sync if one is already in progress
        if (_uiState.value.isSyncing) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, snackbarMessage = null)
            try {
                val unsent = repository.getUnsyncedLocalTransactions()
                val resultMessage = repository.postUnsentTransactions(unsent)
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    snackbarMessage = resultMessage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    snackbarMessage = "خطا در فرآیند همگام‌سازی: ${e.message}"
                )
            }
        }
    }

    /**
     * Notifies the ViewModel that the snackbar message has been handled.
     */
    fun onSnackbarShown() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
