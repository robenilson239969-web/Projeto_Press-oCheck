package com.example.pressocheck_final.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pressocheck_final.ui.screens.ChartScreen
import com.example.pressocheck_final.ui.screens.PressureFormScreen
import com.example.pressocheck_final.ui.screens.PressureListScreen
import com.example.pressocheck_final.viewmodel.PressureViewModel

/**
 * Define as rotas de navegação do aplicativo.
 */
sealed class Screen(val route: String) {
    object List : Screen("pressure_list")
    object Add : Screen("pressure_add")
    object Edit : Screen("pressure_edit/{id}") {
        fun createRoute(id: Long) = "pressure_edit/$id"
    }
    object Chart : Screen("pressure_chart")
}

/**
 * Configuração do gráfico de navegação do aplicativo.
 * 
 * @param navController Controlador de navegação
 * @param viewModel ViewModel compartilhado
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: PressureViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        composable(Screen.List.route) {
            PressureListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Screen.Add.route) },
                onEditClick = { id ->
                    navController.navigate(Screen.Edit.createRoute(id))
                },
                onChartClick = { navController.navigate(Screen.Chart.route) }
            )
        }
        
        composable(Screen.Add.route) {
            PressureFormScreen(
                viewModel = viewModel,
                pressureId = null,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")
            PressureFormScreen(
                viewModel = viewModel,
                pressureId = id,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Chart.route) {
            ChartScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

