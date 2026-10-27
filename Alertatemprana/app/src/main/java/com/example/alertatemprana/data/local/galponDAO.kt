package com.example.alertatemprana.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GalponDao {

    @Query("SELECT * FROM mediciones_galpon ORDER BY timestamp DESC")
    fun getAllMediciones(): Flow<List<MedicionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicion(medicion: MedicionEntity): Long

    @Delete
    suspend fun eliminarMedicion(medicion: MedicionEntity): Int
}