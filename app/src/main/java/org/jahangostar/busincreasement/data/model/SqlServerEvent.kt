package org.jahangostar.busincreasement.data.model

sealed class SqlServerEvent {
    data object TestConnection : SqlServerEvent()
    data object ServerMessageShown : SqlServerEvent()
}