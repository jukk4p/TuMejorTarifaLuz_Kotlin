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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import android.content.Intent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Image
import com.tumejortarifaluz.ui.viewmodel.ComparisonViewModel
import com.tumejortarifaluz.ui.utils.LogoMapper
import coil.compose.rememberAsyncImagePainter

import com.tumejortarifaluz.ui.theme.BackgroundDeep
import com.tumejortarifaluz.ui.theme.BackgroundOLED
import com.tumejortarifaluz.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    tariffId: String,
    onBack: () -> Unit,
    onContracted: () -> Unit,
    viewModel: ComparisonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tariff = uiState.tariff
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(tariffId) {
        viewModel.loadComparison(tariffId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)) { 
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground) 
                    }
                },
                title = { Text("Análisis de Ahorro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        tariff?.let {
                            val shareText = "¡He encontrado una mejor tarifa de luz! Con ${it.company} (${it.name}) pagaré solo ${it.totalBill}/mes y ahorraré ${it.estimatedSaving} al año. ¡Comprueba tu ahorro en TuMejorTarifaLuz!"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir tarifa"))
                        }
                    }, modifier = Modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), CircleShape)) { 
                        Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground) 
                    }
                    Spacer(Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            if (tariff != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 16.dp
                ) {
                    Button(
                        onClick = { if (tariff.contractUrl.isNotEmpty()) uriHandler.openUri(tariff.contractUrl) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text("CONTRATAR ESTA TARIFA", fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        if (tariff != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Hero Section (Premium Brand Focus)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Company Logo & Name Card
                        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(32.dp),
                            color = if (isDark) BackgroundOLED else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier.size(160.dp, 80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = LogoMapper.getLogoForCompany(
                                            company = tariff.company,
                                            isDark = isDark
                                        )),
                                        contentDescription = tariff.company,
                                        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.85f)
                                    )
                                }
                                Text(
                                    text = tariff.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        // Dramatic Price Display
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "ESTIMACIÓN MENSUAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                letterSpacing = 2.sp
                            )
                            
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = tariff.totalBill.replace(" €", ""),
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 72.sp,
                                        lineHeight = 72.sp
                                    ),
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    letterSpacing = (-2).sp
                                )
                                Column(modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)) {
                                    Text(
                                        text = "€",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "neto/mes",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Visual Savings Meter
                        val savingAmount = tariff.estimatedSaving.filter { it.isDigit() || it == ',' }.replace(',', '.').toFloatOrNull() ?: 0f
                        val savingLevel = (savingAmount / 1000f).coerceIn(0.1f, 1f)
                        
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Nivel de Ahorro", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("¡Excelente!", color = Color(0xFF10B981), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                            }
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(10.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                shape = CircleShape
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(savingLevel.toFloat())
                                        .background(
                                            androidx.compose.ui.graphics.Brush.linearGradient(
                                                colors = listOf(Color(0xFF10B981), Color(0xFF34D399))
                                            ),
                                            CircleShape
                                        )
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "↓ ${tariff.estimatedSaving} de ahorro anual",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Data Sections organized in modern cards
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        
                        // 1. Desglose Section
                        PremiumSection(
                            title = "Desglose de Factura",
                            icon = Icons.Default.ReceiptLong
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    uiState.details.filter { !it.isTotal }.forEach { detail ->
                                        CompactDetailRow(detail.concept, detail.new)
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // 2. Precios Aplicados Section
                        var showWithTaxes by remember { mutableStateOf(false) }
                        
                        PremiumSection(
                            title = "Precios Aplicados",
                            icon = Icons.Default.Payments,
                            topAction = {
                                // Tax Toggle - Segmented Control (Base vs PVP)
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Option: SIN IVA (Base)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(if (!showWithTaxes) MaterialTheme.colorScheme.primary else Color.Transparent)
                                                .clickable { showWithTaxes = false }
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "BASE",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = if (!showWithTaxes) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 9.sp
                                            )
                                        }
                                        
                                        // Option: CON IVA (PVP)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(if (showWithTaxes) MaterialTheme.colorScheme.primary else Color.Transparent)
                                                .clickable { showWithTaxes = true }
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "PVP (IVA)",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = if (showWithTaxes) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val tariff = uiState.tariff
                                    if (tariff != null) {
                                        // Potencia Group
                                        PriceDetailRow("Potencia Punta", if (showWithTaxes) String.format("%.4f €/kW/día", tariff.pricePowerP1WithTaxes) else String.format("%.4f €/kW/día", tariff.pricePowerP1), Icons.Default.FlashOn, Color(0xFF3B82F6))
                                        PriceDetailRow("Potencia Valle", if (showWithTaxes) String.format("%.4f €/kW/día", tariff.pricePowerP2WithTaxes) else String.format("%.4f €/kW/día", tariff.pricePowerP2), Icons.Default.FlashOn, Color(0xFF3B82F6))
                                        
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                        
                                        // Energía Group
                                        PriceDetailRow("Energía Punta", if (showWithTaxes) String.format("%.4f €/kWh", tariff.priceEnergyP1WithTaxes) else String.format("%.4f €/kWh", tariff.priceEnergyP1), Icons.Default.Bolt, Color(0xFFFBBF24))
                                        PriceDetailRow("Energía Llano", if (showWithTaxes) String.format("%.4f €/kWh", tariff.priceEnergyP2WithTaxes) else String.format("%.4f €/kWh", tariff.priceEnergyP2), Icons.Default.Bolt, Color(0xFFFBBF24))
                                        PriceDetailRow("Energía Valle", if (showWithTaxes) String.format("%.4f €/kWh", tariff.priceEnergyP3WithTaxes) else String.format("%.4f €/kWh", tariff.priceEnergyP3), Icons.Default.Bolt, Color(0xFFFBBF24))
                                        
                                        if (tariff.surplusPrice > 0) {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                            PriceDetailRow("Precio Excedentes", String.format("%.4f €/kWh", tariff.surplusPrice), Icons.Default.WbSunny, Color(0xFFF97316))
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // 3. Info Adicional
                        val conditionsText = when {
                            tariff.name.contains("Solar", ignoreCase = true) -> "Optimizada para hogares con paneles solares. Incluye compensación de excedentes."
                            tariff.name.contains("3 Periodos", ignoreCase = true) || tariff.type.contains("3 Periodos") -> "Discriminación horaria en tres periodos. Ahorra desplazando tu consumo a la noche y fines de semana."
                            else -> "Precio fijo por kWh las 24 horas del día. Máxima tranquilidad sin importar la hora de consumo."
                        }
                        PremiumSection(
                            title = "Términos del Contrato",
                            icon = Icons.Default.Gavel
                        ) {
                            Text(
                                text = conditionsText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = { if (tariff.contractUrl.isNotEmpty()) uriHandler.openUri(tariff.contractUrl) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Leer condiciones completas >", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
fun PremiumSection(
    title: String,
    icon: ImageVector,
    topAction: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) BackgroundOLED else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (topAction != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    topAction()
                }
            }
            
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Black, 
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(32.dp))
                content()
            }
        }
    }
}

@Composable
fun CompactDetailRow(label: String, value: String) {
    val icon = when {
        label.contains("Potencia", ignoreCase = true) -> Icons.Default.FlashOn
        label.contains("Energía", ignoreCase = true) -> Icons.Default.Bolt
        label.contains("IVA", ignoreCase = true) || label.contains("Impuesto", ignoreCase = true) -> Icons.Default.Percent
        label.contains("Contador", ignoreCase = true) -> Icons.Default.Timer
        label.contains("Bono", ignoreCase = true) -> Icons.Default.Verified
        else -> Icons.Default.Label
    }
    
    val color = when {
        label.contains("Potencia", ignoreCase = true) -> Color(0xFF3B82F6) // Blue
        label.contains("Energía", ignoreCase = true) -> Color(0xFFFBBF24) // Yellow
        label.contains("IVA", ignoreCase = true) || label.contains("Impuesto", ignoreCase = true) -> Color(0xFFF87171) // Red
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = color, 
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label, 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyLarge, 
            color = MaterialTheme.colorScheme.onSurface, 
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun PriceDetailRow(label: String, value: String, icon: ImageVector = Icons.Default.Label, color: Color = MaterialTheme.colorScheme.primary) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = color.copy(alpha = 0.8f), 
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = label, 
                style = MaterialTheme.typography.bodyLarge, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
        }
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyLarge, 
            color = MaterialTheme.colorScheme.onSurface, 
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
        )
    }
}
