package org.jahangostar.busincreasement.ui.screen.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jahangostar.busincreasement.data.model.NetworkState
import org.jahangostar.busincreasement.data.model.SqlServerEvent
import org.jahangostar.busincreasement.data.model.TechPayTransaction
import org.jahangostar.busincreasement.ui.components.ConfirmTopUpDialog
import org.jahangostar.busincreasement.ui.components.GetTransactionsDialog
import org.jahangostar.busincreasement.ui.components.LoginOrSetupDialog
import org.jahangostar.busincreasement.ui.components.PasswordPromptDialog
import org.jahangostar.busincreasement.ui.components.ResultDialog
import org.jahangostar.busincreasement.ui.components.TransactionsDialog
import org.jahangostar.busincreasement.ui.components.WaitingForCardDialog
import org.jahangostar.busincreasement.util.Constants.formatCreditValue
import org.jahangostar.busincreasement.util.Constants.predefinedAmounts
import org.jahangostar.busincreasement.util.TosanPayment.launchTechPay
import org.jahangostar.busincreasement.util.TosanPayment.parseTransaction
import org.jahangostar.busincreasement.viewmodel.HomeScreenViewModel
import org.jahangostar.busincreasement.viewmodel.SettingsViewModel
import org.jahangostar.busincreasement.viewmodel.SqlConnectionViewModel
import org.jahangostar.busincreasement.viewmodel.SqlServerViewModel
import kotlin.system.exitProcess

