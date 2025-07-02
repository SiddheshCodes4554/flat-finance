package com.flatfinance.app.ui.screens.reports.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpenseTrendLineChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    val density = LocalDensity.current
    val textSize = with(density) { 12.sp.toPx() }
    val labelPadding = with(density) { 8.dp.toPx() }
    
    val sortedData = data.entries.sortedBy { it.key }
    val maxValue = data.values.maxOrNull() ?: 0.0
    val minValue = 0.0 // We'll start from 0 for better visualization
    
    Canvas(modifier = modifier
        .fillMaxSize()
        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val xStep = canvasWidth / (sortedData.size - 1)
        val yStep = canvasHeight / (maxValue - minValue).toFloat()
        
        // Draw horizontal grid lines
        val gridLineCount = 5
        val gridStep = canvasHeight / gridLineCount
        
        for (i in 0..gridLineCount) {
            val y = canvasHeight - (i * gridStep)
            
            // Draw grid line
            drawLine(
                color = surfaceVariantColor,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            
            // Draw y-axis label
            val value = (maxValue * i / gridLineCount).toInt()
            drawContext.canvas.nativeCanvas.drawText(
                value.toString(),
                0f,
                y - labelPadding,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor(onSurfaceVariantColor.toHexString())
                    textSize = this@Canvas.textSize
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }
        
        // Draw line chart
        val path = Path()
        
        sortedData.forEachIndexed { index, (label, value) ->
            val x = index * xStep
            val y = canvasHeight - ((value - minValue) * yStep).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            // Draw data point
            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            
            // Draw x-axis label
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x - textSize / 2,
                canvasHeight + labelPadding,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor(onSurfaceVariantColor.toHexString())
                    textSize = this@Canvas.textSize
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
        
        // Draw line
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Draw filled area under the line
        val filledPath = Path().apply {
            addPath(path)
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }
        
        drawPath(
            path = filledPath,
            color = primaryColor.copy(alpha = 0.2f)
        )
    }
}

// Extension function to convert Color to hex string
fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}