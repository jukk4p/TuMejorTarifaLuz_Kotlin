package com.tumejortarifaluz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.tumejortarifaluz.ui.viewmodel.ResultsViewModel
import com.tumejortarifaluz.ui.viewmodel.Tariff
import com.tumejortarifaluz.ui.viewmodel.TariffFilter
import com.tumejortarifaluz.ui.utils.LogoMapper
import com.tumejortarifaluz.ui.components.TuMejorTarifaLuzLogo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onNavigateToDetail: (String) -> Unit,
    onEditConsumption: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 4.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Sheet header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Filtrar tarifas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    if (uiState.selectedFilter != TariffFilter.ALL) {
                        TextButton(onClick = {
                            viewModel.updateFilter(TariffFilter.ALL)
                            showFilterSheet = false
                        }) {
                            Text("Resetear", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    placeholder = { 
                        Text(
                            "Buscar compañía o tarifa...", 
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        ) 
                    },
                    leadingIcon = { 
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        ) 
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    Icons.Default.Close, 
                                    contentDescription = "Borrar",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                // Filter options grid
                val filters = TariffFilter.entries
                filters.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { filter ->
                            val isSelected = uiState.selectedFilter == filter
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp)
                                    .clickable {
                                        viewModel.updateFilter(filter)
                                        showFilterSheet = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                                                     else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                                        Icon(
                                            imageVector = when(filter) {
                                                TariffFilter.ALL -> Icons.Default.Bolt
                                                TariffFilter.TOP4 -> Icons.Default.EmojiEvents
                                                TariffFilter.FIXED -> Icons.Default.Lock
                                                TariffFilter.THREE_PERIOD -> Icons.Default.Schedule
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Text(
                                            text = filter.label,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 15.sp,
                                            lineHeight = 18.sp
                                        )
                                        Text(
                                            text = "${uiState.filterCounts[filter] ?: 0} tarifas",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp).align(Alignment.TopEnd)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                title = { TuMejorTarifaLuzLogo() },
                actions = {
                    IconButton(onClick = { /* Share app */ }) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        },
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // Changed to primaryContainer
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer, // Changed to onPrimaryContainer
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        floatingActionButton = {
            ResultsFloatingMenu(
                onFilterClick = { showFilterSheet = true },
                onNewClick = onNavigateToHome,
                onSaveClick = {
                    viewModel.saveAnalysis()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "✅ Análisis guardado correctamente",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Active filter pill (compact, only when filter is active)
            if (uiState.selectedFilter != TariffFilter.ALL) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .clickable { showFilterSheet = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${uiState.selectedFilter.emoji} ${uiState.selectedFilter.label}  •  ${uiState.tariffs.size} resultado${if (uiState.tariffs.size != 1) "s" else ""}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.Close,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp).clickable { viewModel.updateFilter(TariffFilter.ALL) }
                        )
                    }
                }
            }

            // Profile Card (Premium Dashboard Style)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    ) {
                        // Header with Pulsing Saving
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "ESTÁS AHORRANDO",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    letterSpacing = 1.5.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    uiState.tariffs.firstOrNull()?.estimatedSaving ?: "0,00 €",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Ahorro anual estimado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            // Small, subtle edit button in the corner
                            Surface(
                                onClick = onEditConsumption,
                                modifier = Modifier.align(Alignment.TopEnd).size(32.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Edit, 
                                        "Editar", 
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), 
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        Spacer(Modifier.height(24.dp))

                        // Consumption Visual Grid
                        Text(
                            "DETALLE DE CONSUMO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        // Consolidated Consumption Display - Layered for absolute alignment
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Row 1: Badges
                            Row(modifier = Modifier.fillMaxWidth()) {
                                listOf("PUNTA" to Color(0xFFF87171), "LLANO" to Color(0xFFFBBF24), "VALLE" to Color(0xFF34D399)).forEach { (label, color) ->
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                                            Text(text = label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), fontWeight = FontWeight.Black, color = color)
                                        }
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            // Row 2: Values (Forced baseline alignment)
                            Row(
                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val energy = listOf(
                                    uiState.currentInvoice?.energyP1?.toInt() ?: 0,
                                    uiState.currentInvoice?.energyP2?.toInt() ?: 0,
                                    uiState.currentInvoice?.energyP3?.toInt() ?: 0
                                )
                                energy.forEach { e ->
                                    Row(
                                        modifier = Modifier.weight(1f).alignByBaseline(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            text = e.toString(),
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.alignByBaseline()
                                        )
                                        Text(
                                            text = " kWh", 
                                            style = MaterialTheme.typography.labelSmall, 
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(bottom = 4.dp).alignByBaseline()
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            // Row 3: Indicators
                            Row(modifier = Modifier.fillMaxWidth()) {
                                listOf(Color(0xFFF87171), Color(0xFFFBBF24), Color(0xFF34D399)).forEach { color ->
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Box(modifier = Modifier.width(36.dp).height(4.dp).background(color, CircleShape))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        val companyName = uiState.currentInvoice?.company?.takeIf { it.isNotEmpty() && it != "Otras" }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (companyName != null) {
                                // Quick Stats Box: Company
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("COMPAÑÍA ACTUAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(companyName, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    }
                                }
                            }
                            
                            // Quick Stats Box: Power
                            Surface(
                                modifier = if (companyName != null) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = if (companyName != null) Alignment.Start else Alignment.CenterHorizontally
                                ) {
                                    Text("POTENCIA CONTRATADA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    val p1 = uiState.currentInvoice?.powerP1 ?: 0.0
                                    val p2 = uiState.currentInvoice?.powerP2 ?: 0.0
                                    val powerText = if (p1 == p2) "$p1 kW" else "P: $p1 | V: $p2 kW"
                                    Text(powerText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Section Header (Centered)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tarifas recomendadas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Encontradas según tu consumo real",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Tariff List
            if (uiState.tariffs.isEmpty() && !uiState.isLoading) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SearchOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No hay tarifas con este filtro", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                itemsIndexed(uiState.tariffs) { index, tariff ->
                    TariffCard(
                        tariff = tariff,
                        isBestPrice = index == 0 && uiState.selectedFilter == TariffFilter.ALL,
                        onClick = { onNavigateToDetail(tariff.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(tariff) }
                    )
                }
            }
            
            // Transparency Card (Centered)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Los cálculos incluyen todos los impuestos (IVA, IEE) y costes regulados actuales. Estimación basada en consumos medios por periodo.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            

        }
    }
}

@Composable
fun TariffCard(
    tariff: Tariff,
    isBestPrice: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(32.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isBestPrice) MaterialTheme.colorScheme.primary.copy(alpha = 0.03f) 
                             else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp, 
            if (isBestPrice) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBestPrice) 4.dp else 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large top space to give the heart its own "territory"
                Spacer(modifier = Modifier.height(42.dp))

                // Best price badge (Slightly smaller to keep balance)
                if (isBestPrice) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = "⭐ MEJOR PRECIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            letterSpacing = 0.5.sp,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Identity Container (Narrower for maximum breathing room)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(115.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                        Box(
                            modifier = Modifier.size(140.dp, 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = coil.compose.rememberAsyncImagePainter(
                                    model = LogoMapper.getLogoForCompany(tariff.company, isDark)
                                ),
                                contentDescription = tariff.company,
                                modifier = Modifier.fillMaxHeight().fillMaxWidth()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = tariff.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Central Price display
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = tariff.totalBill.replace(" €", "").replace("/mes", "").trim(),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = " €/mes",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 10.dp, start = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Savings Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AHORRO MENSUAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(
                            text = "↓ ${tariff.estimatedSavingMonthly}",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981),
                            fontSize = 16.sp
                        )
                    }
                    VerticalDivider(modifier = Modifier.height(36.dp).width(1.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AHORRO ANUAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(
                            text = "↓ ${tariff.estimatedSaving}",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981),
                            fontSize = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Bottom row: Details Link
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VER DETALLES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Favorite Button in Corner
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            ) {
                Icon(
                    imageVector = if (tariff.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (tariff.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun SavingRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val savingClean = value.replace(",", ".").replace(" €", "").toDoubleOrNull() ?: 0.0
        val isSaving = savingClean >= 0
        val absSaving = kotlin.math.abs(savingClean)
        val color = if (isSaving) Color(0xFF22C55E) else Color(0xFFEF4444)
        val icon = if (isSaving) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.2f €", absSaving),
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 15.sp
            )
        }
    }
}
@Composable
fun ResultsFloatingMenu(
    onFilterClick: () -> Unit,
    onNewClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        shadowElevation = 16.dp,
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(0.85f)
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // FILTROS
            FloatingMenuItem(
                icon = Icons.Default.FilterList,
                label = "FILTROS",
                onClick = onFilterClick,
                modifier = Modifier.weight(1f)
            )
            
            // NUEVA (Acción Principal Destacada)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .offset(y = (-8).dp)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onNewClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // GUARDAR
            FloatingMenuItem(
                icon = Icons.Default.StarBorder,
                label = "GUARDAR",
                onClick = onSaveClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FloatingMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ConsumptionMeter(
    label: String,
    value: String,
    color: Color,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    fontWeight = FontWeight.Black,
                    color = color
                )
            }
        }
        
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = " $unit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        Spacer(Modifier.height(4.dp))
        
        // Mini Indicator Bar
        Surface(
            modifier = Modifier.width(40.dp).height(4.dp),
            color = color.copy(alpha = 0.2f),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .background(color, CircleShape)
            )
        }
    }
}

