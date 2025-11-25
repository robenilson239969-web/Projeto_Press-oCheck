package com.example.pressocheck_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pressocheck_final.database.PressureDatabase
import com.example.pressocheck_final.navigation.NavGraph
import com.example.pressocheck_final.repository.PressureRepository
import com.example.pressocheck_final.ui.theme.PressãoCheckFinalTheme
import com.example.pressocheck_final.viewmodel.PressureViewModel

/**
 * Activity principal do aplicativo PressãoCheck.
 * 
 * Configura o banco de dados, repositório, ViewModel e navegação.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inicializar banco de dados e dependências
        val database = PressureDatabase.getDatabase(applicationContext)
        val repository = PressureRepository(database.pressureDao())
        val viewModelFactory = PressureViewModel.Factory(repository)
        
        setContent {
            PressãoCheckFinalTheme {
                val navController = rememberNavController()
                val viewModel: PressureViewModel = viewModel(factory = viewModelFactory)
                val snackbarHostState = remember { SnackbarHostState() }
                
                // Observar mensagens de erro e sucesso
                val errorMessage by viewModel.errorMessage.collectAsState()
                val successMessage by viewModel.successMessage.collectAsState()
                
                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearError()
                    }
                }
                
                LaunchedEffect(successMessage) {
                    successMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearSuccess()
                    }
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) {
                    NavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}