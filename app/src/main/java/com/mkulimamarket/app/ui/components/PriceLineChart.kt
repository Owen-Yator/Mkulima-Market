package com.mkulimamarket.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private val ChartLineColor    = Color(0xFF2E7D32)
private val ChartPointColor   = Color(0xFF81C784)
private val ChartPointBorder  = Color(0xFF1B5E20)
private val ChartFillTop      = Color(0x552E7D32)
private val ChartFillBottom   = Color(0x002E7D32)
private val ChartGridColor    = Color(0x22000000)

@Composable
fun PriceLineChart(
    data: List<Double>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F8E9))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        val maxValue = data.maxOrNull() ?: return@Canvas
        val minValue = data.minOrNull() ?: return@Canvas
        val range    = (maxValue - minValue).coerceAtLeast(1.0)

        val stepX = if (data.size > 1) size.width / (data.size - 1) else size.width / 2f
        val padV  = 16f

        fun yFor(value: Double): Float {
            val normalized = (value - minValue).toFloat() / range.toFloat()
            return size.height - padV - normalized * (size.height - padV * 2)
        }

        val points = data.mapIndexed { i, v ->
            Offset(x = i * stepX, y = yFor(v))
        }

        val gridCount = 3
        repeat(gridCount) { i ->
            val y = (size.height / (gridCount + 1)) * (i + 1)
            drawLine(
                color       = ChartGridColor,
                start       = Offset(0f, y),
                end         = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        if (points.size > 1) {
            val fillPath = Path().apply {
                moveTo(points.first().x, size.height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, size.height)
                close()
            }
            drawPath(
                path  = fillPath,
                brush = Brush.verticalGradient(
                    colors     = listOf(ChartFillTop, ChartFillBottom),
                    startY     = 0f,
                    endY       = size.height
                )
            )

            for (i in 0 until points.size - 1) {
                drawLine(
                    color       = ChartLineColor,
                    start       = points[i],
                    end         = points[i + 1],
                    strokeWidth = 4f,
                    cap         = StrokeCap.Round
                )
            }
        }

        points.forEach { pt ->
            drawCircle(color = Color.White,         radius = 9f,  center = pt)
            drawCircle(color = ChartPointColor,     radius = 7f,  center = pt)
            drawCircle(
                color  = ChartPointBorder,
                radius = 7f,
                center = pt,
                style  = Stroke(width = 2f)
            )
        }
    }
}