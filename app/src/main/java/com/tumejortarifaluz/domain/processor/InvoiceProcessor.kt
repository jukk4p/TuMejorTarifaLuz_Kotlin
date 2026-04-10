package com.tumejortarifaluz.domain.processor

import android.graphics.Bitmap
import com.tumejortarifaluz.domain.model.ProcessedInvoice
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceProcessor @Inject constructor() {

    // Using Standalone Google AI SDK (Legacy 2026 Support)
    private val apiKey = BuildConfig.GEMINI_API_KEY
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    suspend fun processImage(bitmap: Bitmap): ProcessedInvoice {
        android.util.Log.d("InvoiceProcessor", "Starting AI processing (Standalone SDK). Bitmap: ${bitmap.width}x${bitmap.height}")
        
        return try {
            val prompt = """
                ERES UN EXPERTO EN FACTURACIÓN ELÉCTRICA ESPAÑOLA. 
                Analiza el documento adjunto y extrae los datos técnicos. 

                REGLAS DE ORO PARA EVITAR ERRORES:
                1. CUPS: Código de 20-22 caracteres (ES...). Búscalo en 'Datos del punto de suministro'.
                2. POTENCIA (kW): Busca la potencia contratada. Suele haber dos (Punta/P1 y Valle/P2). 
                3. ENERGÍA ACTIVA (kWh) - IMPORTANTE:
                   - Busca el 'Consumo del periodo' para P1, P2 y P3. 
                   - ¡SUMA LOS VALORES!: Si extraes P1, P2 y P3, súmalos y asegúrate de que el total coincida con el 'Consumo total' que indique la factura. 
                   - Si hay incongruencias, confía siempre en el desglose de consumos del periodo.
                4. PERIODO DE FACTURACIÓN: Cuenta los días entre las fechas indicadas. 
                5. TOTAL FACTURA (€): Importe final con impuestos. 

                VERIFICACIÓN CRUZADA: Antes de responder, comprueba que no has confundido el precio (€/kWh) con el consumo (kWh).

                Responde ÚNICAMENTE un objeto JSON válido:
                {
                  "company": "Nombre",
                  "cups": "ES...", 
                  "powerP1": 0.0,
                  "powerP2": 0.0,
                  "energyP1": 0.0,
                  "energyP2": 0.0,
                  "energyP3": 0.0,
                  "days": 0,
                  "totalAmount": 0.0
                }
                Si no encuentras un dato, usa null. NO inventes. 
                Devuelve solo el JSON puro.
            """.trimIndent()

            val response = generativeModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
            
            val jsonResponse = response.text
            if (jsonResponse == null) {
                android.util.Log.e("InvoiceProcessor", "Standalone AI Response NULL. Check API Key and internet.")
                return ProcessedInvoice(company = "Error: Sin respuesta de IA")
            }
            
            android.util.Log.d("InvoiceProcessor", "Standalone AI response: $jsonResponse")
            
            val cleanedJson = sanitizeJson(jsonResponse)
            parseJsonResult(cleanedJson)
        } catch (e: Exception) {
            // Robust error handling for the 404/500 cases in 2026
            val errorMsg = e.message ?: "Unknown Server Error (Standalone 2026)"
            android.util.Log.e("InvoiceProcessor", "Standalone AI CRITICAL ERROR: $errorMsg", e)
            
            // Return dummy data or error message to UI instead of crashing
            ProcessedInvoice(company = "Error de conexión (Standalone)")
        }
    }

    private fun sanitizeJson(raw: String): String {
        return if (raw.contains("```json")) {
            raw.substringAfter("```json").substringBefore("```").trim()
        } else if (raw.contains("```")) {
            raw.substringAfter("```").substringBefore("```").trim()
        } else {
            raw.trim()
        }
    }

    private fun parseJsonResult(jsonString: String): ProcessedInvoice {
        return try {
            val json = Json.parseToJsonElement(jsonString).jsonObject
            
            fun parseDouble(key: String): Double {
                val value = json[key]?.jsonPrimitive?.content ?: return 0.0
                return value.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
            }

            ProcessedInvoice(
                company = json["company"]?.jsonPrimitive?.content ?: "Suministradora Actual",
                cups = json["cups"]?.jsonPrimitive?.content,
                powerP1 = parseDouble("powerP1").takeIf { it > 0 } ?: 4.6,
                powerP2 = parseDouble("powerP2").takeIf { it > 0 } ?: (parseDouble("powerP1").takeIf { it > 0 } ?: 4.6),
                energyP1 = parseDouble("energyP1"),
                energyP2 = parseDouble("energyP2"),
                energyP3 = parseDouble("energyP3"),
                days = json["days"]?.jsonPrimitive?.content?.toIntOrNull() ?: 30,
                totalAmount = json["totalAmount"]?.jsonPrimitive?.content?.replace(",", ".")?.plus(" €")
            )
        } catch (e: Exception) {
            android.util.Log.e("InvoiceProcessor", "Parsing error in Standalone JSON: $jsonString", e)
            ProcessedInvoice(company = "Error en formato de datos")
        }
    }
}
