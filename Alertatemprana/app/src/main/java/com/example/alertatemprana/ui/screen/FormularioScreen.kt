package com.example.alertatemprana.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Medición") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = formState.galponId,
                onValueChange = galponViewModel::onGalponIdChange,
                label = { Text("ID del Galpón (Número)") },
                isError = formState.errorGalponId != null,
                supportingText = {
                    formState.errorGalponId?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.nombreGalpon,
                onValueChange = galponViewModel::onNombreGalponChange,
                label = { Text("Nombre o Ubicación Galpón") },
                isError = formState.errorNombreGalpon != null,
                supportingText = {
                    formState.errorNombreGalpon?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.temperatura,
                onValueChange = galponViewModel::onTemperaturaChange,
                label = { Text("Temperatura (°C)") },
                isError = formState.errorTemperatura != null,
                supportingText = {
                    formState.errorTemperatura?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = formState.humedad,
                onValueChange = galponViewModel::onHumedadChange,
                label = { Text("Humedad (%)") },
                isError = formState.errorHumedad != null,
                supportingText = {
                    formState.errorHumedad?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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