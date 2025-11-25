package com.example.pressocheck_final.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilitários para validação e classificação de pressão arterial.
 * 
 * Fornece funções para validar valores de pressão e determinar o nível de risco
 * baseado nas diretrizes médicas.
 */
object PressureUtils {
    
    // Limites recomendados pela OMS e Sociedade Brasileira de Cardiologia
    const val SISTOLICA_NORMAL_MAX = 120
    const val SISTOLICA_PRE_HIPERTENSAO = 140
    const val DIASTOLICA_NORMAL_MAX = 80
    const val DIASTOLICA_PRE_HIPERTENSAO = 90
    
    // Valores mínimos e máximos aceitáveis
    const val SISTOLICA_MIN = 70
    const val SISTOLICA_MAX = 250
    const val DIASTOLICA_MIN = 40
    const val DIASTOLICA_MAX = 150
    
    /**
     * Enum que representa a classificação da pressão arterial.
     */
    enum class PressureCategory {
        NORMAL,           // < 120/80
        PRE_HIPERTENSAO,  // 120-139/80-89
        HIPERTENSAO_1,    // 140-159/90-99
        HIPERTENSAO_2,    // >= 160/100
        CRITICA           // Valores extremamente altos
    }
    
    /**
     * Valida se os valores de pressão estão dentro dos limites aceitáveis.
     * 
     * @param sistolica Valor da pressão sistólica
     * @param diastolica Valor da pressão diastólica
     * @return true se os valores são válidos, false caso contrário
     */
    fun isValidPressure(sistolica: Int, diastolica: Int): Boolean {
        return sistolica in SISTOLICA_MIN..SISTOLICA_MAX &&
               diastolica in DIASTOLICA_MIN..DIASTOLICA_MAX &&
               sistolica > diastolica // Sistólica deve ser maior que diastólica
    }
    
    /**
     * Classifica a pressão arterial de acordo com as diretrizes médicas.
     * 
     * @param sistolica Valor da pressão sistólica
     * @param diastolica Valor da pressão diastólica
     * @return Categoria da pressão arterial
     */
    fun classifyPressure(sistolica: Int, diastolica: Int): PressureCategory {
        return when {
            sistolica >= 180 || diastolica >= 120 -> PressureCategory.CRITICA
            sistolica >= 160 || diastolica >= 100 -> PressureCategory.HIPERTENSAO_2
            sistolica >= 140 || diastolica >= 90 -> PressureCategory.HIPERTENSAO_1
            sistolica >= 120 || diastolica >= 80 -> PressureCategory.PRE_HIPERTENSAO
            else -> PressureCategory.NORMAL
        }
    }
    
    /**
     * Verifica se a pressão está fora dos limites recomendados (alerta de risco).
     * 
     * @param sistolica Valor da pressão sistólica
     * @param diastolica Valor da pressão diastólica
     * @return true se a pressão está acima dos limites recomendados
     */
    fun isHighPressure(sistolica: Int, diastolica: Int): Boolean {
        return sistolica >= SISTOLICA_PRE_HIPERTENSAO || diastolica >= DIASTOLICA_PRE_HIPERTENSAO
    }
    
    /**
     * Obtém uma mensagem descritiva para a categoria de pressão.
     * 
     * @param category Categoria da pressão
     * @return Mensagem descritiva
     */
    fun getCategoryMessage(category: PressureCategory): String {
        return when (category) {
            PressureCategory.NORMAL -> "Pressão Normal"
            PressureCategory.PRE_HIPERTENSAO -> "Pré-Hipertensão"
            PressureCategory.HIPERTENSAO_1 -> "Hipertensão Estágio 1"
            PressureCategory.HIPERTENSAO_2 -> "Hipertensão Estágio 2"
            PressureCategory.CRITICA -> "Crise Hipertensiva - Procure atendimento médico!"
        }
    }
    
    /**
     * Obtém a cor associada à categoria de pressão (para uso na UI).
     * 
     * @param category Categoria da pressão
     * @return Código hexadecimal da cor
     */
    fun getCategoryColor(category: PressureCategory): String {
        return when (category) {
            PressureCategory.NORMAL -> "#4CAF50" // Verde
            PressureCategory.PRE_HIPERTENSAO -> "#FF9800" // Laranja
            PressureCategory.HIPERTENSAO_1 -> "#F44336" // Vermelho claro
            PressureCategory.HIPERTENSAO_2 -> "#D32F2F" // Vermelho
            PressureCategory.CRITICA -> "#B71C1C" // Vermelho escuro
        }
    }
    
    /**
     * Formata uma data (timestamp) para string no formato brasileiro.
     * 
     * @param timestamp Timestamp em milissegundos
     * @return String formatada (dd/MM/yyyy)
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Obtém o timestamp do início do dia atual.
     * 
     * @return Timestamp do início do dia em milissegundos
     */
    fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Obtém o timestamp do início de uma data específica.
     * 
     * @param timestamp Timestamp de qualquer hora do dia
     * @return Timestamp do início do dia
     */
    fun getDayStartTimestamp(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

