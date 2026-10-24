package com.example.alertatemprana.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mediciones_galpon")
data class MedicionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val galponId: Int,
    val nombreGalpon: String,
    val temperatura: Double,
    val humedad: Double,
    val estado: String, // NORMAL, ADVERTENCIA, CRÍTICO
    val fechaHora: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface GalponDao {
    @Query("SELECT * FROM mediciones_galpon ORDER BY timestamp DESC")
    fun getAllMediciones(): Flow<List<MedicionEntity>>

    @Query("SELECT * FROM mediciones_galpon WHERE galponId = :galponId ORDER BY timestamp DESC")
    fun getMedicionesByGalpon(galponId: Int): Flow<List<MedicionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicion(medicion: MedicionEntity)
}