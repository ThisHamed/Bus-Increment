package org.jahangostar.busincreasement.ui.navigation

sealed class Screens(val route: String) {

    data object Splash : Screens("splash_screen")
    data object Home : Screens("home_screen")
    data object Settings : Screens("settings_screen")
    data object Report: Screens("report_screen")

}