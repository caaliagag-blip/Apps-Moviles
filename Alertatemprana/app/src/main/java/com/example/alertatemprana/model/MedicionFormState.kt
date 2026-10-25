package com.example.alertatemprana.model

data class MedicionFormState(
    val galponId: String = "",
    val nombreGalpon: String = "",
    val temperatura: String = "",
    val humedad: String = "",
    val errorGalponId: String? = null,
    val errorNombreGalpon: String? = null,
    val errorTemperatura: String? = null,
    val errorHumedad: String? = null
)