package org.jahangostar.busincreasement.data.db.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.jahangostar.busincreasement.data.model.User

/**
 * Data Access Object (DAO) for the User entity.
 * This interface defines the database interactions for the User table.
 */
@Dao
interface UserDao {

    /**
     * Retrieves all users from the database as a reactive Flow.
     * The Flow will automatically emit a new list of users whenever the data changes.
     */
    @Query("SELECT * FROM user ORDER BY name ASC")
    fun observeAll(): Flow<List<User>>

    /**
     * Finds and observes a single user by their password as a reactive Flow.
     * The Flow will emit the user or null, and update if the user's data changes.
     *
     * @param password The password to search for.
     * @return A Flow emitting the matching [User] object, or null if not found.
     */
    @Query("SELECT * FROM user WHERE pass = :password LIMIT 1")
    fun observeByPassword(password: String): Flow<User?>

    /**
     * Finds and observes a single user by their unique ID as a reactive Flow.
     *
     * @param id The unique ID of the user.
     * @return A Flow emitting the matching [User] object, or null if not found.
     */
    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<User?>

    /**
     * Inserts a single user into the database. If a user with the same primary key
     * already exists, the insertion is ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    /**
     * Inserts a list of users into the database. If any user in the list
     * has a primary key that already exists, that user will be replaced with the new data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(users: List<User>)

    /**
     * Updates an existing user in the database.
     * The user is identified by its primary key.
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Deletes a specific user from the database.
     */
    @Delete
    suspend fun deleteUser(user: User)

    /**
     * Deletes all users from the user table. Use with caution.
     */
    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Transaction
    suspend fun sync(users: List<User>) {
        deleteAllUsers()
        insertOrReplaceAll(users)
    }


}