package com.tumejortarifaluz.ui.screens

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.tumejortarifaluz.ui.components.TuMejorTarifaLuzLogo
import com.tumejortarifaluz.ui.viewmodel.UploadViewModel
import com.tumejortarifaluz.domain.model.ProcessedInvoice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadInvoiceScreen(
    onUploadSuccess: () -> Unit,
    onNavigateToManual: () -> Unit,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(it)
            
            val bitmap = if (mimeType == "application/pdf") {
                renderPdfToBitmap(context, it)
            } else {
                if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                } else {
                    val source = android.graphics.ImageDecoder.createSource(contentResolver, it)
                    android.graphics.ImageDecoder.decodeBitmap(source)
                }
            }
            
            bitmap?.let { b -> viewModel.startUpload(b) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = { TuMejorTarifaLuzLogo() },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)) }
                    IconButton(onClick = {}) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            color = if (uiState.isLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = if (uiState.isLoggedIn) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
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
                Column(
                    modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val displayName = if (uiState.isLoggedIn) {
                        uiState.userEmail?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Usuario"
                    } else "Invitado"
                    Text(
                        text = "Bienvenido, $displayName", 
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp), 
                        fontWeight = FontWeight.Black, 
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Empecemos analizando tu factura actual.", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                // Glassmorphism Picker Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            ),
                            RoundedCornerShape(32.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "Analizador de factura", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
                                Text(text = "Detección inteligente de datos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Drop Zone with Glow
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .clickable { launcher.launch("*/*") }
                                .background(Color.White.copy(alpha = 0.02f)),
                            border = BorderStroke(
                                1.dp, 
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                            color = Color.Transparent
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudUpload, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Toca para subir una foto o PDF", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text("Formatos admitidos: JPG, PNG, PDF", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("USAR CÁMARA", fontWeight = FontWeight.Black)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextButton(
                            onClick = onNavigateToManual,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("O introduce los datos manualmente", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Data Summary Section
            item {
                val scan = uiState.scanResult
                val hasScan = scan != null
                Spacer(modifier = Modifier.height(32.dp))
                
                if (hasScan) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Speed, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = "DATOS DETECTADOS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            PowerCard(modifier = Modifier.weight(1f), label = "P1 (PUNTA)", value = scan?.powerP1?.toString() ?: "4.6", color = Color(0xFF60A5FA))
                            PowerCard(modifier = Modifier.weight(1f), label = "P2 (VALLE)", value = scan?.powerP2?.toString() ?: "4.6", color = Color(0xFF818CF8))
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                PeriodItem(label = "Periodo P1 (Punta)", value = scan?.energyP1?.toInt()?.toString() ?: "--", color = Color(0xFFEF4444))
                                PeriodItem(label = "Periodo P2 (Llano)", value = scan?.energyP2?.toInt()?.toString() ?: "--", color = Color(0xFFF59E0B))
                                PeriodItem(label = "Periodo P3 (Valle)", value = scan?.energyP3?.toInt()?.toString() ?: "--", color = Color(0xFF22C55E))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Premium Finish Button
                        Surface(
                            onClick = { onUploadSuccess() },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(24.dp),
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Analytics, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(text = "Finalizar y comparar", fontWeight = FontWeight.Black, color = Color.White)
                                    Text(text = "Calcularemos tu ahorro real ahora", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (uiState.isComplete) {
            LaunchedEffect(Unit) {
                onUploadSuccess()
            }
        }

        if (uiState.isUploading) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = uiState.statusMessage,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Analizando píxeles...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PowerCard(modifier: Modifier = Modifier, label: String, value: String, color: Color = Color.White) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "kW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(color.copy(alpha = 0.3f)))
        }
    }
}

@Composable
fun PeriodItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "kWh", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * Renders the first and second pages of a PDF file into a single vertical Bitmap.
 */
fun renderPdfToBitmap(context: android.content.Context, uri: Uri): android.graphics.Bitmap? {
    android.util.Log.d("InvoiceProcessor", "Rendering PDF (Multi-page) for URI: $uri")
    return try {
        val contentResolver = context.contentResolver
        val pfd = contentResolver.openFileDescriptor(uri, "r") ?: return null
        val renderer = android.graphics.pdf.PdfRenderer(pfd)
        
        val totalPages = renderer.pageCount
        android.util.Log.d("InvoiceProcessor", "PDF Pages: $totalPages")
        
        if (totalPages == 0) {
            renderer.close(); pfd.close(); return null
        }

        // Render Page 0
        val page0 = renderer.openPage(0)
        val bitmap0 = android.graphics.Bitmap.createBitmap(page0.width * 2, page0.height * 2, android.graphics.Bitmap.Config.ARGB_8888)
        page0.render(bitmap0, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page0.close()

        val finalBitmap = if (totalPages > 1) {
            // Render Page 1 and merge
            val page1 = renderer.openPage(1)
            val bitmap1 = android.graphics.Bitmap.createBitmap(page1.width * 2, page1.height * 2, android.graphics.Bitmap.Config.ARGB_8888)
            page1.render(bitmap1, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page1.close()

            val merged = android.graphics.Bitmap.createBitmap(bitmap0.width, bitmap0.height + bitmap1.height, bitmap0.config)
            val canvas = android.graphics.Canvas(merged)
            canvas.drawColor(android.graphics.Color.WHITE)
            canvas.drawBitmap(bitmap0, 0f, 0f, null)
            canvas.drawBitmap(bitmap1, 0f, bitmap0.height.toFloat(), null)
            
            bitmap0.recycle()
            bitmap1.recycle()
            merged
        } else {
            bitmap0
        }

        renderer.close()
        pfd.close()
        android.util.Log.d("InvoiceProcessor", "Rendering COMPLETE. Merged Dimensions: ${finalBitmap.width}x${finalBitmap.height}")
        finalBitmap
    } catch (e: Exception) {
        android.util.Log.e("InvoiceProcessor", "PDF Rendering Error: ${e.message}", e)
        null
    }
}
