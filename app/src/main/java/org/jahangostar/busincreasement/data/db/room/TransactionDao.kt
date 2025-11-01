package org.jahangostar.busincreasement.data.db.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.jahangostar.busincreasement.data.model.Transaction
import saman.zamani.persiandate.PersianDate

/**
 * Data Access Object (DAO) for the Transaction entity.
 * This interface defines all database interactions for the local transaction log.
 */
@Dao
interface TransactionDao {

    /**
     * Retrieves all transactions from the database, ordered by date, as a reactive Flow.
     */
    @Query("SELECT * FROM `transaction` ORDER BY regDate DESC")
    fun observeAll(): Flow<List<Transaction>>

    /**
     * Finds a specific transaction based on its core contents to check for duplicates.
     */
    @Query(
        "SELECT * FROM `transaction` " +
                "WHERE regDate = :regDate AND cardId = :cardId AND operationType = :operationType"
    )
    suspend fun findByContents(
        cardId: Int,
        operationType: Int,
        regDate: PersianDate
    ): List<Transaction>

    /**
     * Provides a PagingSource for efficiently loading and displaying a large report of transactions within a date range.
     */
    @Query("SELECT * FROM `transaction` WHERE regDate BETWEEN :start AND :end ORDER BY regDate DESC")
    fun getDeviceReportPaged(start: Long, end: Long): PagingSource<Int, Transaction>

    /**
     * Calculates the total sum of 'CHARGE' transactions within a specific date range.
     */
    @Query("SELECT SUM(price) FROM `transaction` WHERE operationType = 1 AND regDate BETWEEN :start AND :end")
    suspend fun calculateTotalCharge(start: Long, end: Long): Long?

    /**
     * Retrieves all transactions that have not yet been successfully synced with the server.
     */
    @Query("SELECT * FROM `transaction` WHERE has_set = 0")
    suspend fun getUnsyncedTransactions(): List<Transaction>

    /**
     * Provides a reactive count of unsynced transactions (excluding gifts).
     */
    @Query("SELECT COUNT(id) FROM `transaction` WHERE has_set = 0")
    fun observeUnsyncedCount(): Flow<Int>

    /**
     * Marks a specific transaction as successfully synced with the server.
     */
    @Query("UPDATE `transaction` SET has_set = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    /**
     * Inserts a single transaction into the database.
     * @return The row ID of the newly inserted transaction.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: Transaction): Long

    /**
     * Deletes a specific transaction from the database.
     */
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    /**
     * Deletes all transactions from the transaction table. Use with caution.
     */
    @Query("DELETE FROM `transaction`")
    suspend fun clearAllTransactions()
}