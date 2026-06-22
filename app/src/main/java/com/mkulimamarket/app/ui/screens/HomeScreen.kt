package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Color tokens for Mkulima Market.
 * Worth moving into ui/theme/Color.kt once you're ready to share them
 * across the rest of the app.
 */
private object MkulimaPalette {
    val CanopyGreen = Color(0xFF1B4332)        // primary — deep, grounded green
    val CanopyGreenLight = Color(0xFF2D6A4F)   // gradient partner for the hero
    val FreshLeaf = Color(0xFF40916C)          // secondary green, used for "up" prices
    val MaizeGold = Color(0xFFE9A23B)          // accent — ripe maize
    val SoilRust = Color(0xFFB5651D)           // grounding accent, used for trends / "down" prices
    val SkyDust = Color(0xFF577590)            // muted slate for profile
    val Ivory = Color(0xFFFBF7EE)              // warm background
    val Charcoal = Color(0xFF1F2620)           // primary text
    val MutedText = Color(0xFF6B7268)          // secondary text
}

@Composable
fun HomeScreen(
    onNationalPricesClick: () -> Unit,
    onCountyMarketsClick: () -> Unit,
    onPriceTrendsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkulimaPalette.Ivory)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mkulima Market",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = MkulimaPalette.CanopyGreen
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Habari, Mkulima 👋",
            style = MaterialTheme.typography.bodyLarge,
            color = MkulimaPalette.MutedText
        )

        Spacer(modifier = Modifier.height(20.dp))

        MarketHeroCard(onClick = onNationalPricesClick)

        Spacer(modifier = Modifier.height(16.dp))

        PriceTicker()

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "QUICK ACTIONS",
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MkulimaPalette.MutedText
        )

        Spacer(modifier = Modifier.height(12.dp))

        DashboardCard(
            title = "National Prices",
            description = "View commodity prices across Kenya",
            icon = Icons.Filled.TrendingUp,
            accentColor = MkulimaPalette.FreshLeaf,
            onClick = onNationalPricesClick
        )

        DashboardCard(
            title = "County Markets",
            description = "Compare prices in different counties",
            icon = Icons.Filled.LocationOn,
            accentColor = MkulimaPalette.MaizeGold,
            onClick = onCountyMarketsClick
        )

        DashboardCard(
            title = "Price Trends",
            description = "Analyze market trends over time",
            icon = Icons.Filled.ShowChart,
            accentColor = MkulimaPalette.SoilRust,
            onClick = onPriceTrendsClick
        )

        DashboardCard(
            title = "Profile",
            description = "Manage account settings",
            icon = Icons.Filled.Person,
            accentColor = MkulimaPalette.SkyDust,
            onClick = { }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * The hero: leads with today's most characteristic fact about this app's
 * world — a live price movement — rather than a generic greeting banner.
 * Tapping it opens the same destination as "National Prices" so it does
 * real work, not just decoration.
 */
@Composable
private fun MarketHeroCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MkulimaPalette.CanopyGreen, MkulimaPalette.CanopyGreenLight)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "TODAY'S MARKET",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MkulimaPalette.MaizeGold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Maize ",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "▲ 8%",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = MkulimaPalette.MaizeGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "National farmgate prices climbed this week across Nakuru, Uasin Gishu and Trans Nzoia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View full report",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * The signature element — a small scrolling strip of live-feeling prices.
 * Replace the hardcoded list with real data from your price repository.
 */
@Composable
private fun PriceTicker() {
    val items = listOf(
        Triple("Maize", "KES 52/kg", true),
        Triple("Beans", "KES 110/kg", false),
        Triple("Potatoes", "KES 38/kg", true)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (name, price, isUp) ->
            TickerChip(name = name, price = price, isUp = isUp)
        }
    }
}

@Composable
private fun TickerChip(name: String, price: String, isUp: Boolean) {
    val tint = if (isUp) MkulimaPalette.FreshLeaf else MkulimaPalette.SoilRust
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tint.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = MkulimaPalette.Charcoal
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$price ${if (isUp) "▲" else "▼"}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = tint
            )
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MkulimaPalette.Charcoal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MkulimaPalette.MutedText
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MkulimaPalette.MutedText.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
