package org.jahangostar.busincreasement.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jahangostar.busincreasement.data.db.room.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()

    @Provides
    @Singleton
    fun provideAppDao(
        dataBase: AppDatabase
    ) = dataBase.deviceDao

    @Provides
    @Singleton
    fun provideUserDao(
        dataBase: AppDatabase
    ) = dataBase.userDao

    @Provides
    @Singleton
    fun provideCreditDao(
        dataBase: AppDatabase
    ) = dataBase.creditDao

    @Provides
    @Singleton
    fun provideTransactionDao(
        dataBase: AppDatabase
    ) = dataBase.transactionDao

}