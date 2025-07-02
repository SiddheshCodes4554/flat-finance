package com.flatfinance.app.ui.screens.reports.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flatfinance.app.ui.screens.reports.TopExpense
import com.flatfinance.app.ui.screens.reports.getCategoryColor

@Composable
fun BarChart(
    data: List<TopExpense>,
    modifier: Modifier = Modifier
) {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    val density = LocalDensity.current
    val textSize = with(density) { 12.sp.toPx() }
    val labelPadding = with(density) { 8.dp.toPx() }
    
    val maxValue = data.maxOfOrNull { it.amount } ?: 0.0
    
    Canvas(modifier = modifier
        .fillMaxSize()
        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val barWidth = canvasWidth / (data.size * 2)
        val barSpacing = barWidth / 2
        
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
        
        // Draw bars
        data.forEachIndexed { index, expense ->
            val x = index * (barWidth + barSpacing) * 2 + barSpacing
            val barHeight = (expense.amount / maxValue * canvasHeight).toFloat()
            val y = canvasHeight - barHeight
            
            // Draw bar
            drawRect(
                color = getCategoryColor(expense.category),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
            
            // Draw bar outline
            drawRect(
                color = Color.White,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
            )
            
            // Draw x-axis label
            val labelX = x + barWidth / 2
            drawContext.canvas.nativeCanvas.drawText(
                expense.name.take(10) + if (expense.name.length > 10) "..." else "",
                labelX,
                canvasHeight + labelPadding,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor(onSurfaceVariantColor.toHexString())
                    textSize = this@Canvas.textSize
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}