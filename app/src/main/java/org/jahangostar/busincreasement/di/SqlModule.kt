package org.jahangostar.busincreasement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jahangostar.busincreasement.data.db.sql.SqlConnection
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SqlModule {
    @Provides
    @Singleton
    fun provideSqlServerConnection() = SqlConnection()


}
