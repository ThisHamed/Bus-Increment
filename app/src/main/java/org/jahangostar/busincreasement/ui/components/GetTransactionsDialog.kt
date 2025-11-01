package org.jahangostar.busincreasement.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * A dialog to ask the user the source for fetching transaction history.
 *
 * @param onDismissRequest Called when the user cancels or dismisses the dialog.
 * @param onFromServer Called when the user chooses to get transactions from the server.
 * @param onFromCard Called when the user chooses to get transactions from the card.
 */
@Composable
fun GetTransactionsDialog(
    onDismissRequest: () -> Unit,
    onFromServer: () -> Unit,
    onFromCard: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "مشاهده تاریخچه",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "تراکنش‌ها از کدام منبع دریافت شوند؟",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onFromCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("دریافت از کارت")
                    }
                    OutlinedButton(
                        onClick = onFromServer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("دریافت از سرور")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("انصراف")
                }
            }
        }
    }
}
