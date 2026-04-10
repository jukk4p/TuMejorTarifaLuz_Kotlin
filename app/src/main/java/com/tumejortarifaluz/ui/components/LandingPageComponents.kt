package com.tumejortarifaluz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalUriHandler
import com.tumejortarifaluz.R
import com.tumejortarifaluz.ui.theme.PremiumGradient
import com.tumejortarifaluz.ui.theme.BorderColor
import com.tumejortarifaluz.ui.theme.SurfaceCard
import com.tumejortarifaluz.ui.theme.TextSecondary
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle

@Composable
fun TuMejorTarifaLuzLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                contentScale = ContentScale.FillWidth
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = buildAnnotatedString {
                append("TuMejorTarifa")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("Luz")
                }
            },
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            ),
            color = Color.White,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun MobileHero(
    onUploadClick: () -> Unit,
    onMoreInfoClick: () -> Unit,
    totalSaving: String = "+35.40€",
    lastAnalysisDate: String = "No analizado"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier.padding(bottom = 24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Text(
                text = "✨ Tu ahorro real, garantizado",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "Comparamos más de 25 tarifas\npara ofrecerte un ahorro",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                lineHeight = 48.sp,
                letterSpacing = (-1).sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Analizamos tu factura en segundos y encontramos la tarifa que te hará ahorrar hasta 400€ al año.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onUploadClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("COMPARAR MI FACTURA", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onMoreInfoClick,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Text("O introducir datos manualmente", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Premium Hero Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1.4f)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = if (MaterialTheme.colorScheme.background.luminance() < 0.5f)
                            listOf(Color(0xFF0F172A), Color(0xFF030712))
                        else
                            listOf(Color(0xFFE2E8F0), Color(0xFFF1F5F9))
                    )
                )
                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header of the "mock card"
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFEF4444), CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.Analytics, null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Visual chart bars
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    repeat(14) { i ->
                        val targetHeight = listOf(40, 60, 45, 80, 55, 90, 70, 40, 65, 85, 30, 75, 50, 95)[i]
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .fillMaxHeight(targetHeight / 100f)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = if (i == 6) listOf(MaterialTheme.colorScheme.primary, Color(0xFF6366F1))
                                        else listOf(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                                    )
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AHORRO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontWeight = FontWeight.Black)
                        val savingText = if (totalSaving.startsWith("+") || totalSaving.startsWith("-")) totalSaving else "+$totalSaving"
                        Text(savingText, fontWeight = FontWeight.Black, color = Color(0xFF10B981), fontSize = 18.sp)
                    }
                    VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ESTADO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), fontWeight = FontWeight.Black)
                        val statusText = if (lastAnalysisDate == "No analizado") "PENDIENTE" else "OPTIMIZADO"
                        Text(statusText, fontWeight = FontWeight.Black, color = if (statusText == "OPTIMIZADO") Color(0xFF10B981) else MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EL MÉTODO",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Fácil, rápido y real",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        ProcessStepCard(
            index = "01",
            icon = Icons.Default.Description,
            title = "Carga de Datos",
            description = "Sube tu factura o introduce los datos manualmente. Extraeremos tu perfil de consumo al instante."
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProcessStepCard(
            index = "02",
            icon = Icons.Default.Psychology,
            title = "Análisis 360º",
            description = "Cruzamos tus consumos reales con todas las comercializadoras del país para hallar el ahorro real."
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProcessStepCard(
            index = "03",
            icon = Icons.Default.Savings,
            title = "Ahorro al Instante",
            description = "Recibe tu informe personalizado. Tú decides si quieres cambiarte y empezar a pagar menos."
        )
    }
}

@Composable
fun ProcessStepCard(index: String, icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
                    Spacer(Modifier.weight(1f))
                    Text(text = index, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun TestimonialSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Opiniones reales",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row {
                repeat(5) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("4.9/5 Trustpilot", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
        }

        Spacer(modifier = Modifier.height(40.dp))

        TestimonialCard(
            name = "María García",
            role = "Madrid",
            text = "\"Subí mi factura y en 2 minutos ya sabía que podía ahorrar 42€ al mes. Increíble.\""
        )
        Spacer(modifier = Modifier.height(16.dp))
        TestimonialCard(
            name = "Carlos Ruiz",
            role = "Barcelona",
            text = "\"La mejor app para no perderse entre tantas tarifas de luz. Simple y directa.\""
        )
        Spacer(modifier = Modifier.height(16.dp))
        TestimonialCard(
            name = "Elena Martínez",
            role = "Sevilla",
            text = "\"Totalmente recomendada. Transparencia total sin intentar venderte nada.\""
        )
    }
}

@Composable
fun TestimonialCard(name: String, role: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = name, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                    Text(text = role, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyzedCompaniesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "COMPAÑÍAS ANALIZADAS EN\n2026",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
        
        val logos = listOf(
            if (isDark) R.drawable.nibav1 else R.drawable.niba,
            if (isDark) R.drawable.logo_octopus_dark else R.drawable.logo_octopus,
            R.drawable.logo_imaginaenergia,
            R.drawable.logo_visalia,
            if (isDark) R.drawable.logo_repsol_dark else R.drawable.logo_repsol,
            if (isDark) R.drawable.logo_energianufri_dark else R.drawable.logo_energianufri,
            R.drawable.logo_iberdrola,
            R.drawable.logo_endesa,
            R.drawable.logo_naturgy,
            R.drawable.logo_energiavm,
            R.drawable.logo_total_energies,
            if (isDark) "file:///android_asset/logos/logo_neoluxenergy_dark.png" else "file:///android_asset/logos/logo_neoluxenergy_base.webp",
            R.drawable.logo_esluz,
            "file:///android_asset/logos/COR.svg"
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 3
        ) {
            logos.forEach { logoRes ->
                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .width(95.dp)
                        .height(60.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = logoRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TuMejorTarifaLuzFooter() {
    val uriHandler = LocalUriHandler.current
    val baseUrl = "https://www.tumejortarifaluz.es"
    var showSupportMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(top = 64.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TuMejorTarifaLuz",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Tu comparador de tarifas de luz\nindependiente en España. Encuentra la\nmejor tarifa de luz y deja de pagar de\nmás.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text("INFORMACIÓN LEGAL", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, letterSpacing = 1.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        FooterLink("Compañías Eléctricas", onClick = { uriHandler.openUri("$baseUrl/companias") })
        FooterLink("Contacto", onClick = { uriHandler.openUri("$baseUrl/contacto") })
        FooterLink("Política de Privacidad", onClick = { uriHandler.openUri("$baseUrl/legal/privacidad") })
        FooterLink("Aviso legal", onClick = { uriHandler.openUri("$baseUrl/legal/aviso-legal") })
        FooterLink("Política de Cookies", onClick = { uriHandler.openUri("$baseUrl/legal/cookies") })
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text("APOYA EL PROYECTO", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, letterSpacing = 1.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Somos 100% independientes y gratuitos. Si\nte hemos ayudado a ahorrar, considera\napoyarnos para mantener el servicio activo.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(contentAlignment = Alignment.BottomCenter) {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                
                TextButton(
                    onClick = { showSupportMenu = !showSupportMenu },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apoyar Proyecto", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(if (showSupportMenu) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
                
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            }

            if (showSupportMenu) {
                androidx.compose.ui.window.Popup(
                    alignment = Alignment.BottomCenter,
                    properties = androidx.compose.ui.window.PopupProperties(focusable = true),
                    onDismissRequest = { showSupportMenu = false }
                ) {
                    Card(
                        modifier = Modifier
                            .padding(bottom = 66.dp)
                            .width(300.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "ELIGE UNA PLATAFORMA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
                            )
                            
                            PlatformButton(
                                title = "Buy Me a Coffee",
                                subtitle = "La opción más rápida",
                                icon = Icons.Default.LocalCafe,
                                bgColor = Color(0xFFFEF3C7),
                                iconColor = Color(0xFFD97706),
                                onClick = { 
                                    showSupportMenu = false
                                    uriHandler.openUri("https://buymeacoffee.com/tumejortarifaluz")
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            PlatformButton(
                                title = "PayPal",
                                subtitle = "Donación segura",
                                icon = Icons.Default.Payments, 
                                isPaypal = true,
                                bgColor = Color(0xFFDBEAFE),
                                iconColor = Color(0xFF2563EB),
                                onClick = { 
                                    showSupportMenu = false
                                    uriHandler.openUri("https://paypal.me/jukk4p")
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            PlatformButton(
                                title = "Ko-fi",
                                subtitle = "0% comisiones",
                                icon = Icons.Default.Favorite,
                                bgColor = Color(0xFFFCE7F3),
                                iconColor = Color(0xFFDB2777),
                                onClick = { 
                                    showSupportMenu = false
                                    uriHandler.openUri("https://ko-fi.com/tumejortarifaluz")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatformButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    isPaypal: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isPaypal) {
               Text("P", fontWeight = FontWeight.Black, color = iconColor, fontSize = 20.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic) 
            } else {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp)
    )
}
