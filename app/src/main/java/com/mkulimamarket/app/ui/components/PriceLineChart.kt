package com.mkulimamarket.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun PriceLineChart(
    data: List<Int>
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {

        if (data.isEmpty()) return@Canvas

        val maxValue = data.maxOrNull() ?: 0
        val minValue = data.minOrNull() ?: 0
        val range = maxValue - minValue

        val stepX = size.width / (data.size - 1).coerceAtLeast(1)

        val points = data.mapIndexed { index, value ->

            val normalizedY = if (range == 0) {
                size.height / 2
            } else {
                size.height - ((value - minValue).toFloat() / range) * size.height
            }

            Offset(
                x = index * stepX,
                y = normalizedY
            )
        }

        // Draw lines
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color(0xFF2E7D32),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 5f
            )
        }

        // Draw points
        points.forEach {
            drawCircle(
                color = Color(0xFF1B5E20),
                radius = 6f,
                center = it
            )
        }
    }
}