package com.example.alertatemprana.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertatemprana.data.local.AppDatabase
import com.example.alertatemprana.data.local.EstadoDataStore
import com.example.alertatemprana.data.local.MedicionEntity
import com.example.alertatemprana.data.repository.GalponRepository
import com.example.alertatemprana.domain.usecases.ValidateFormUseCase
import com.example.alertatemprana.model.MedicionFormState
import com.example.alertatemprana.util.HapticFeedbackHelper
import com.example.alertatemprana.util.NotificacionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalponViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GalponRepository
    private val estadoDataStore = EstadoDataStore(application)
    private val validator = ValidateFormUseCase()
    private val hapticHelper = HapticFeedbackHelper(application)
    private val notificacionHelper = NotificacionHelper(application)

    init {
        val dao = AppDatabase.getDatabase(application).galponDao()
        repository = GalponRepository(dao)
    }

    val todasLasMediciones: StateFlow<List<MedicionEntity>> = repository.todasLasMediciones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val modoEmergencia: StateFlow<Boolean?> = estadoDataStore.modoEmergenciaFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _evidenciaUri = MutableStateFlow<Uri?>(null)
    val evidenciaUri: StateFlow<Uri?> = _evidenciaUri.asStateFlow()

    private val _formState = MutableStateFlow(MedicionFormState())
    val formState: StateFlow<MedicionFormState> = _formState.asStateFlow()

    private val _mostrarMensaje = MutableStateFlow(false)
    val mostrarMensaje: StateFlow<Boolean> = _mostrarMensaje.asStateFlow()

    fun setEvidenciaUri(uri: Uri?) {
        _evidenciaUri.value = uri
    }

    fun alternarModoEmergencia() {
        viewModelScope.launch {
            val actual = modoEmergencia.value ?: false
            estadoDataStore.guardarModoEmergencia(!actual)
        }
    }

    fun onGalponIdChange(value: String) {
        _formState.value = _formState.value.copy(galponId = value)
    }

    fun onNombreGalponChange(value: String) {
        _formState.value = _formState.value.copy(nombreGalpon = value)
    }

    fun onTemperaturaChange(value: String) {
        _formState.value = _formState.value.copy(temperatura = value)
    }

    fun onHumedadChange(value: String) {
        _formState.value = _formState.value.copy(humedad = value)
    }

    fun guardarMedicion(): Boolean {
        val currentState = _formState.value
        val valResult = validator(currentState)

        if (valResult.isValid) {
            val tempVal = currentState.temperatura.toDoubleOrNull() ?: 0.0
            val humVal = currentState.humedad.toDoubleOrNull() ?: 0.0
            val galponIdVal = currentState.galponId.toIntOrNull() ?: 1

            val estadoCalc = if (tempVal >= 32.0) "CRÍTICO" else if (tempVal >= 28.0) "ADVERTENCIA" else "NORMAL"

            val nuevaMedicion = MedicionEntity(
                galponId = galponIdVal,
                nombreGalpon = currentState.nombreGalpon,
                temperatura = tempVal,
                humedad = humVal,
                estado = estadoCalc,
                fechaHora = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            )

            if (tempVal >= 32.0) {
                hapticHelper.vibrarAlerta()
                notificacionHelper.mostrarNotificacion(
                    "¡ALERTA CRÍTICA!",
                    "El Galpón '${currentState.nombreGalpon}' registró $tempVal °C"
                )
            }

            viewModelScope.launch {
                repository.insertarMedicion(nuevaMedicion)
                _formState.value = MedicionFormState()
                _mostrarMensaje.value = true
            }
            return true
        } else {
            _formState.value = currentState.copy(
                errorGalponId = valResult.errorGalponId,
                errorNombreGalpon = valResult.errorNombreGalpon,
                errorTemperatura = valResult.errorTemperatura,
                errorHumedad = valResult.errorHumedad
            )
            return false
        }
    }

    fun resetMensaje() {
        _mostrarMensaje.value = false
    }
}