package org.jahangostar.busincreasement.data.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.jahangostar.busincreasement.data.model.Balance
import org.jahangostar.busincreasement.data.model.Credit

/**
 * Data Access Object (DAO) for the Credit entity.
 * This interface defines the database interactions for the 'default_credit' table,
 * which stores predefined top-up amounts.
 */
@Dao
interface CreditDao {

    /**
     * Retrieves all predefined credit amounts from the database as a reactive Flow.
     * The Flow will automatically emit a new list whenever the data changes.
     *
     * @return A Flow emitting a list of [Credit] objects.
     */
    @Query("SELECT * FROM `default_credit`")
    fun observeAll(): Flow<List<Credit>>

    /**
     * Inserts a list of credit amounts into the database. If any credit amount
     * with the same primary key already exists, it will be replaced.
     * This is useful for syncing the local cache with data from a server.
     *
     * @param credits The list of [Credit] objects to insert or replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(credits: List<Credit>)

    /**
     * Deletes all records from the 'default_credit' table.
     * This is typically used before syncing new data from a server.
     */
    @Query("DELETE FROM `default_credit`")
    suspend fun deleteAll()

    @Query("SELECT balance FROM balance LIMIT 1")
    fun observeBalance(): Flow<Int?>

    @Transaction
    suspend fun sync(credits: List<Credit>) {
        deleteAll()
        insertOrReplaceAll(credits)
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: Balance)

}
