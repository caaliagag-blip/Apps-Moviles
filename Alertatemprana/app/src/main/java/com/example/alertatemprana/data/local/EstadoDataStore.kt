package com.example.alertatemprana.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "configuracion")

class EstadoDataStore(private val context: Context) {
    companion object {
        val MODO_EMERGENCIA_KEY = booleanPreferencesKey("modo_emergencia")
    }

    val modoEmergenciaFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODO_EMERGENCIA_KEY] ?: false
    }

    suspend fun guardarModoEmergencia(activo: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODO_EMERGENCIA_KEY] = activo
        }
    }
}