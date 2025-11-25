package com.example.pressocheck_final.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entidade que representa uma medição de pressão arterial no banco de dados Room.
 * 
 * @param id Identificador único gerado automaticamente
 * @param sistolica Valor da pressão sistólica (máxima)
 * @param diastolica Valor da pressão diastólica (mínima)
 * @param data Data da medição
 * @param hora Hora da medição
 * @param observacao Observações opcionais (sintomas, medicamentos, etc.)
 */
@Entity(tableName = "pressure_measurements")
data class PressureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sistolica: Int,
    val diastolica: Int,
    val data: Long, // Timestamp em milissegundos
    val hora: String, // Formato HH:mm
    val observacao: String = ""
)

