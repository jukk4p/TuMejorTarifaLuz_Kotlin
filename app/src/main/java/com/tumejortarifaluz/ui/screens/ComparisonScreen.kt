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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)) { 
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
                    }, modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)) { 
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
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
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
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
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
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(32.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
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
                                            isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = (-2).sp
                                )
                                Column(modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)) {
                                    Text(
                                        text = "€",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "neto/mes",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                            uiState.details.filter { !it.isTotal }.forEach { detail ->
                                CompactDetailRow(detail.concept, detail.new)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // 2. Precios Section
                        var showWithTaxes by remember { mutableStateOf(false) }
                        
                        PremiumSection(
                            title = "Precios Aplicados",
                            icon = Icons.Default.Payments,
                            topAction = {
                                // Tax Toggle - Top Right Action
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clickable { showWithTaxes = !showWithTaxes }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (showWithTaxes) "CON" else "SIN",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            fontWeight = FontWeight.Black,
                                            color = if (showWithTaxes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Switch(
                                            checked = showWithTaxes,
                                            onCheckedChange = { showWithTaxes = it },
                                            modifier = Modifier.scale(0.55f).height(18.dp),
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            val p1Power = if (showWithTaxes) (if (tariff.pricePowerP1WithTaxes > 0) tariff.pricePowerP1WithTaxes else tariff.pricePowerP1 * 1.105) else tariff.pricePowerP1
                            val p2Power = if (showWithTaxes) (if (tariff.pricePowerP2WithTaxes > 0) tariff.pricePowerP2WithTaxes else tariff.pricePowerP2 * 1.105) else tariff.pricePowerP2
                            val p1Energy = if (showWithTaxes) (if (tariff.priceEnergyP1WithTaxes > 0) tariff.priceEnergyP1WithTaxes else tariff.priceEnergyP1 * 1.105) else tariff.priceEnergyP1
                            val p2Energy = if (showWithTaxes) (if (tariff.priceEnergyP2WithTaxes > 0) tariff.priceEnergyP2WithTaxes else tariff.priceEnergyP2 * 1.105) else tariff.priceEnergyP2
                            val p3Energy = if (showWithTaxes) (if (tariff.priceEnergyP3WithTaxes > 0) tariff.priceEnergyP3WithTaxes else tariff.priceEnergyP3 * 1.105) else tariff.priceEnergyP3

                            PriceInfoRow("Potencia Punta", String.format("%.4f €/kW/día", p1Power))
                            PriceInfoRow("Potencia Valle", String.format("%.4f €/kW/día", p2Power))
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            PriceInfoRow("Energía Punta", String.format("%.4f €/kWh", p1Energy))
                            if (tariff.priceEnergyP2 > 0) {
                                PriceInfoRow("Energía Llano", String.format("%.4f €/kWh", p2Energy))
                            }
                            if (tariff.priceEnergyP3 > 0) {
                                PriceInfoRow("Energía Valle", String.format("%.4f €/kWh", p3Energy))
                            }
                            
                            if (tariff.surplusPrice > 0) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                PriceInfoRow("Precio Excedentes", String.format("%.4f €/kWh", tariff.surplusPrice))
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
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
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Black)
    }
}

@Composable
fun PriceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}
