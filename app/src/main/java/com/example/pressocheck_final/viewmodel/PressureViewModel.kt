package com.example.pressocheck_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pressocheck_final.model.PressureEntity
import com.example.pressocheck_final.repository.PressureRepository
import com.example.pressocheck_final.utils.PressureUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel que gerencia o estado e a lógica de negócio relacionada às medições de pressão.
 * 
 * Utiliza StateFlow para gerenciamento reativo de estado e corrotinas para operações assíncronas.
 * 
 * @param repository Repositório para acesso aos dados
 */
class PressureViewModel(private val repository: PressureRepository) : ViewModel() {
    
    // Estado da lista de medições
    private val _pressures = MutableStateFlow<List<PressureEntity>>(emptyList())
    val pressures: StateFlow<List<PressureEntity>> = _pressures.asStateFlow()
    
    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado de erro
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Estado de sucesso (para feedback após salvar)
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    init {
        loadAllPressures()
    }
    
    /**
     * Carrega todas as medições do banco de dados.
     */
    private fun loadAllPressures() {
        viewModelScope.launch {
            repository.getAllPressures().collect { pressureList ->
                _pressures.value = pressureList
            }
        }
    }
    
    /**
     * Insere uma nova medição de pressão.
     * 
     * @param sistolica Valor da pressão sistólica
     * @param diastolica Valor da pressão diastólica
     * @param observacao Observações opcionais
     */
    fun insertPressure(sistolica: Int, diastolica: Int, observacao: String = "") {
        viewModelScope.launch {
            try {
                // Validação
                if (!PressureUtils.isValidPressure(sistolica, diastolica)) {
                    _errorMessage.value = "Valores de pressão inválidos. Verifique os valores inseridos."
                    return@launch
                }
                
                _isLoading.value = true
                _errorMessage.value = null
                
                // Obter data e hora atual
                val now = System.currentTimeMillis()
                val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
                val hora = timeFormat.format(Date(now))
                
                val pressure = PressureEntity(
                    sistolica = sistolica,
                    diastolica = diastolica,
                    data = now,
                    hora = hora,
                    observacao = observacao
                )
                
                repository.insertPressure(pressure)
                
                // Verificar se há alerta de risco
                val category = PressureUtils.classifyPressure(sistolica, diastolica)
                if (category != PressureUtils.PressureCategory.NORMAL) {
                    _successMessage.value = "Medição registrada! ${PressureUtils.getCategoryMessage(category)}"
                } else {
                    _successMessage.value = "Medição registrada com sucesso!"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao salvar medição: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Atualiza uma medição existente.
     * 
     * @param pressure A medição atualizada
     */
    fun updatePressure(pressure: PressureEntity) {
        viewModelScope.launch {
            try {
                if (!PressureUtils.isValidPressure(pressure.sistolica, pressure.diastolica)) {
                    _errorMessage.value = "Valores de pressão inválidos. Verifique os valores inseridos."
                    return@launch
                }
                
                _isLoading.value = true
                _errorMessage.value = null
                
                repository.updatePressure(pressure)
                _successMessage.value = "Medição atualizada com sucesso!"
                
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao atualizar medição: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Remove uma medição do banco de dados.
     * 
     * @param pressure A medição a ser removida
     */
    fun deletePressure(pressure: PressureEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                repository.deletePressure(pressure)
                _successMessage.value = "Medição excluída com sucesso!"
                
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao excluir medição: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Remove uma medição pelo ID.
     * 
     * @param id ID da medição a ser removida
     */
    fun deletePressureById(id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                repository.deletePressureById(id)
                _successMessage.value = "Medição excluída com sucesso!"
                
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao excluir medição: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpa a mensagem de erro.
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Limpa a mensagem de sucesso.
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
    
    /**
     * Factory para criar instâncias do ViewModel com injeção de dependência.
     */
    class Factory(private val repository: PressureRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PressureViewModel::class.java)) {
                return PressureViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

