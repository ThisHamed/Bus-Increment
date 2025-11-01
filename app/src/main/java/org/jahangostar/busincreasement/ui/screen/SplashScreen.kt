package org.jahangostar.busincreasement.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import org.jahangostar.busincreasement.R
import org.jahangostar.busincreasement.ui.theme.BusIncreasementTheme
import org.jahangostar.busincreasement.viewmodel.SqlConnectionViewModel

@Composable
fun SplashScreen(
    viewModel: SqlConnectionViewModel,
    onNavigateToHome: () -> Unit,
) {
    val overallAlpha = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0.5f) }
    var showTexts by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    LaunchedEffect(key1 = true) {
        overallAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        showTexts = true
        viewModel.connect()
        delay(3000)
        onNavigateToHome()
    }

    BusIncreasementTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0F7FA), // Light Cyan/Blue (Top, less transparent)
                                Color(0xFFB3E5FC), // Lighter Blue (Middle)
                                Color(0xFF81D4FA)  // Light Blue (Bottom)
                            ),
                            startY = 0.0f,
                            endY = with(LocalDensity.current) { 2000.dp.toPx() }
                        )
                    )
                    .alpha(overallAlpha.value)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bus_card_splash),
                        contentDescription = "لوگو برنامه",
                        modifier = Modifier
                            .size(180.dp)
                            .graphicsLayer(
                                scaleX = iconScale.value,
                                scaleY = iconScale.value
                            )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(
                        visible = showTexts,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 700,
                                delayMillis = 100
                            )
                        ) +
                                slideInVertically(
                                    initialOffsetY = { with(density) { 40.dp.roundToPx() } },
                                    animationSpec = tween(durationMillis = 700, delayMillis = 100)
                                )
                    ) {
                        Text(
                            text = "شارژینو",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface, // Or a specific blue if theme isn't blue
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = showTexts,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 700,
                                delayMillis = 300
                            )
                        ) +
                                slideInVertically(
                                    initialOffsetY = { with(density) { 40.dp.roundToPx() } },
                                    animationSpec = tween(durationMillis = 700, delayMillis = 300)
                                )
                    ) {
                        Text(
                            text = "افزایش اعتبار کارت اتوبوس",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = if (MaterialTheme.colorScheme.primary == Color.Transparent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary, // Use primary if defined, else onSurface
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AnimatedVisibility(
                        visible = showTexts,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 700,
                                delayMillis = 500
                            )
                        ) +
                                slideInVertically(
                                    initialOffsetY = { with(density) { 40.dp.roundToPx() } },
                                    animationSpec = tween(durationMillis = 700, delayMillis = 500)
                                )
                    ) {
                        Text(
                            text = "به سرعت و آسانی",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                        )
                    }
                }
            }
        }
    }
}
