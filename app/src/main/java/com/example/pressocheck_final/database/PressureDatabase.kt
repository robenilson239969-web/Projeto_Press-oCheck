package com.example.pressocheck_final.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pressocheck_final.dao.PressureDao
import com.example.pressocheck_final.model.PressureEntity

/**
 * Classe abstrata que representa o banco de dados Room do aplicativo.
 * 
 * Gerencia a criação e configuração do banco de dados SQLite usando Room.
 * Implementa o padrão Singleton para garantir uma única instância do banco.
 * 
 * @property version Versão do banco de dados (incrementar ao alterar schema)
 */
@Database(
    entities = [PressureEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PressureDatabase : RoomDatabase() {
    
    /**
     * Retorna a instância do DAO para operações de pressão arterial.
     */
    abstract fun pressureDao(): PressureDao
    
    companion object {
        @Volatile
        private var INSTANCE: PressureDatabase? = null
        
        /**
         * Obtém a instância única do banco de dados (Singleton).
         * 
         * @param context Contexto da aplicação
         * @return Instância do banco de dados
         */
        fun getDatabase(context: Context): PressureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PressureDatabase::class.java,
                    "pressure_database"
                )
                    .fallbackToDestructiveMigration() // Permite recriar o banco se a versão mudar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

