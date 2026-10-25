package com.example.alertatemprana.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alertatemprana.navigation.Screen
import com.example.alertatemprana.viewmodel.GalponViewModel
import com.example.alertatemprana.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    galponViewModel: GalponViewModel = viewModel()
) {
    val mediciones by galponViewModel.todasLasMediciones.collectAsState()
    val modoEmergencia by galponViewModel.modoEmergencia.collectAsState()
    val mostrarMensaje by galponViewModel.mostrarMensaje.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (modoEmergencia == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val estaActivo = modoEmergencia!!

        val colorAnimado by animateColorAsState(
            targetValue = if (estaActivo) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
            animationSpec = tween(durationMillis = 500),
            label = "ColorBoton"
        )

        val textoBoton by remember(key1 = estaActivo) {
            derivedStateOf { if (estaActivo) "Desactivar Emergencia" else "Activar Modo Emergencia" }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = "Menú Alerta Temprana",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        label = { Text("Registrar Medición") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            mainViewModel.navigateTo(Screen.Formulario)
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Camera, contentDescription = null) },
                        label = { Text("Adjuntar Evidencia (Cámara)") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            mainViewModel.navigateTo(Screen.Evidencia)
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Alerta Temprana - Monitoreo") },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorAnimado, titleContentColor = Color.White),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú Lateral", tint = Color.White)
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { mainViewModel.navigateTo(Screen.Formulario) }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { galponViewModel.alternarModoEmergencia() },
                        colors = ButtonDefaults.buttonColors(containerColor = colorAnimado),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(textoBoton)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(visible = mostrarMensaje) {
                        Text(
                            text = "¡Estado guardado en DataStore!",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Historial en Base de Datos Room:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (mediciones.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay mediciones registradas.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(mediciones) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (item.estado == "CRÍTICO") Color(0xFFFFCDD2) else Color(0xFFE8F5E9),
                                        contentColor = Color(0xFF1C1B1F)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Galpón #${item.galponId} - ${item.nombreGalpon}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(text = "Temperatura: ${item.temperatura} °C | Humedad: ${item.humedad} %")
                                        Text(text = "Estado: ${item.estado} | Fecha: ${item.fechaHora}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}