package org.jahangostar.busincreasement.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline // M3 Outlined variant
import androidx.compose.material.icons.filled.HighlightOff        // M3 Error/Cancel variant
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SqlConnectionResponseDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    isSuccess: Boolean,
    message: String,
    title: String = "اتصال به پایگاه داده",
    autoDismissDelayMillis: Long = 6000L
) {
    if (showDialog) {
        val icon: ImageVector
        val iconColor: Color
        val titleColor: Color

        if (isSuccess) {
            icon = Icons.Filled.CheckCircleOutline // Using outlined for a lighter M3 feel
            iconColor = MaterialTheme.colorScheme.primary
            titleColor = MaterialTheme.colorScheme.onSurface
        } else {
            icon = Icons.Filled.HighlightOff // Clearer error/off icon
            iconColor = MaterialTheme.colorScheme.error
            titleColor = MaterialTheme.colorScheme.error
        }

        LaunchedEffect(Unit) {
            delay(autoDismissDelayMillis)
            onDismissRequest()
        }

        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true,
                usePlatformDefaultWidth = false // Allows custom width if needed via modifier
            ),
            modifier = Modifier.padding(horizontal = 24.dp) // Standard M3 dialog padding
        ) {
            Surface(
                shape = AlertDialogDefaults.shape, // Uses M3 default dialog shape (more rounded)
                color = AlertDialogDefaults.containerColor, // M3 dialog container color
                tonalElevation = AlertDialogDefaults.TonalElevation // M3 tonal elevation
            ) {
                Column(
                    modifier = Modifier.padding(24.dp), // M3 standard content padding
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (isSuccess) "موفق" else "خطا",
                        tint = iconColor,
                        modifier = Modifier.size(40.dp) // Slightly smaller icon, typical for M3 info
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = titleColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Muted for body
                        textAlign = TextAlign.Center // Center message text for this style
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center // Center the button
                    ) {
                        Button( // Using a filled button for primary action
                            onClick = onDismissRequest,
                            shape = MaterialTheme.shapes.medium, // M3 button shape
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer,
                                contentColor = if (isSuccess) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text("تایید")
                        }
                    }
                }
            }
        }
    }
}


// --- Preview ---
@Preview(locale = "fa", name = "M3 Success Dialog", showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun M3SuccessDialogPreview() {
    MaterialTheme { // Ensure MaterialTheme is applied for M3 styles
        var showDialog by remember { mutableStateOf(true) } // Keep dialog visible for preview
        SqlConnectionResponseDialog(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            isSuccess = true,
            message = "اتصال با موفقیت به سرور SQL برقرار شد.",
            title = "عملیات موفق"
        )
    }
}

@Preview(locale = "fa", name = "M3 Failure Dialog", showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun M3FailureDialogPreview() {
    MaterialTheme {
        var showDialog by remember { mutableStateOf(true) }
        SqlConnectionResponseDialog(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            isSuccess = false,
            message = "خطا در برقراری اتصال به سرور SQL. لطفاً تنظیمات را بررسی کنید یا با پشتیبانی تماس بگیرید.",
            title = "خطا در اتصال"
        )
    }
}

