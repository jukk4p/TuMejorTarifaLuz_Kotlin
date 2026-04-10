package com.tumejortarifaluz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.SupportAgent
import com.tumejortarifaluz.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class Message(val text: String, val isFromUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                Message("¡Hola! Soy tu asistente de TuMejorTarifaLuz. ¿En qué puedo ayudarte hoy?", false),
                Message("Tengo una duda sobre mi última factura analizada.", true),
                Message("Claro, indícame qué parte no te queda clara y la revisamos juntos.", false)
            )
        )
    }
    var currentText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    val assistantReplies = mapOf(
        "factura" to "Para analizar tu factura, asegúrate de que todos los datos coincidan exactamente con el documento PDF o la factura física de tu comercializadora.",
        "ahorro" to "Nuestra estimación de ahorro toma en cuenta los precios actuales de más de 30 comercializadoras en España.",
        "cups" to "El código CUPS es como el DNI de tu punto de suministro. Empieza por ES y tiene entre 20 y 22 caracteres.",
        "hola" to "¡Hola! Soy el asistente virtual de TuMejorTarifaLuz. ¿En qué puedo ayudarte con tu factura eléctrica?",
        "gracias" to "¡De nada! Estoy aquí para ayudarte a ahorrar. ¿Tienes alguna otra duda?"
    )

    fun getAssistantReply(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()
        for ((keyword, reply) in assistantReplies) {
            if (lowerMessage.contains(keyword)) return reply
        }
        return "Entiendo. ¿Podrías darme más detalles sobre tu consulta para que pueda ayudarte mejor?"
    }

    LaunchedEffect(messages) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null && lastMessage.isFromUser) {
            isTyping = true
            kotlinx.coroutines.delay(1500)
            val reply = getAssistantReply(lastMessage.text)
            messages = messages + Message(reply, false)
            isTyping = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Soporte TuMejorTarifaLuz", style = MaterialTheme.typography.titleMedium)
                            if (isTyping) {
                                Text("Escribiendo...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.ime),
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it },
                    modifier = Modifier.weight(1f).padding(8.dp),
                    placeholder = { Text("Escribe un mensaje...") },
                    shape = RoundedCornerShape(24.dp),
                    enabled = !isTyping
                )
                IconButton(
                    onClick = {
                        if (currentText.isNotBlank()) {
                            messages = messages + Message(currentText, true)
                            currentText = ""
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    enabled = !isTyping && currentText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = if (isTyping || currentText.isBlank()) TextSecondary else MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            reverseLayout = false
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (message.isFromUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (message.isFromUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = shape
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
