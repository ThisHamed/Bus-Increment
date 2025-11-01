package org.jahangostar.busincreasement.ui.screen.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.razaghimahdi.compose_persian_date.core.components.rememberDialogDatePicker
import com.razaghimahdi.compose_persian_date.dialog.PersianLinearDatePickerDialog
import org.jahangostar.busincreasement.data.model.Transaction
import org.jahangostar.busincreasement.data.model.TransactionRecord
import org.jahangostar.busincreasement.ui.components.TransactionsDialog
import org.jahangostar.busincreasement.util.Constants.formatCreditValue
import org.jahangostar.busincreasement.util.DigitHelper.digitByLocate
import org.jahangostar.busincreasement.viewmodel.DeviceReportViewModel
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceReportScreen(
    onNavigateUp: () -> Unit,
    viewModel: DeviceReportViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val unsyncedCount by viewModel.unsyncedTransactionsCount.collectAsState()
    val transactions = viewModel.transactionsPager.collectAsState().value.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showConfirmClearDialog by remember { mutableStateOf(false) }

    // --- Effects ---
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
            viewModel.onSnackbarShown()
        }
    }

    LaunchedEffect(uiState.unsyncedTransactions) {
        viewModel.updateSummaries(
            start = uiState.startDate.time,
            end = uiState.endDate.time
        )
    }

    val rememberDatePicker = rememberDialogDatePicker()

    if (showStartDatePicker) {

        var tempYear = uiState.startDate.shYear
        var tempMonth = uiState.startDate.shMonth
        var tempDay = uiState.startDate.shDay

        PersianLinearDatePickerDialog(
            rememberDatePicker,
            Modifier.fillMaxWidth(),
            onDismissRequest = {
                val newStartDate =
                    PersianDate().initJalaliDate(tempYear, tempMonth, tempDay, 0, 0, 0)
                viewModel.onDateRangeChanged(newStartDate, uiState.endDate)
                showStartDatePicker = false
            },
            onDateChanged = { year, month, day ->
                tempYear = year
                tempMonth = month
                tempDay = day
            }
        )
    }

    if (showEndDatePicker) {

        var tempYear = uiState.endDate.shYear
        var tempMonth = uiState.endDate.shMonth
        var tempDay = uiState.endDate.shDay

        PersianLinearDatePickerDialog(
            rememberDatePicker,
            Modifier.fillMaxWidth(),
            onDismissRequest = {
                val newEndDate =
                    PersianDate().initJalaliDate(tempYear, tempMonth, tempDay, 23, 59, 59)
                viewModel.onDateRangeChanged(uiState.startDate, newEndDate)
                showEndDatePicker = false
            },
            onDateChanged = { year, month, day ->
                tempYear = year
                tempMonth = month
                tempDay = day
            }
        )
    }

    if (showConfirmClearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmClearDialog = false },
            title = { Text("پاک کردن همه تراکنش‌ها") },
            text = { Text("آیا از حذف تمام تراکنش‌های محلی مطمئن هستید؟ این عمل غیرقابل بازگشت است.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllLocalTransactions()
                        showConfirmClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("حذف کن") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearDialog = false }) { Text("انصراف") }
            }
        )
    }

    if (uiState.isUnsyncedDialogVisible) {
        val transactionRecords = uiState.unsyncedTransactions.map {
            TransactionRecord(
                etebar = it.preCredit,
                op = it.operationType,
                year = it.regDate.shYear,
                month = it.regDate.shMonth,
                day = it.regDate.shDay,
                remEtebar = it.finalCredit,
                did = it.deviceId,
                hour = it.regDate.hour,
                minute = it.regDate.minute
            )
        }
        TransactionsDialog(
            transactions = transactionRecords,
            onDismissRequest = { viewModel.dismissUnsyncedTransactionsDialog() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("گزارش دستگاه", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.postUnsentTransactions() },
                        enabled = unsyncedCount > 0 && !uiState.isSyncing
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "همگام‌سازی تراکنش‌ها")
                    }
                    IconButton(onClick = { showConfirmClearDialog = true }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "پاک کردن همه")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
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
                    .padding(horizontal = 16.dp)
            ) {
                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateClick = { showStartDatePicker = true },
                    onEndDateClick = { showEndDatePicker = true }
                )
                Spacer(Modifier.height(16.dp))
                SummaryGrid(
                    totalCharge = uiState.totalCharge,
                    unsyncedCount = unsyncedCount,
                    onUnsyncedClick = { viewModel.showUnsyncedTransactions() }
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (transactions.loadState.refresh is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (transactions.loadState.refresh is LoadState.NotLoading && transactions.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("هیچ تراکنشی در این بازه یافت نشد.", color = Color.Gray)
                            }
                        }
                    }

                    items(
                        count = transactions.itemCount,
                        key = transactions.itemKey { it.id }
                    ) { index ->
                        val transaction = transactions[index]
                        transaction?.let { TransactionReportItem(it) }
                    }

                    if (transactions.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }

            if (uiState.isSyncing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

// --- Stateless Sub-Components ---

@Composable
private fun DateRangeSelector(
    startDate: PersianDate,
    endDate: PersianDate,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateChip(
            label = "از تاریخ:",
            date = startDate,
            onClick = onStartDateClick,
            modifier = Modifier.weight(1f)
        )
        DateChip(
            label = "تا تاریخ:",
            date = endDate,
            onClick = onEndDateClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateChip(
    label: String,
    date: PersianDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall)
                Text(
                    digitByLocate(date.toString()),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SummaryGrid(
    totalCharge: Long,
    unsyncedCount: Int,
    onUnsyncedClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(
                label = "مجموع شارژ",
                amount = totalCharge,
                icon = Icons.Default.AttachMoney,
                color = Color(0xFF00897B),
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            ClickableSummaryCard(
                label = "تراکنش‌های ارسال‌نشده",
                count = unsyncedCount,
                icon = Icons.Default.SyncProblem,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = onUnsyncedClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Long,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(
                    "${formatCreditValue(amount.toInt())} تومان",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClickableSummaryCard(
    label: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(
                    "${digitByLocate(count.toString())} تراکنش",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


/**
 * A visually enhanced composable that displays a single transaction item in a report list.
 * It uses icons, colors, and improved typography for a more user-friendly presentation.
 */
@Composable
private fun TransactionReportItem(transaction: Transaction) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val icon: ImageVector
            val iconColor: Color
            val iconBackgroundColor: Color
            val operationTypeText: String

            when (transaction.operationType) {
                1 -> {
                    icon = Icons.AutoMirrored.Filled.TrendingUp
                    iconColor = Color(0xFF00C853)
                    iconBackgroundColor = Color(0xFFE0F2F1)
                    operationTypeText = "شارژ"
                }

                2 -> {
                    icon = Icons.AutoMirrored.Filled.TrendingDown
                    iconColor = Color(0xFFD50000)
                    iconBackgroundColor = Color(0xFFFFEBEE)
                    operationTypeText = "کاهش"
                }

                else -> {
                    icon = Icons.AutoMirrored.Filled.HelpOutline
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
                    operationTypeText = "نامشخص ${transaction.operationType}"
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = operationTypeText,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${formatCreditValue(transaction.price)} تومان",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "کارت: ${digitByLocate(transaction.cardId.toString())}  •  نوع: $operationTypeText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val date = PersianDate(transaction.regDate.time)
                val dateFormat = PersianDateFormat("Y/m/d")
                val timeFormat = PersianDateFormat("H:i")
                val formattedDate = dateFormat.format(date)
                val formattedTime = timeFormat.format(date)
                Text(
                    text = digitByLocate(formattedDate),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = digitByLocate(formattedTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
