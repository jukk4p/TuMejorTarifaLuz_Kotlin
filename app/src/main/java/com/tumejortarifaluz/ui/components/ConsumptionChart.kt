package com.tumejortarifaluz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun ConsumptionChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val spacing = width / (data.size - 1)
                val maxVal = data.maxOrNull() ?: 1f

                // Draw background lines
                val horizontalLines = 4
                for (i in 0..horizontalLines) {
                    val y = height - (height / horizontalLines) * i
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw the line chart
                for (i in 0 until data.size - 1) {
                    val x1 = i * spacing
                    val y1 = height - (data[i] / maxVal) * height
                    val x2 = (i + 1) * spacing
                    val y2 = height - (data[i + 1] / maxVal) * height

                    drawLine(
                        color = lineColor,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Ene", style = MaterialTheme.typography.labelSmall)
            Text("Mar", style = MaterialTheme.typography.labelSmall)
            Text("May", style = MaterialTheme.typography.labelSmall)
            Text("Jul", style = MaterialTheme.typography.labelSmall)
            Text("Sep", style = MaterialTheme.typography.labelSmall)
            Text("Nov", style = MaterialTheme.typography.labelSmall)
        }
    }
}
