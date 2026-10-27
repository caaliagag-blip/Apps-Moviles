package com.example.alertatemprana.ui.screen

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.alertatemprana.navigation.Screen
import com.example.alertatemprana.viewmodel.GalponViewModel
import com.example.alertatemprana.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    mainViewModel: MainViewModel,
    galponViewModel: GalponViewModel
) {
    val formState by galponViewModel.formState.collectAsState()
    val evidenciaUri by galponViewModel.evidenciaUri.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Medición") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // CAMPO ID GALPÓN
            OutlinedTextField(
                value = formState.galponId,
                onValueChange = galponViewModel::onGalponIdChange,
                label = { Text("ID del Galpón") },
                leadingIcon = {
                    Icon(Icons.Default.Badge, contentDescription = "ID Galpón")
                },
                trailingIcon = {
                    if (formState.errorGalponId != null) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formState.errorGalponId != null,
                supportingText = {
                    formState.errorGalponId?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO NOMBRE / UBICACIÓN
            OutlinedTextField(
                value = formState.nombreGalpon,
                onValueChange = galponViewModel::onNombreGalponChange,
                label = { Text("Nombre o Ubicación Galpón") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación Galpón")
                },
                trailingIcon = {
                    if (formState.errorNombreGalpon != null) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formState.errorNombreGalpon != null,
                supportingText = {
                    formState.errorNombreGalpon?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO TEMPERATURA
            OutlinedTextField(
                value = formState.temperatura,
                onValueChange = galponViewModel::onTemperaturaChange,
                label = { Text("Temperatura (°C)") },
                leadingIcon = {
                    Icon(Icons.Default.Thermostat, contentDescription = "Temperatura")
                },
                trailingIcon = {
                    if (formState.errorTemperatura != null) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formState.errorTemperatura != null,
                supportingText = {
                    formState.errorTemperatura?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO HUMEDAD
            OutlinedTextField(
                value = formState.humedad,
                onValueChange = galponViewModel::onHumedadChange,
                label = { Text("Humedad (%)") },
                leadingIcon = {
                    Icon(Icons.Default.WaterDrop, contentDescription = "Humedad")
                },
                trailingIcon = {
                    if (formState.errorHumedad != null) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = formState.errorHumedad != null,
                supportingText = {
                    formState.errorHumedad?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // VINCULACIÓN CON RECURSO NATIVO (EVIDENCIA FOTOGRÁFICA)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Evidencia Fotográfica", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = if (evidenciaUri != null) "Imagen adjunta correctamente" else "Opcional: Capturar foto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (evidenciaUri != null) {
                        AsyncImage(
                            model = evidenciaUri,
                            contentDescription = "Evidencia Adjunta",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(onClick = { mainViewModel.navigateTo(Screen.Evidencia) }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (evidenciaUri != null) "Cambiar" else "Capturar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (galponViewModel.guardarMedicion()) {
                        mainViewModel.navigateTo(Screen.Home)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar en BD Room")
            }
        }
    }
}