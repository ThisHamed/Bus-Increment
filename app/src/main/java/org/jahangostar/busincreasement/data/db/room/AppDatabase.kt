package org.jahangostar.busincreasement.data.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.jahangostar.busincreasement.data.model.Balance
import org.jahangostar.busincreasement.data.model.Credit
import org.jahangostar.busincreasement.data.model.Device
import org.jahangostar.busincreasement.data.model.ServerConfig
import org.jahangostar.busincreasement.data.model.Transaction
import org.jahangostar.busincreasement.data.model.User

@Database(
    entities = [
        Device::class,
        ServerConfig::class,
        Credit::class,
        Transaction::class,
        User::class,
        Balance::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val deviceDao: DeviceDao
    abstract val creditDao: CreditDao
    abstract val transactionDao: TransactionDao
    abstract val userDao: UserDao


}