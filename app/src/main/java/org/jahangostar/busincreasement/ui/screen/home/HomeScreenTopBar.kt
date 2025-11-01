package org.jahangostar.busincreasement.ui.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    sqlConnectionIcon: ImageVector,
    onSqlConnectionIconClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text("شارژ کارت اتوبوس", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "باز کردن منو")
            }
        },
        actions = {
            IconButton(onClick = onSqlConnectionIconClick) {
                Icon(
                    imageVector = sqlConnectionIcon,
                    contentDescription = "بررسی وضعیت ارتباط با سرور",
                    tint = when (sqlConnectionIcon) {
                        Icons.Default.CloudDone -> Color(0xFF00C853)
                        Icons.Default.CloudSync -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    )
}