package com.example.alertatemprana.domain.usecases

import com.example.alertatemprana.model.MedicionFormState

data class ValidationResult(
    val isValid: Boolean,
    val errorGalponId: String? = null,
    val errorNombreGalpon: String? = null,
    val errorTemperatura: String? = null,
    val errorHumedad: String? = null
)

class ValidateFormUseCase {
    operator fun invoke(state: MedicionFormState): ValidationResult {
        var isValid = true
        var errGalponId: String? = null
        var errNombre: String? = null
        var errTemp: String? = null
        var errHum: String? = null

        if (state.galponId.isBlank()) {
            errGalponId = "Campo obligatorio"
            isValid = false
        }

        if (state.nombreGalpon.isBlank()) {
            errNombre = "Campo obligatorio"
            isValid = false
        }

        val tempVal = state.temperatura.toDoubleOrNull()
        if (state.temperatura.isBlank()) {
            errTemp = "Campo obligatorio"
            isValid = false
        } else if (tempVal == null || tempVal !in -10.0..60.0) {
            errTemp = "Temperatura entre -10 y 60 °C"
            isValid = false
        }

        val humVal = state.humedad.toDoubleOrNull()
        if (state.humedad.isBlank()) {
            errHum = "Campo obligatorio"
            isValid = false
        } else if (humVal == null || humVal !in 0.0..100.0) {
            errHum = "Humedad entre 0 y 100 %"
            isValid = false
        }

        return ValidationResult(
            isValid = isValid,
            errorGalponId = errGalponId,
            errorNombreGalpon = errNombre,
            errorTemperatura = errTemp,
            errorHumedad = errHum
        )
    }
}