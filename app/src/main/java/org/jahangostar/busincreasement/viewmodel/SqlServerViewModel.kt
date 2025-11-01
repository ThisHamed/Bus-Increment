package org.jahangostar.busincreasement.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jahangostar.busincreasement.data.db.sql.SqlResponse
import org.jahangostar.busincreasement.data.model.SqlServerUiState
import org.jahangostar.busincreasement.data.model.Transaction
import org.jahangostar.busincreasement.data.model.User
import org.jahangostar.busincreasement.repository.SqlServerRepository
import javax.inject.Inject

@HiltViewModel
class SqlServerViewModel @Inject constructor(
    private val repository: SqlServerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SqlServerUiState())
    val uiState: StateFlow<SqlServerUiState> = _uiState

    init {
        viewModelScope.launch {
            delay(1000)
            fetchRemoteUsers()
            observeLocalUsers()
            observeLocalBalance()
            observeLocalCredits()
            getUnsyncedTransactionsCount()
        }
    }

    fun fetchRemoteUsers() {
        _uiState.update { it.copy(isLoadingUsers = true, usersError = null) }
        viewModelScope.launch {
            try {
                val users = repository.getRemoteMembers()
                _uiState.update { it.copy(isLoadingUsers = false, users = users) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingUsers = false,
                        usersError = "خطا در بارگذاری کاربران: ${e.message}"
                    )
                }
            }
        }
    }

    fun onUserLoggedIn(user: User) {
        _uiState.update { it.copy(loggedInUser = user) }
    }

    fun fetchRemoteCredits(deviceId: Int) {
        _uiState.update { it.copy(isLoadingCredits = true, creditsError = null) }
        viewModelScope.launch {
            try {
                val credits = repository.getRemoteCredits(deviceId)
                Log.d("fetchRemoteCredits", "credits: $credits")
                if (credits.isNotEmpty()) {
                    _uiState.update { it.copy(isLoadingCredits = false, credits = credits) }
                }
            } catch (e: Exception) {
                Log.e("fetchRemoteCredits", "Exception: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoadingCredits = false,
                        creditsError = "خطا در بارگذاری اعتبارات: ${e.message}"
                    )
                }
            }
        }
    }

    fun syncBalance(deviceId: Int, preBalance: Int, operatorId: Int) {
        _uiState.update { it.copy(isLoadingBalance = true, balanceError = null) }
        viewModelScope.launch {
            try {
                val balance = repository.syncDeviceBalance(deviceId, preBalance, operatorId)
                Log.d("syncBalance", "balance: $balance")
                if (balance != null)
                    _uiState.update { it.copy(isLoadingBalance = false, balance = balance) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingBalance = false,
                        balanceError = "خطا در بروزرسانی موجودی: ${e.message}"
                    )
                }
            }
        }
    }

    fun findUserByPassword(password: String) {
        _uiState.update { it.copy(isFindingUser = true, userByPasswordError = null) }
        viewModelScope.launch {
            try {
                repository.findLocalUserByPassword(password).collect { user ->
                    _uiState.update {
                        it.copy(
                            isFindingUser = false,
                            userByPassword = user,
                            userByPasswordError = if (user == null) "کاربر یافت نشد" else null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isFindingUser = false,
                        userByPasswordError = "خطا در جستجو: ${e.message}"
                    )
                }
            }
        }
    }

    private fun observeLocalUsers() {
        viewModelScope.launch {
            repository.localUsersFlow.collect { users ->
                _uiState.update {
                    it.copy(
                        localUsers = users,
                        localUsersError = if (users.isEmpty()) "هیچ کاربری یافت نشد" else null
                    )
                }
            }
        }
    }

    private fun observeLocalBalance() {
        viewModelScope.launch {
            repository.localBalance.collect { balance ->
                _uiState.update {
                    it.copy(
                        balance = balance,
                        balanceError = if (balance == null) "هیچ اعتباری یافت نشد" else null
                    )
                }
            }
        }
    }

    private fun observeLocalCredits() {
        viewModelScope.launch {
            repository.localCreditsFlow.collect { credits ->
                _uiState.update {
                    it.copy(
                        localCredits = credits,
                        localCreditsError = if (credits.isEmpty()) "هیچ اعتباری یافت نشد" else null
                    )
                }
            }
        }
    }

    private fun getUnsyncedTransactionsCount() {
        viewModelScope.launch {
            repository.unsyncedTransactionsCount.collect { count ->
                _uiState.update { it.copy(unsyncedTransactionCount = count) }
            }
        }
    }

    fun clearLocalTransactions() {
        viewModelScope.launch {
            try {
                repository.clearLocalTransactions()
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = "خطا در پاکسازی تراکنش‌ها") }
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
