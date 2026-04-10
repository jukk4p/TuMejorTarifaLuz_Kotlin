package com.tumejortarifaluz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumejortarifaluz.ui.viewmodel.ManualEntryViewModel

import com.tumejortarifaluz.ui.theme.BackgroundDeep
import com.tumejortarifaluz.ui.theme.BackgroundOLED
import com.tumejortarifaluz.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(
    onDataEntered: () -> Unit,
    onBack: () -> Unit,
    viewModel: ManualEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DATOS DE FACTURA", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 2.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(10.dp))
                        }
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
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text(
                        text = "Configura tu perfil",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Necesitamos estos datos para calcular tu ahorro real.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Compañía y CUPS
            item {
                ManualEntrySection(title = "INFORMACIÓN GENERAL", icon = Icons.Default.Business) {
                    ManualTextField(
                        value = uiState.company,
                        onValueChange = { viewModel.updateCompany(it) },
                        label = "Compañía actual",
                        placeholder = "Ej. Iberdrola, Endesa...",
                        icon = Icons.Default.Business
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ManualTextField(
                        value = uiState.cups,
                        onValueChange = { viewModel.updateCups(it) },
                        label = "CUPS",
                        placeholder = "ES000...",
                        icon = Icons.Default.Grid3x3,
                        isError = uiState.cupsError != null,
                        supportingText = uiState.cupsError
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Potencias
            item {
                ManualEntrySection(title = "POTENCIA CONTRATADA", icon = Icons.Default.Speed) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ManualTextField(
                            value = uiState.powerP1,
                            onValueChange = { viewModel.updatePowerP1(it) },
                            label = "Potencia Punta",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Decimal,
                            isError = uiState.powerError != null
                        )
                        ManualTextField(
                            value = uiState.powerP2,
                            onValueChange = { viewModel.updatePowerP2(it) },
                            label = "Potencia Valle",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Decimal,
                            isError = uiState.powerError != null
                        )
                    }
                    uiState.powerError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Consumo
            item {
                ManualEntrySection(title = "ENERGÍA CONSUMIDA", icon = Icons.Default.FlashOn) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ManualTextField(
                            value = uiState.energyP1,
                            onValueChange = { viewModel.updateEnergyP1(it) },
                            label = "Energía Punta",
                            keyboardType = KeyboardType.Decimal,
                            isError = uiState.energyError != null
                        )
                        ManualTextField(
                            value = uiState.energyP2,
                            onValueChange = { viewModel.updateEnergyP2(it) },
                            label = "Energía Llano",
                            keyboardType = KeyboardType.Decimal,
                            isError = uiState.energyError != null
                        )
                        ManualTextField(
                            value = uiState.energyP3,
                            onValueChange = { viewModel.updateEnergyP3(it) },
                            label = "Energía Valle",
                            keyboardType = KeyboardType.Decimal,
                            isError = uiState.energyError != null
                        )
                    }
                    uiState.energyError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Importe Total
            item {
                ManualEntrySection(title = "IMPORTE Y PERIODO", icon = Icons.Default.Payments) {
                    ManualTextField(
                        value = uiState.totalAmount,
                        onValueChange = { viewModel.updateTotalAmount(it) },
                        label = "Importe total (€)",
                        icon = Icons.Default.Payments,
                        keyboardType = KeyboardType.Decimal
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ManualTextField(
                        value = uiState.days,
                        onValueChange = { viewModel.updateDays(it) },
                        label = "Días facturados",
                        icon = Icons.Default.Event,
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = { viewModel.submit(onDataEntered) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Calculate, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Calcular mi ahorro", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ManualEntrySection(
    title: String, 
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) BackgroundOLED else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}

@Composable
fun ManualTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), fontSize = 14.sp) },
            leadingIcon = if (icon != null) { 
                { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.size(18.dp)) } 
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            supportingText = supportingText?.let { { Text(it) } },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}
