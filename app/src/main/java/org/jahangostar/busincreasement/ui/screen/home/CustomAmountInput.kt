package org.jahangostar.busincreasement.ui.screen.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.jahangostar.busincreasement.util.PersianNumberVisualTransformation

/**
 * A stateless composable for entering a custom top-up amount.
 * The confirmation action is now a clickable icon inside the text field.
 */
@Composable
fun CustomAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Text(
        "یا مبلغ دلخواه را وارد کنید",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("مبلغ به تومان") },
        visualTransformation = PersianNumberVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onConfirm() }
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            IconButton(
                onClick = onConfirm,
                enabled = amount.isNotBlank() && (amount.toIntOrNull() ?: 0) > 0
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "تایید مبلغ",
                    tint = if (amount.isNotBlank() && (amount.toIntOrNull() ?: 0) > 0) {
                        Color(0xFF00C853)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) // Grayed out when disabled
                    }
                )
            }
        }
    )
}