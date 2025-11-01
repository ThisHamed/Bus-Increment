package org.jahangostar.busincreasement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jahangostar.busincreasement.data.model.TransactionRecord
import org.jahangostar.busincreasement.util.DigitHelper

/**
 * یک دیالوگ برای نمایش لیستی از تراکنش‌ها.
 *
 * @param transactions لیستی از رکوردهای تراکنش برای نمایش.
 * @param onDismissRequest تابعی که هنگام درخواست بسته شدن دیالوگ فراخوانی می‌شود.
 */
@Composable
fun TransactionsDialog(
    transactions: List<TransactionRecord>,
    onDismissRequest: () -> Unit
) {
    val sortedTransactions = transactions.sortedWith(
        compareByDescending<TransactionRecord> { it.year }
            .thenByDescending { it.month }
            .thenByDescending { it.day }
            .thenByDescending { it.hour }
            .thenByDescending { it.minute }
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 550.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "تاریخچه تراکنش‌ها",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (sortedTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هیچ تراکنشی برای نمایش وجود ندارد.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(
                            items = sortedTransactions,
                        ) { index, transaction ->
                            TransactionListItem(transaction)
                            if (index < sortedTransactions.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        "بستن",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}


@Composable
private fun TransactionListItem(transaction: TransactionRecord) {
    val transactionAmount = transaction.remEtebar - transaction.etebar

    val icon: ImageVector
    val iconColor: Color
    val iconBackgroundColor: Color
    val operationTypeText: String

    when (transaction.op) {
        1 -> { // شارژ
            icon = Icons.AutoMirrored.Filled.TrendingUp
            iconColor = Color(0xFF00C853)
            iconBackgroundColor = Color(0xFFE0F2F1)
            operationTypeText = "شارژ"
        }

        2 -> { // کاهش
            icon = Icons.AutoMirrored.Filled.TrendingDown
            iconColor = Color(0xFFD50000)
            iconBackgroundColor = Color(0xFFFFEBEE)
            operationTypeText = "کاهش"
        }

        else -> { // نامشخص
            icon = Icons.AutoMirrored.Filled.HelpOutline
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
            iconBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
            operationTypeText = "نامشخص ${transaction.op}"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                text = "${DigitHelper.digitByLocateAndSeparator(transactionAmount.toString())} تومان",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "مانده: ${DigitHelper.digitByLocateAndSeparator(transaction.remEtebar.toString())} تومان",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = DigitHelper.digitByLocate("${transaction.year}/${transaction.month}/${transaction.day}"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = DigitHelper.digitByLocate(
                    "${"%02d".format(transaction.hour)}:${
                        "%02d".format(
                            transaction.minute
                        )
                    }"
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "دستگاه: ${DigitHelper.digitByLocate(transaction.did.toString())}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}