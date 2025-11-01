package org.jahangostar.busincreasement.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jahangostar.busincreasement.util.Constants.formatCreditValue

/**
 * A dialog to confirm the user's intended action for a selected top-up amount.
 *
 * @param amount The monetary value the user selected.
 * @param onDismissRequest Called when the user cancels or dismisses the dialog.
 * @param onConfirmTopUp Called when the user confirms they want to top-up the bus card.
 * @param onConfirmCash Called when the user confirms they want to pay with cash credit.
 */
@Composable
fun ConfirmTopUpDialog(
    amount: Int,
    onDismissRequest: () -> Unit,
    onConfirmTopUp: () -> Unit,
    onConfirmCash: () -> Unit
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "انتخاب نوع پرداخت",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "مبلغ انتخاب شده:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${formatCreditValue(amount)} تومان",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "این مبلغ را چگونه پرداخت می‌کنید؟",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onConfirmTopUp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("پرداخت با کارت بانکی")
                    }
                    OutlinedButton(
                        onClick = onConfirmCash,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("پرداخت نقدی")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
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
