package org.jahangostar.busincreasement.data.model

/**
 * A cleaner UI state class that avoids a generic wrapper. Each piece of state
 * is represented by its own, more specific, property.
 */
data class SqlServerUiState(
    val isLoadingUsers: Boolean = false,
    val users: List<User> = emptyList(),
    val usersError: String? = null,

    val isLoadingCredits: Boolean = false,
    val credits: List<Credit> = emptyList(),
    val creditsError: String? = null,

    val isLoadingBalance: Boolean = false,
    val balance: Int? = null,
    val balanceError: String? = null,

    val loggedInUser: User? = null,
    val localUsers: List<User> = emptyList(),
    val localUsersError: String? = null,

    val localCredits: List<Credit> = emptyList(),
    val localCreditsError: String? = null,

    val isSavingTransaction: Boolean = false,
    val transactionSaved: Boolean = false,
    val transactionError: String? = null,

    val userByPassword: User? = null,
    val userByPasswordError: String? = null,
    val isFindingUser: Boolean = false,

    val unsyncedTransactionCount: Int = 0,

    val snackbarMessage: String? = null,

    val remoteTransactions: List<Transaction> = emptyList(),
    val isLoadingTransactions: Boolean = false
)

