package com.example.alertatemprana.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alertatemprana.ui.screen.EvidenciaScreen
import com.example.alertatemprana.ui.screen.FormularioScreen
import com.example.alertatemprana.ui.screen.HomeScreen
import com.example.alertatemprana.viewmodel.GalponViewModel
import com.example.alertatemprana.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = viewModel(),
    galponViewModel: GalponViewModel = viewModel()
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        launchSingleTop = true
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(mainViewModel = mainViewModel, galponViewModel = galponViewModel)
        }
        composable(Screen.Formulario.route) {
            FormularioScreen(mainViewModel = mainViewModel, galponViewModel = galponViewModel)
        }
        composable(Screen.Evidencia.route) {
            EvidenciaScreen(mainViewModel = mainViewModel, galponViewModel = galponViewModel)
        }
    }
}