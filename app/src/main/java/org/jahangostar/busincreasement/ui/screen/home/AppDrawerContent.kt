package org.jahangostar.busincreasement.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jahangostar.busincreasement.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onReadCardInfo: () -> Unit,
    onShowHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReport: () -> Unit,
    onRestartApp: () -> Unit
) {
    ModalDrawerSheet {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = R.drawable.banner,
                contentDescription = "Drawer Header Banner",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DrawerDefaults.modalContainerColor
                            ),
                            startY = 300f
                        )
                    )
            )
        }

        Spacer(Modifier.height(12.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.CreditCard, contentDescription = "خواندن کارت") },
            label = { Text("خواندن اطلاعات کارت") },
            selected = false,
            onClick = {
                onReadCardInfo()
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "تاریخچه") },
            label = { Text("نمایش تاریخچه تراکنش‌ها") },
            selected = false,
            onClick = {
                onShowHistory()
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Assessment, contentDescription = "گزارشات") },
            label = { Text("مشاهده گزارشات دستگاه") },
            selected = false,
            onClick = {
                onNavigateToReport()
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "تنظیمات") },
            label = { Text("تنظیمات") },
            selected = false,
            onClick = {
                onNavigateToSettings()
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.RestartAlt, contentDescription = "راه اندازی مجدد") },
            label = { Text("راه اندازی مجدد برنامه") },
            selected = false,
            onClick = {
                onRestartApp()
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}
