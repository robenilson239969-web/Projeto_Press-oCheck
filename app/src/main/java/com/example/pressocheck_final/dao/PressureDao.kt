package com.example.pressocheck_final.dao

import androidx.room.*
import com.example.pressocheck_final.model.PressureEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operações de banco de dados relacionadas a medições de pressão.
 * 
 * Fornece métodos para CRUD completo (Create, Read, Update, Delete) usando corrotinas e Flow
 * para operações reativas e assíncronas.
 */
@Dao
interface PressureDao {
    
    /**
     * Insere uma nova medição de pressão no banco de dados.
     * 
     * @param pressure A entidade de pressão a ser inserida
     * @return O ID da medição inserida
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPressure(pressure: PressureEntity): Long
    
    /**
     * Atualiza uma medição de pressão existente.
     * 
     * @param pressure A entidade de pressão atualizada
     */
    @Update
    suspend fun updatePressure(pressure: PressureEntity)
    
    /**
     * Remove uma medição de pressão do banco de dados.
     * 
     * @param pressure A entidade de pressão a ser removida
     */
    @Delete
    suspend fun deletePressure(pressure: PressureEntity)
    
    /**
     * Remove uma medição de pressão pelo ID.
     * 
     * @param id O ID da medição a ser removida
     */
    @Query("DELETE FROM pressure_measurements WHERE id = :id")
    suspend fun deletePressureById(id: Long)
    
    /**
     * Obtém uma medição de pressão pelo ID.
     * 
     * @param id O ID da medição
     * @return A entidade de pressão ou null se não encontrada
     */
    @Query("SELECT * FROM pressure_measurements WHERE id = :id")
    suspend fun getPressureById(id: Long): PressureEntity?
    
    /**
     * Obtém todas as medições de pressão ordenadas por data (mais recente primeiro).
     * 
     * @return Flow com lista de todas as medições
     */
    @Query("SELECT * FROM pressure_measurements ORDER BY data DESC, hora DESC")
    fun getAllPressures(): Flow<List<PressureEntity>>
    
    /**
     * Obtém todas as medições de pressão de uma data específica.
     * 
     * @param dateTimestamp Timestamp da data (início do dia em milissegundos)
     * @return Flow com lista de medições do dia
     */
    @Query("SELECT * FROM pressure_measurements WHERE data >= :dateTimestamp AND data < :dateTimestamp + 86400000 ORDER BY hora DESC")
    fun getPressuresByDate(dateTimestamp: Long): Flow<List<PressureEntity>>
    
    /**
     * Obtém as últimas N medições de pressão.
     * 
     * @param limit Número de medições a retornar
     * @return Flow com lista das últimas medições
     */
    @Query("SELECT * FROM pressure_measurements ORDER BY data DESC, hora DESC LIMIT :limit")
    fun getRecentPressures(limit: Int): Flow<List<PressureEntity>>
}

