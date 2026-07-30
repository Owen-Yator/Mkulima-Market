package com.mkulimamarket.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.domain.model.AlertType
import com.mkulimamarket.app.domain.model.PriceAlert
import com.mkulimamarket.app.domain.model.PriceTrend
import com.mkulimamarket.app.ui.components.PriceLineChart
import com.mkulimamarket.app.ui.viewmodel.TrendTimeRange
import com.mkulimamarket.app.ui.viewmodel.TrendViewModel

private val CanopyGreen = Color(0xFF1B4332)
private val CanopyMid = Color(0xFF2D6A4F)
private val FreshLeaf = Color(0xFF40916C)
private val MaizeGold = Color(0xFFE9A23B)
private val SoilRust = Color(0xFFB5651D)
private val Ivory = Color(0xFFFBF7EE)
private val CardWhite = Color(0xFFFFFFFF)
private val Charcoal = Color(0xFF1F2620)
private val MutedText = Color(0xFF6B7268)

@Composable
fun TrendScreen() {
    val vm: TrendViewModel = viewModel()
    val context = LocalContext.current

    val selectedCommodity by vm.selectedCommodity.collectAsStateWithLifecycle()
    val availableCommodities by vm.availableCommodities.collectAsStateWithLifecycle()
    val selectedRange by vm.selectedRange.collectAsStateWithLifecycle()
    val trends by vm.trends.collectAsStateWithLifecycle()
    val insight by vm.insight.collectAsStateWithLifecycle()
    val alerts by vm.alerts.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Ivory,
        floatingActionButton = {
            if (trends.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val report = vm.buildTextReport(selectedCommodity, trends)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Mkulima Market Report: $selectedCommodity")
                            putExtra(Intent.EXTRA_TEXT, report)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Market Report"))
                    },
                    icon = { Icon(Icons.Filled.IosShare, contentDescription = "Share Report") },
                    text = { Text("Generate Market Report") },
                    containerColor = CanopyGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Ivory)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {

            // Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(CanopyGreen, CanopyMid)))
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = null,
                                tint = MaizeGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Price Trends",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Historical commodity price analysis & market movements",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.75f))
                        )
                    }
                }
            }

            // Crop Selector
            item {
                Spacer(modifier = Modifier.height(18.dp))
                SectionLabel(text = "SELECT CROP", modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(modifier = Modifier.height(8.dp))
                CommoditySelector(
                    commodities = availableCommodities,
                    selected = selectedCommodity,
                    onSelect = vm::selectCommodity
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Chart Card with Time-Range Selector
            item {
                TrendChartCard(
                    commodity = selectedCommodity,
                    trends = trends,
                    selectedRange = selectedRange,
                    onRangeSelected = vm::selectTimeRange,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Market Insight Callout
            item {
                InsightCard(
                    text = insight,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Market Alerts Across All Crops
            if (alerts.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            tint = MaizeGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        SectionLabel(text = "MARKET-WIDE ALERTS")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(alerts, key = { it.commodity }) { alert ->
                    AlertCard(
                        alert = alert,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Weekly Breakdown Table Header
            item {
                SectionLabel(text = "RECORD BREAKDOWN", modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (trends.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recorded price data for $selectedCommodity.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(CanopyGreen)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.85f))
                        )
                        Text(
                            text = "Normalized Price (KSh / kg)",
                            style = MaterialTheme.typography.labelMedium.copy(color = MaizeGold)
                        )
                    }
                }

                itemsIndexed(trends, key = { idx, item -> "${item.week}_$idx" }) { index, trend ->
                    val isLast = index == trends.lastIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .then(
                                if (isLast) Modifier.clip(
                                    RoundedCornerShape(
                                        bottomStart = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                ) else Modifier
                            )
                            .background(CardWhite)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trend.week,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Charcoal)
                        )
                        Text(
                            text = "KSh ${"%.2f".format(trend.price)} / kg",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = FreshLeaf,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (!isLast) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Ivory
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MutedText
        )
    )
}

@Composable
private fun CommoditySelector(
    commodities: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        commodities.forEach { name ->
            val isSelected = name == selected
            Surface(
                onClick = { onSelect(name) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) CanopyGreen else CardWhite,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                    1.dp,
                    CanopyGreen.copy(alpha = 0.3f)
                ) else null
            ) {
                Text(
                    text = name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isSelected) Color.White else CanopyGreen,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun TrendChartCard(
    commodity: String,
    trends: List<PriceTrend>,
    selectedRange: TrendTimeRange,
    onRangeSelected: (TrendTimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title & Trend Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$commodity Overview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Charcoal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (trends.isNotEmpty()) {
                        Text(
                            text = "${trends.first().week}  →  ${trends.last().week}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                        )
                    }
                }
                TrendBadge(trends)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Range Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TrendTimeRange.values().forEach { range ->
                    val active = range == selectedRange
                    Surface(
                        onClick = { onRangeSelected(range) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) CanopyMid else Ivory,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = range.label,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (active) Color.White else Charcoal,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Min/Max Chips
            if (trends.isNotEmpty()) {
                val prices = trends.map { it.price }
                val minPrice = prices.minOrNull() ?: 0.0
                val maxPrice = prices.maxOrNull() ?: 0.0
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriceChip(label = "Low", value = "KSh ${"%.2f".format(minPrice)}/kg", tint = SoilRust)
                    PriceChip(label = "High", value = "KSh ${"%.2f".format(maxPrice)}/kg", tint = FreshLeaf)
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Price Chart
            if (trends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No history available for $selectedRange selection.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                    )
                }
            } else {
                PriceLineChart(
                    data = trends.map { it.price },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendBadge(trends: List<PriceTrend>) {
    if (trends.size < 2) return
    val first = trends.first().price
    val last = trends.last().price
    val pct = if (first != 0.0) ((last - first) / first) * 100 else 0.0
    val (label, color) = when {
        pct > 2.0 -> "▲ ${"%.1f".format(pct)}%" to FreshLeaf
        pct < -2.0 -> "▼ ${"%.1f".format(-pct)}%" to SoilRust
        else -> "➖ Stable" to MutedText
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun PriceChip(label: String, value: String, tint: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.08f))
            .border(width = 1.dp, color = tint.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = MutedText))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(color = tint, fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun InsightCard(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(color = MaizeGold, shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Market Insight",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaizeGold, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Charcoal)
                )
            }
        }
    }
}

@Composable
private fun AlertCard(alert: PriceAlert, modifier: Modifier = Modifier) {
    val (bgColor, borderColor, emoji) = when (alert.type) {
        AlertType.SPIKE -> Triple(FreshLeaf.copy(alpha = 0.08f), FreshLeaf.copy(alpha = 0.30f), "📈")
        AlertType.DROP -> Triple(SoilRust.copy(alpha = 0.08f), SoilRust.copy(alpha = 0.30f), "📉")
        AlertType.STABLE -> Triple(MutedText.copy(alpha = 0.08f), MutedText.copy(alpha = 0.20f), "➖")
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
        Column {
            Text(
                text = alert.commodity,
                style = MaterialTheme.typography.labelLarge.copy(color = Charcoal, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
            )
        }
    }
}