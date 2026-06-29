// FILE 1 of 5
// Location: com/mkulimamarket/app/ui/components/PriceLineChart.kt

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

// Brand colors kept consistent with the rest of the app
private val ChartLineColor    = Color(0xFF2E7D32)   // Deep forest green
private val ChartPointColor   = Color(0xFF81C784)   // Soft leaf green (dot fill)
private val ChartPointBorder  = Color(0xFF1B5E20)   // Dark border on dots
private val ChartFillTop      = Color(0x552E7D32)   // Semi-transparent fill top
private val ChartFillBottom   = Color(0x002E7D32)   // Transparent fill bottom
private val ChartGridColor    = Color(0x22000000)   // Subtle grid lines

/**
 * A polished line chart for displaying price trends.
 *
 * Improvements over the original:
 * - Filled area under the line for better readability
 * - Subtle horizontal grid lines for scale reference
 * - Clipped rounded corners so it looks like a card element
 * - Larger tap-friendly data points with a white border ring
 * - StrokeCap.Round on lines for a smoother feel
 * - Guards against single-point data (no line to draw, just a dot)
 *
 * @param data  Ordered list of integer prices (oldest → newest, left → right).
 */
@Composable
fun PriceLineChart(
    data: List<Int>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F8E9)) // Very light green tint background
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        val maxValue = data.maxOrNull() ?: return@Canvas
        val minValue = data.minOrNull() ?: return@Canvas
        val range    = (maxValue - minValue).coerceAtLeast(1) // avoid ÷0

        val stepX = if (data.size > 1) size.width / (data.size - 1) else size.width / 2f
        val padV  = 16f // vertical padding so dots don't clip at top/bottom

        fun yFor(value: Int): Float {
            val normalized = (value - minValue).toFloat() / range
            return size.height - padV - normalized * (size.height - padV * 2)
        }

        val points = data.mapIndexed { i, v ->
            Offset(x = i * stepX, y = yFor(v))
        }

        // ── Grid lines (3 horizontal levels) ─────────────────────────────────
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

        // ── Filled area under the line ────────────────────────────────────────
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

            // ── Line connecting all points ────────────────────────────────────
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

        // ── Data points ───────────────────────────────────────────────────────
        points.forEach { pt ->
            // White ring
            drawCircle(color = Color.White,         radius = 9f,  center = pt)
            // Colored fill
            drawCircle(color = ChartPointColor,     radius = 7f,  center = pt)
            // Dark border
            drawCircle(
                color  = ChartPointBorder,
                radius = 7f,
                center = pt,
                style  = Stroke(width = 2f)
            )
        }
    }
}
