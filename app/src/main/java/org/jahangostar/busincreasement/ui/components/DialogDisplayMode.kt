package org.jahangostar.busincreasement.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import org.jahangostar.busincreasement.data.model.User

private sealed class DialogDisplayMode {
    data object Loading : DialogDisplayMode()
    data object NeedsSetup : DialogDisplayMode()
    data class Login(val users: List<User>) : DialogDisplayMode()
}

/**
 * A dialog that intelligently adapts its UI based on the presence of local users.
 * It shows a brief loading state before deciding whether to show the Login form
 * or the "Setup Required" prompt.
 *
 * @param users The list of users from the local database. Can be null during initial composition.
 * @param onLoginSuccess Callback invoked when the correct password is entered.
 * @param onNavigateToSettings Callback invoked for initial setup.
 */
@Composable
fun LoginOrSetupDialog(
    users: List<User>?,
    onLoginSuccess: (User) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var displayMode by remember { mutableStateOf<DialogDisplayMode>(DialogDisplayMode.Loading) }

    LaunchedEffect(users) {
        delay(1000)
        users?.let {
            displayMode = if (it.isEmpty()) {
                DialogDisplayMode.NeedsSetup
            } else {
                DialogDisplayMode.Login(it)
            }
        }
    }

    Dialog(
        onDismissRequest = { /* This dialog cannot be dismissed */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            when (val mode = displayMode) {
                is DialogDisplayMode.Loading -> LoadingMode()
                is DialogDisplayMode.NeedsSetup -> SetupRequiredMode(onNavigateToSettings)
                is DialogDisplayMode.Login -> LoginMode(mode.users, onLoginSuccess)
            }
        }
    }
}

@Composable
private fun LoadingMode() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = "در حال بارگذاری اطلاعات...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SetupRequiredMode(onNavigateToSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Information",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "نیاز به راه اندازی اولیه",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "هیچ کاربری در دستگاه یافت نشد. لطفاً ابتدا به بخش تنظیمات رفته و پس از ثبت اطلاعات سرور، کاربران را همگام‌سازی کنید.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("رفتن به تنظیمات")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginMode(
    users: List<User>,
    onLoginSuccess: (User) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf(users.firstOrNull()) }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Login,
            contentDescription = "Login",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "ورود اپراتور",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedUser?.name ?: "کاربر را انتخاب کنید",
                onValueChange = {},
                readOnly = true,
                label = { Text("نام کاربری") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                users.forEachIndexed { index, user ->
                    DropdownMenuItem(
                        text = { Text(user.name, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            selectedUser = user
                            expanded = false
                        }
                    )
                    if (index < users.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                loginError = null
            },
            label = { Text("رمز عبور") },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(image, "Show/Hide Password")
                }
            },
            isError = loginError != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (loginError != null) {
            Text(
                text = loginError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (selectedUser?.pass == password) {
                    onLoginSuccess(selectedUser!!)
                } else {
                    loginError = "رمز عبور اشتباه است."
                }
            },
            enabled = selectedUser != null && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ورود")
        }
    }
}
