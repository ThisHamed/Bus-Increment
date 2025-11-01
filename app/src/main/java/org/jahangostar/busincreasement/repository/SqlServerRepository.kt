package org.jahangostar.busincreasement.repository

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.jahangostar.busincreasement.data.db.room.CreditDao
import org.jahangostar.busincreasement.data.db.room.TransactionDao
import org.jahangostar.busincreasement.data.db.room.UserDao
import org.jahangostar.busincreasement.data.db.sql.SqlConnection
import org.jahangostar.busincreasement.data.db.sql.SqlResponse
import org.jahangostar.busincreasement.data.db.sql.SqlService
import org.jahangostar.busincreasement.data.model.Balance
import org.jahangostar.busincreasement.data.model.Credit
import org.jahangostar.busincreasement.data.model.NetworkState
import org.jahangostar.busincreasement.data.model.ServerConfig
import org.jahangostar.busincreasement.data.model.Transaction
import org.jahangostar.busincreasement.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The Repository acts as a single source of truth for all data operations,
 * for the remote SQL Server and the local Room database.
 *
 * @param sqlService The service that executes queries against the SQL Server.
 * @param connection The raw SQL connection manager for lifecycle events.
 * @param userDao The DAO for local User entities.
 * @param transactionDao The DAO for local Transaction entities.
 * @param creditDao The DAO for local predefined Credit entities.
 */
