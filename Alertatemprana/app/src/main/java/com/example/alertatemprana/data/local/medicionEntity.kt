package com.example.alertatemprana.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mediciones_galpon")
data class MedicionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val galponId: Int,
    val nombreGalpon: String,
    val temperatura: Double,
    val humedad: Double,
    val estado: String,
    val fechaHora: String,
    val imagenUri: String?,
    val timestamp: Long = System.currentTimeMillis()
)