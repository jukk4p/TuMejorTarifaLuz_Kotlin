package com.tumejortarifaluz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpArticleScreen(
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Este artículo te explica detalladamente cómo entender los conceptos básicos de tu factura eléctrica y cómo optimizar tu ahorro.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = """
                    1. Término de Potencia: Es el coste fijo que pagas por tener luz disponible. Se mide en kW y depende de cuántos electrodomésticos quieras usar a la vez.
                    
                    2. Término de Energía: Es la parte variable, lo que consumes realmente. Se mide en kWh. Aquí es donde los precios cambian según la tarifa contratada.
                    
                    3. Impuestos: Incluye el impuesto eléctrico y el IVA. Algunas tarifas incluyen gestiones que pueden reducir estos costes indirectamente.
                    
                    Para ahorrar, es vital elegir una tarifa que se ajuste a tus horas de mayor consumo. Por ejemplo, si teletrabajas, te interesa una tarifa plana o con discriminación horaria amplia.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¿Te ha sido útil este artículo?",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { /* Yes */ }, modifier = Modifier.weight(1f)) {
                            Text("Sí")
                        }
                        OutlinedButton(onClick = { /* No */ }, modifier = Modifier.weight(1f)) {
                            Text("No")
                        }
                    }
                }
            }
        }
    }
}