/**
 * The main screen of the application for managing and topping up a bus card.
 *
 * This screen is stateless and observes its UI state from a [HomeScreenViewModel],
 * sending events back on user interaction. It handles various dialogs and user flows
 * for reading card info, viewing history, and performing top-up transactions.
 *
 * @param onNavigateToSettings A callback to navigate to the settings screen.
 * @param viewModel The Hilt-injected ViewModel that manages the screen's state and logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel,
    sqlConnectionViewModel: SqlConnectionViewModel,
    sqlServerViewModel: SqlServerViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToReport: () -> Unit,
    newIntent: Intent?,
    onNewIntentHandled: () -> Unit
) {
    val uiState = viewModel.uiState
    val state by sqlServerViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val serverMessage by sqlConnectionViewModel.serverMessage.collectAsState()
    val sqlConnectionState by sqlConnectionViewModel.sqlState.collectAsState()
    var sqlConnectionIcon by remember { mutableStateOf(Icons.Default.CloudOff) }

    val device by settingsViewModel.device.collectAsState()

    val activity = LocalActivity.current

    var isInitialUserCheckComplete by remember { mutableStateOf(false) }

    LaunchedEffect(state.localUsers) {
        if (!isInitialUserCheckComplete) {
            isInitialUserCheckComplete = true
        }
    }

    LaunchedEffect(newIntent) {
        newIntent?.let { intent ->
            val TAG = "ON_NEW_INTENT"
            Log.i(TAG, "HomeScreen handling new intent: $intent")

            val resultJson = intent.getStringExtra("transaction")
            Log.i(TAG, "Received transaction JSON from TechPay: $resultJson")

            if (resultJson.isNullOrBlank()) {
                Log.w(TAG, "Intent received but 'transaction' extra was null or empty.")
                snackbarHostState.showSnackbar("پاسخ پرداخت دریافت شد اما حاوی اطلاعات نبود.")
                onNewIntentHandled()
                return@let
            }

            try {
                val parsedResult = parseTransaction(resultJson)

                if (parsedResult.status == "0") {
                    Log.i(
                        TAG,
                        "Payment SUCCESSFUL. Parsed amount: ${parsedResult.amount}. Triggering card top-up."
                    )

                    val requestedAmount = if (uiState.dialogState is DialogState.ShowConfirmTopUp) {
                        uiState.dialogState.amount
                    } else {
                        parsedResult.amount?.toIntOrNull() ?: 0
                    }

                    val cardUc = device?.uc?.toIntOrNull() ?: 1

                    if (requestedAmount > 0) {
                        viewModel.onEvent(
                            HomeScreenEvent.TopUpCard(context, requestedAmount),
                            cardUc
                        )
                    } else {
                        Log.e(
                            TAG,
                            "Payment was successful but could not determine the amount to top up."
                        )
                        snackbarHostState.showSnackbar("پرداخت موفق بود اما مبلغ شارژ نامشخص است.")
                    }

                } else {
                    val errorMessage = parsedResult.responseMessage ?: "پرداخت ناموفق بود."
                    Log.e(
                        TAG,
                        "Payment FAILED. Status: ${parsedResult.status}, Message: $errorMessage"
                    )
                    snackbarHostState.showSnackbar("خطای پرداخت: $errorMessage")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse transaction JSON or handle the intent.", e)
                snackbarHostState.showSnackbar("خطا در پردازش پاسخ پرداخت.")
            }

            onNewIntentHandled()
        }
    }


    LaunchedEffect(device) {
        device?.let { fetchedDevice ->

            sqlServerViewModel.fetchRemoteCredits(fetchedDevice.deviceId.toInt())
        }
    }

    if (isInitialUserCheckComplete && state.loggedInUser == null) {
        LoginOrSetupDialog(
            users = state.localUsers,
            onLoginSuccess = { loggedInUser ->
                sqlServerViewModel.onUserLoggedIn(loggedInUser)
                val currentDevice = device
                val currentBalance = state.balance ?: 0
                viewModel.updateOperatorId(loggedInUser.id)
                if (currentDevice != null) {
                    sqlServerViewModel.syncBalance(
                        currentDevice.deviceId.toInt(),
                        currentBalance,
                        loggedInUser.id
                    )
                }
            },
            onNavigateToSettings = {
                onNavigateToSettings()
            }
        )
    }

    LaunchedEffect(state.localUsers) {
        if (state.localUsers.isNotEmpty()) {
            Log.d("HomeScreen", "Users: ${state.users}")
        }
    }

    state.snackbarMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            sqlServerViewModel.clearSnackbarMessage()
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(HomeScreenEvent.SnackbarShown, device?.uc?.toIntOrNull() ?: 1)
        }
    }

    LaunchedEffect(serverMessage) {
        serverMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            sqlConnectionViewModel.onEvent(SqlServerEvent.ServerMessageShown)
        }
    }

    val config = sqlConnectionViewModel.serverConfig.collectAsState()
    LaunchedEffect(sqlConnectionState) {
        while (true) {
            val currentState = sqlConnectionState
            delay(10_000)
            if (
                currentState is NetworkState.Disconnected
                || currentState is NetworkState.NoNetwork
                && config.value != null
            ) {
                sqlConnectionViewModel.connect()
            }
        }
    }

    LaunchedEffect(sqlConnectionState) {
        sqlConnectionIcon = when (sqlConnectionState) {
            is NetworkState.Connected -> {
                Icons.Default.CloudDone
            }

            is NetworkState.Connecting -> {
                Icons.Default.CloudSync
            }

            is NetworkState.Disconnected -> {
                Icons.Default.CloudOff
            }

            is NetworkState.NoNetwork -> {
                Icons.Default.WifiOff
            }
        }
    }

    when (val dialogState = uiState.dialogState) {
        is DialogState.Hidden -> {}
        is DialogState.ShowPasswordPrompt -> {
            PasswordPromptDialog(
                onDismissRequest = {
                    viewModel.onEvent(
                        HomeScreenEvent.DismissDialog,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onPasswordVerified = {
                    viewModel.onEvent(HomeScreenEvent.DismissDialog, device?.uc?.toIntOrNull() ?: 1)
                    onNavigateToSettings()
                },
                correctPassword = device?.devicePassword ?: "1",
                snackbarHostState = snackbarHostState
            )
        }

        is DialogState.ShowTransactions -> {
            TransactionsDialog(
                transactions = uiState.transactionList,
                onDismissRequest = {
                    viewModel.onEvent(
                        HomeScreenEvent.DismissDialog,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                }
            )
        }

        is DialogState.ShowWaitingForCard -> WaitingForCardDialog()
        is DialogState.ShowResult -> {
            ResultDialog(
                isSuccess = dialogState.isSuccess,
                onDismissRequest = {
                    viewModel.onEvent(
                        HomeScreenEvent.DismissDialog,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                }
            )
        }

        is DialogState.ShowConfirmTopUp -> {
            ConfirmTopUpDialog(
                amount = dialogState.amount,
                onDismissRequest = {
                    viewModel.onEvent(
                        HomeScreenEvent.DismissDialog,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onConfirmCash = {
                    viewModel.onEvent(
                        HomeScreenEvent.ConfirmCashPayment(
                            context = context,
                            amount = dialogState.amount
                        ),
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onConfirmTopUp = {
                    activity?.let { activity ->
                        if (isPackageInstalled(activity.packageManager)) {

                            if (!isValidAmount(dialogState.amount.toString())) {
                                showToast(
                                    "مبلغ نامعتبر. مبلغ باید بین ۲,۰۰۰ تا ۱,۰۰۰,۰۰۰,۰۰۰ ریال باشد.",
                                    context
                                )
                                return@let
                            }
                            launchTechPay(
                                amount = dialogState.amount.toString()
                                    .trim(),
                                context = context
                            )
                        } else {
                            showToast("اپلیکیشن TechPay نصب نشده است.", context)
                        }

                    }
                }
            )
        }

        is DialogState.ShowGetTransactionSource -> {
            GetTransactionsDialog(
                onDismissRequest = {
                    viewModel.onEvent(
                        HomeScreenEvent.DismissDialog,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onFromServer = {
                    viewModel.onEvent(
                        HomeScreenEvent.GetHistoryFromServer(context),
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onFromCard = {
                    viewModel.onEvent(
                        HomeScreenEvent.GetHistoryFromCard(context),
                        device?.uc?.toIntOrNull() ?: 1
                    )
                }
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                scope = scope,
                drawerState = drawerState,
                onReadCardInfo = {
                    viewModel.onEvent(
                        HomeScreenEvent.ReadCardInfo(context),
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onShowHistory = {
                    viewModel.onEvent(
                        HomeScreenEvent.ShowGetHistoryDialog,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onNavigateToSettings = {
                    viewModel.onEvent(
                        HomeScreenEvent.RequestSettingsAccess,
                        device?.uc?.toIntOrNull() ?: 1
                    )
                },
                onNavigateToReport = onNavigateToReport,
                onRestartApp = { activity?.let { restartApp(it) } }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                HomeScreenTopBar(
                    sqlConnectionIcon = sqlConnectionIcon,
                    onSqlConnectionIconClick = { sqlConnectionViewModel.onEvent(SqlServerEvent.TestConnection) },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0F7FA),
                                Color(0xFFB3E5FC),
                                Color(0xFF81D4FA)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CurrentCreditCard(credit = uiState.currentCredit)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "مبلغ مورد نظر برای شارژ را انتخاب کنید",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TopUpOptionsGrid(
                        amounts = state.credits.ifEmpty { predefinedAmounts },
                        onAmountSelected = {
                            viewModel.onEvent(
                                HomeScreenEvent.PredefinedAmountSelected(
                                    it
                                ),
                                device?.uc?.toIntOrNull() ?: 1
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomAmountInput(
                        amount = uiState.customAmount,
                        onAmountChange = {
                            viewModel.onEvent(
                                HomeScreenEvent.CustomAmountEntered(it),
                                device?.uc?.toIntOrNull() ?: 1
                            )
                        },
                        onConfirm = {
                            viewModel.onEvent(
                                HomeScreenEvent.CustomAmountConfirmed,
                                device?.uc?.toIntOrNull() ?: 1
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "اعتبار نقدی دستگاه:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${formatCreditValue(state.balance ?: 0)} تومان",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun restartApp(activity: Activity) {
    val packageManager = activity.packageManager
    val intent = packageManager.getLaunchIntentForPackage(activity.packageName)
    val componentName = intent!!.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    activity.startActivity(mainIntent)
    exitProcess(0)
}

private fun isPackageInstalled(
    pm: PackageManager
): Boolean {
    return try {
        pm.getPackageInfo("com.tech.pay", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        false
    }
}

private fun isValidAmount(amountStr: String): Boolean {
    return try {
        val amount = amountStr.toLong()
        amount in 2000..1_000_000_000
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        false
    }
}

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}