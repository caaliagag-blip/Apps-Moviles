package com.example.alertatemprana.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.alertatemprana.data.local.MedicionEntity
import com.example.alertatemprana.viewmodel.GalponViewModel

enum class RolUsuario {
    OPERADOR,
    SUPERVISOR,
    JEFE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GalponViewModel,
    onNavigateToForm: () -> Unit,
    onNavigateToEvidencia: () -> Unit
) {
    val mediciones by viewModel.todasLasMediciones.collectAsState(initial = emptyList())
    val ultimaMedicion = mediciones.firstOrNull()

    // Gestión de Roles y Diálogos
    var rolActual by remember { mutableStateOf(RolUsuario.OPERADOR) }
    var rolSolicitado by remember { mutableStateOf<RolUsuario?>(null) }
    var passwordInput by remember { mutableStateOf("") }
    var errorPassword by remember { mutableStateOf(false) }

    // Filtro de Supervisor
    var filtroEstado by remember { mutableStateOf("TODOS") }

    val medicionesFiltradas = remember(mediciones, filtroEstado) {
        if (filtroEstado == "TODOS") mediciones
        else mediciones.filter { it.estado.equals(filtroEstado, ignoreCase = true) }
    }

    // Diálogo de autenticación
    rolSolicitado?.let { rol ->
        val claveCorrecta = if (rol == RolUsuario.JEFE) "six seven" else "sin semen "
        val tituloRol = if (rol == RolUsuario.JEFE) "Modo Jefe" else "Modo Supervisor"

        AlertDialog(
            onDismissRequest = {
                rolSolicitado = null
                passwordInput = ""
                errorPassword = false
            },
            icon = { Icon(Icons.Default.Lock, contentDescription = "Seguridad") },
            title = { Text(text = "Acceso a $tituloRol") },
            text = {
                Column {
                    Text("Ingresa la clave para activar el $tituloRol:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorPassword = false
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = errorPassword,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorPassword) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Contraseña incorrecta",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (passwordInput.trim().lowercase() == claveCorrecta) {
                            rolActual = rol
                            rolSolicitado = null
                            passwordInput = ""
                            errorPassword = false
                        } else {
                            errorPassword = true
                        }
                    }
                ) {
                    Text("Ingresar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        rolSolicitado = null
                        passwordInput = ""
                        errorPassword = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Alerta Temprana",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rol: ${rolActual.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = when (rolActual) {
                                RolUsuario.JEFE -> MaterialTheme.colorScheme.error
                                RolUsuario.SUPERVISOR -> MaterialTheme.colorScheme.tertiary
                                RolUsuario.OPERADOR -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = { rolActual = RolUsuario.OPERADOR }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Modo Operador",
                                tint = if (rolActual == RolUsuario.OPERADOR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(onClick = {
                            if (rolActual != RolUsuario.SUPERVISOR) rolSolicitado = RolUsuario.SUPERVISOR
                        }) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = "Modo Supervisor",
                                tint = if (rolActual == RolUsuario.SUPERVISOR) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(onClick = {
                            if (rolActual != RolUsuario.JEFE) rolSolicitado = RolUsuario.JEFE
                        }) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Modo Jefe",
                                tint = if (rolActual == RolUsuario.JEFE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // BOTÓN DIRECTO PARA CÁMARA
                SmallFloatingActionButton(
                    onClick = onNavigateToEvidencia,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Abrir Cámara")
                }

                // BOTÓN DE FORMULARIO
                ExtendedFloatingActionButton(
                    onClick = onNavigateToForm,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Nueva") },
                    text = { Text("Nueva Medición") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = ultimaMedicion?.let { "${it.nombreGalpon.uppercase()} (ID: #${it.galponId})" } ?: "ÚLTIMA MEDICIÓN",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedContent(
                            targetState = ultimaMedicion?.temperatura,
                            label = "temp_anim"
                        ) { temp ->
                            Text(
                                text = if (temp != null) "$temp °C" else "-- °C",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Text(
                            text = ultimaMedicion?.let { "Humedad: ${it.humedad}%" } ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val esAlerta = (ultimaMedicion?.estado ?: "").contains("Alerta", ignoreCase = true) ||
                            (ultimaMedicion?.estado ?: "").contains("Critico", ignoreCase = true) ||
                            (ultimaMedicion?.estado ?: "").contains("CRÍTICO", ignoreCase = true)

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = ultimaMedicion?.estado ?: "Sin Registro",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (esAlerta) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (esAlerta) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (rolActual == RolUsuario.SUPERVISOR || rolActual == RolUsuario.JEFE) {
                Text(
                    text = "Filtro de Auditoría (Supervisor/Jefe)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("TODOS", "NORMAL", "ADVERTENCIA", "CRÍTICO").forEach { estado ->
                        FilterChip(
                            selected = filtroEstado == estado,
                            onClick = { filtroEstado = estado },
                            label = { Text(estado, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Historial Reciente (${medicionesFiltradas.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (medicionesFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay registros coincidentes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(items = medicionesFiltradas, key = { it.id }) { item ->
                        ItemMedicionCard(
                            medicion = item,
                            esModoJefe = (rolActual == RolUsuario.JEFE),
                            onDelete = { viewModel.eliminarMedicion(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemMedicionCard(
    medicion: MedicionEntity,
    esModoJefe: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${medicion.nombreGalpon} (ID: #${medicion.galponId}) • ${medicion.fechaHora}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${medicion.temperatura} °C  |  ${medicion.humedad}% HR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = medicion.estado,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = esModoJefe) {
                    IconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Registro"
                        )
                    }
                }
            }
        }
    }
}