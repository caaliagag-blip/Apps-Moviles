package com.example.alertatemprana.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object Formulario : Screen("formulario_page")
    data object Evidencia : Screen("evidencia_page")
}

sealed class NavigationEvent {
    data class NavigateTo(val route: Screen) : NavigationEvent()
    data object PopBackStack : NavigationEvent()
}