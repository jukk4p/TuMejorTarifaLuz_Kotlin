package com.tumejortarifaluz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumejortarifaluz.ui.theme.BorderColor
import com.tumejortarifaluz.ui.theme.SurfaceCard
import com.tumejortarifaluz.ui.theme.TextSecondary
import com.tumejortarifaluz.ui.viewmodel.ValidationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidationScreen(
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    viewModel: ValidationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = onBack) { 
                        Surface(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(10.dp))
                        }
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VERIFICACIÓN", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 2.sp)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 24.dp, top = 0.dp, end = 24.dp, bottom = 40.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = 32.dp)) {
                    Text(
                        text = "Confirma tus datos", 
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp), 
                        fontWeight = FontWeight.Black, 
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Hemos extraído esta información de tu factura. Asegúrate de que todo sea correcto.", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Invoice Preview Scanner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) SurfaceCard else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background pattern / image placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                    )
                                )
                        )
                        
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Description, 
                                null, 
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), 
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(100.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    "DOCUMENTO ESCANEADO", 
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        // Scanner line animation mock
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.Center)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }

            // Extracted Data List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "DATOS DETECTADOS", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Black, 
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), 
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Verified, null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    uiState.fields.forEachIndexed { index, field ->
                        val icon = when {
                            field.label.contains("Importe", ignoreCase = true) || field.label.contains("Amount", ignoreCase = true) -> Icons.Default.Payments
                            field.label.contains("Días", ignoreCase = true) || field.label.contains("Period", ignoreCase = true) -> Icons.Default.CalendarToday
                            field.label.contains("Potencia", ignoreCase = true) || field.label.contains("Power", ignoreCase = true) -> Icons.Default.Speed
                            field.label.contains("Consumo", ignoreCase = true) || field.label.contains("Energy", ignoreCase = true) -> Icons.Default.Bolt
                            field.label.contains("CUPS", ignoreCase = true) -> Icons.Default.Grid3x3
                            else -> Icons.Default.Business
                        }
                        val numericValue = field.value.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
                        val displayValue = if (field.label.contains("Consumo", ignoreCase = true) || field.label.contains("Importe", ignoreCase = true)) {
                            numericValue.toDoubleOrNull()?.let { 
                                java.text.DecimalFormat("#.##").format(it)
                            }?.plus(if (field.label.contains("Consumo", ignoreCase = true)) " kWh" else " €") ?: field.value
                        } else {
                            field.value
                        }
                        
                        ValidationItem(
                            icon = icon, 
                            label = field.label.uppercase(), 
                            value = displayValue,
                            onClick = { 
                                if (field.isEditable) {
                                    viewModel.setEditingField(index)
                                }
                            }
                        )
                    }
                }
            }

            // Buttons Section
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("COMIENZA EL AHORRO", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.AutoGraph, null, modifier = Modifier.size(20.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("No es correcto, quiero editar", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(
                    color = Color.White.copy(alpha = 0.03f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Lock, null, tint = TextSecondary.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "PROCESAMIENTO SEGURO Y PRIVADO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Direct Edit Dialog
    if (uiState.editingFieldIndex != null) {
        val field = uiState.fields[uiState.editingFieldIndex!!]
        var tempValue by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(field.value) }

        AlertDialog(
            onDismissRequest = { viewModel.setEditingField(null) },
            title = { Text("Corregir ${field.label}", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempValue,
                    onValueChange = { tempValue = it },
                    label = { Text("Introduce el dato correcto") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = { 
                    viewModel.updateField(uiState.editingFieldIndex!!, tempValue)
                    viewModel.setEditingField(null)
                }) {
                    Text("Guardar cambios", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setEditingField(null) }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ValidationItem(icon: ImageVector, label: String, value: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) SurfaceCard else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Black)
                Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            }
            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
        }
    }
}
