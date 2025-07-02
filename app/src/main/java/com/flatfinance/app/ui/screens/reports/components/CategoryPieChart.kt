package com.flatfinance.app.ui.screens.reports.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.ui.screens.reports.getCategoryColor
import kotlin.math.min

@Composable
fun CategoryPieChart(
    data: Map<ExpenseCategory, Double>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = min(canvasWidth, canvasHeight) / 2 * 0.8f
        val center = Offset(canvasWidth / 2, canvasHeight / 2)
        
        var startAngle = -90f // Start from top
        
        data.forEach { (category, amount) ->
            val sweepAngle = (amount / total * 360f).toFloat()
            
            // Draw segment
            drawArc(
                color = getCategoryColor(category),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            // Draw outline
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 2f)
            )
            
            startAngle += sweepAngle
        }
        
        // Draw center circle for donut chart effect
        drawCircle(
            color = Color.White,
            radius = radius * 0.5f,
            center = center
        )
    }
}