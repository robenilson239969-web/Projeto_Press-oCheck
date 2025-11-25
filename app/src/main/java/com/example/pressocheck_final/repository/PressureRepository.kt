package com.example.pressocheck_final.repository

import com.example.pressocheck_final.dao.PressureDao
import com.example.pressocheck_final.model.PressureEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositório que atua como camada de abstração entre a ViewModel e o banco de dados.
 * 
 * Centraliza a lógica de acesso a dados e permite fácil substituição da fonte de dados
 * no futuro (ex: adicionar sincronização com servidor).
 * 
 * @param pressureDao Instância do DAO para acesso ao banco de dados
 */
class PressureRepository(private val pressureDao: PressureDao) {
    
    /**
     * Obtém todas as medições de pressão ordenadas por data.
     * 
     * @return Flow com lista de todas as medições
     */
    fun getAllPressures(): Flow<List<PressureEntity>> = pressureDao.getAllPressures()
    
    /**
     * Obtém uma medição específica pelo ID.
     * 
     * @param id ID da medição
     * @return A medição ou null se não encontrada
     */
    suspend fun getPressureById(id: Long): PressureEntity? = pressureDao.getPressureById(id)
    
    /**
     * Obtém medições de uma data específica.
     * 
     * @param dateTimestamp Timestamp da data
     * @return Flow com lista de medições do dia
     */
    fun getPressuresByDate(dateTimestamp: Long): Flow<List<PressureEntity>> = 
        pressureDao.getPressuresByDate(dateTimestamp)
    
    /**
     * Obtém as últimas N medições.
     * 
     * @param limit Número de medições
     * @return Flow com lista das últimas medições
     */
    fun getRecentPressures(limit: Int): Flow<List<PressureEntity>> = 
        pressureDao.getRecentPressures(limit)
    
    /**
     * Insere uma nova medição de pressão.
     * 
     * @param pressure A medição a ser inserida
     * @return ID da medição inserida
     */
    suspend fun insertPressure(pressure: PressureEntity): Long = 
        pressureDao.insertPressure(pressure)
    
    /**
     * Atualiza uma medição existente.
     * 
     * @param pressure A medição atualizada
     */
    suspend fun updatePressure(pressure: PressureEntity) = 
        pressureDao.updatePressure(pressure)
    
    /**
     * Remove uma medição do banco de dados.
     * 
     * @param pressure A medição a ser removida
     */
    suspend fun deletePressure(pressure: PressureEntity) = 
        pressureDao.deletePressure(pressure)
    
    /**
     * Remove uma medição pelo ID.
     * 
     * @param id ID da medição a ser removida
     */
    suspend fun deletePressureById(id: Long) = 
        pressureDao.deletePressureById(id)
}