@Singleton
class SqlServerRepository @Inject constructor(
    private val sqlService: SqlService,
    private val connection: SqlConnection,
    private val userDao: UserDao,
    private val transactionDao: TransactionDao,
    private val creditDao: CreditDao // Add CreditDao as a dependency
) {

    // --- SQL Server Connection Management ---

    val connectionState: Flow<NetworkState> = connection.connectionState

    suspend fun initConnection(config: ServerConfig): Result<Unit> {
        return connection.initConnection(config)
    }

    fun destroyConnection(state: NetworkState = NetworkState.Disconnected()) {
        connection.destroy(state)
    }


    // --- Remote SQL Server Data Operations ---

    suspend fun getRemoteMembers(): List<User> {
        val members = sqlService.fetchMembers()
        if (members.isNotEmpty()) {
            syncLocalUsers(members)
        }
        return members
    }

    suspend fun getRemoteCredits(deviceId: Int): List<Credit> {
        val credits = sqlService.fetchCredits(deviceId)
        Log.d("credits", credits.toString())
        if (credits.isNotEmpty()) {
            syncLocalCredits(credits)
        }
        return credits
    }

    suspend fun postUnsentTransactions(transactions: List<Transaction>): String {
        if (transactions.isEmpty()) {
            return "هیچ تراکنش همگام‌نشده‌ای برای ارسال وجود ندارد."
        }

        var successfulUploads = 0
        val failedTransactions = mutableListOf<String>()

        for (transaction in transactions) {
            val response = sqlService.postTransaction(transaction)
            when (response) {
                is SqlResponse.Success -> {
                    markTransactionAsSynced(transaction.id)
                    successfulUploads++
                }

                is SqlResponse.Failure -> {
                    val errorMessage = "تراکنش ${transaction.id}: ${response.message}"
                    failedTransactions.add(errorMessage)
                    if (response.message.contains("Cannot insert duplicate key in object")) {
                        markTransactionAsSynced(transaction.id)
                    }
                    Log.e("postUnsent", "Failed to send transaction: $errorMessage")
                }

                SqlResponse.NoConnection -> {
                    val remainingCount =
                        transactions.size - successfulUploads - failedTransactions.size
                    return "ارتباط با سرور قطع شد. " +
                            "ارسال تراکنش‌ها متوقف شد. " +
                            "($successfulUploads موفق, ${failedTransactions.size} ناموفق, $remainingCount ارسال نشده)"
                }

                SqlResponse.NotRow -> {
                    val errorMessage = "تراکنش ${transaction.id}: پاسخ غیرمنتظره از سرور."
                    failedTransactions.add(errorMessage)
                    Log.e(
                        "postUnsent",
                        "Unexpected server response for transaction: ${transaction.id}"
                    )
                }
            }
        }

        return when {
            failedTransactions.isEmpty() -> {
                "همگام‌سازی با موفقیت انجام شد. تعداد $successfulUploads تراکنش ارسال شد."
            }

            successfulUploads > 0 -> {
                "عملیات همگام‌سازی کامل شد. " +
                        "($successfulUploads موفق، ${failedTransactions.size} ناموفق). " +
                        "خطاها: ${failedTransactions.joinToString()}"
            }

            else -> {
                "هیچ تراکنشی با موفقیت ارسال نشد. " +
                        "تعداد ${failedTransactions.size} خطا رخ داد. " +
                        "اولین خطا: ${failedTransactions.first()}"
            }
        }
    }


    suspend fun saveRemoteTransaction(transaction: Transaction): SqlResponse {
        insertLocalTransaction(transaction)
        val response = sqlService.postTransaction(transaction)
        if (response is SqlResponse.Success || response.message == "DONE") {
            Log.d("saveRemoteTransaction", response.message)
            delay(500)
            markTransactionAsSynced(transaction.id)
        }
        return response
    }

    suspend fun syncDeviceBalance(deviceId: Int, preBalance: Int, operatorId: Int): Int? {
        val currentTime = System.currentTimeMillis()
        val balance: Int? = sqlService.updateBalance(
            deviceId = deviceId,
            preBalance = preBalance,
            timeMillis = currentTime,
            operatorId = operatorId
        )
        balance?.let { increasedAmount ->
            if (increasedAmount > 0) {
                val newTotalBalance = preBalance + increasedAmount
                insertBalance(Balance(balance = newTotalBalance))
            }
        }
        return balance
    }

    suspend fun fetchRemoteTransactionsForCard(cardId: Int, uc: Int): Result<List<Transaction>> = runCatching {
        sqlService.fetchTransactionsForCard(cardId, uc)
    }

    val localBalance: Flow<Int?> = creditDao.observeBalance()


    // --- Local Room Database User Operations ---

    val localUsersFlow: Flow<List<User>> = userDao.observeAll()

    @WorkerThread
    suspend fun syncLocalUsers(users: List<User>) {
        userDao.sync(users)
    }

    fun findLocalUserByPassword(password: String): Flow<User?> {
        return userDao.observeByPassword(password)
    }


    // --- Local Room Database Transaction Operations ---

    val unsyncedTransactionsCount: Flow<Int> = transactionDao.observeUnsyncedCount()

    fun getLocalTransactionsPaged(start: Long, end: Long): PagingSource<Int, Transaction> {
        return transactionDao.getDeviceReportPaged(start, end)
    }

    @WorkerThread
    suspend fun insertLocalTransaction(transaction: Transaction): Long? {
        return transactionDao.insertTransaction(transaction)
    }

    @WorkerThread
    suspend fun getUnsyncedLocalTransactions(): List<Transaction> {
        return transactionDao.getUnsyncedTransactions()
    }

    @WorkerThread
    suspend fun markTransactionAsSynced(transactionId: Long) {
        transactionDao.markAsSynced(transactionId)
    }

    @WorkerThread
    suspend fun clearLocalTransactions() {
        transactionDao.clearAllTransactions()
    }

    @WorkerThread
    suspend fun calculateTotalCharge(start: Long, end: Long): Long? {
        return transactionDao.calculateTotalCharge(start, end)
    }


    // --- Local Room Database Credit Operations ---

    /**
     * A reactive flow that emits the list of all predefined credits from the local database.
     */
    val localCreditsFlow: Flow<List<Credit>> = creditDao.observeAll()

    /**
     * Syncs the local predefined credits with a new list, typically fetched from the server.
     * It first deletes all old credits and then inserts the new ones.
     */
    @WorkerThread
    suspend fun syncLocalCredits(credits: List<Credit>) {
        creditDao.sync(credits)
    }

    @WorkerThread
    suspend fun insertBalance(balance: Balance) {
        creditDao.insertBalance(balance)
    }


}
