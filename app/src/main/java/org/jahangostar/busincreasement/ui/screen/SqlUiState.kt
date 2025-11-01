package org.jahangostar.busincreasement.ui.screen

import org.jahangostar.busincreasement.data.model.Credit
import org.jahangostar.busincreasement.data.model.User

data class SqlUiState(
    val users: List<User> = emptyList(),
    val credits: List<Credit> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)