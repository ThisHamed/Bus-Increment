package org.jahangostar.busincreasement.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jahangostar.busincreasement.ui.screen.SettingsScreen
import org.jahangostar.busincreasement.ui.screen.SplashScreen
import org.jahangostar.busincreasement.ui.screen.home.HomeScreen
import org.jahangostar.busincreasement.ui.screen.report.DeviceReportScreen
import org.jahangostar.busincreasement.viewmodel.DeviceReportViewModel
import org.jahangostar.busincreasement.viewmodel.SettingsViewModel
import org.jahangostar.busincreasement.viewmodel.SqlConnectionViewModel
import org.jahangostar.busincreasement.viewmodel.SqlServerViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    newIntent: Intent?,
    onNewIntentHandled: () -> Unit,
    modifier: Modifier = Modifier
) {

    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val sqlServerViewModel = hiltViewModel<SqlServerViewModel>()
    val sqlConnectionViewModel = hiltViewModel<SqlConnectionViewModel>()
    val reportViewModel = hiltViewModel<DeviceReportViewModel>()

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route,
        modifier = modifier
    ) {
        composable(Screens.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                },
                viewModel = sqlConnectionViewModel
            )
        }
        composable(Screens.Home.route) {
            HomeScreen(
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = {
                    navController.navigate(Screens.Settings.route)
                },
                onNavigateToReport = {
                    navController.navigate(Screens.Report.route)
                },
                sqlConnectionViewModel = sqlConnectionViewModel,
                sqlServerViewModel = sqlServerViewModel,
                newIntent = newIntent,
                onNewIntentHandled = onNewIntentHandled
            )
        }

        composable(Screens.Report.route) {
            DeviceReportScreen(
                onNavigateUp = {
                    navController.popBackStack()
                },
                viewModel = reportViewModel
            )
        }

        composable(Screens.Settings.route) {
            SettingsScreen(settingsViewModel, sqlConnectionViewModel)
        }


    }

}