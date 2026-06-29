// FILE 3 of 5
// Location: com/mkulimamarket/app/ui/screens/NationalPricesScreen.kt

package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.viewmodel.NationalPricesViewModel

// ── Brand palette ─────────────────────────────────────────────────────────────
private val GreenDeep    = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSurface = Color(0xFFF1F8E9)
private val GoldAccent   = Color(0xFFF9A825)

/**
 * National prices screen — searchable list of commodity prices across Kenya.
 *
 * Improvements over the original:
 * - Gradient header banner matching CountyMarketScreen for visual consistency
 * - Search field styled with rounded corners, a search icon, and clear label copy
 * - Price cards use a structured two-column row (name + price badge)
 * - Empty state gives the user a useful, actionable message
 * - Price text is formatted as "KSh …" to match TrendScreen convention
 */
@Composable
fun NationalPricesScreen() {
    val viewModel   : NationalPricesViewModel = viewModel()
    val prices      = viewModel.prices
    val searchQuery = viewModel.searchQuery

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenSurface)
    ) {

        // ── Header banner ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(GreenDeep, GreenPrimary))
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Filled.ShoppingCart,
                        contentDescription = "National prices",
                        tint               = GoldAccent,
                        modifier           = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text  = "National Prices",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color      = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "WFP / HDX commodity data across Kenya",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.75f)
                    )
                )
            }
        }

        // ── Body ──────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            // Search field
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder   = { Text("Search commodity…") },
                leadingIcon   = {
                    Icon(
                        imageVector        = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint               = GreenPrimary
                    )
                },
                singleLine  = true,
                shape       = RoundedCornerShape(14.dp),
                colors      = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = GreenPrimary,
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor    = GreenPrimary,
                    cursorColor          = GreenPrimary
                ),
                modifier    = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Results count label
            if (prices.isNotEmpty()) {
                Text(
                    text  = "${prices.size} result${if (prices.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // List / empty state
            if (prices.isEmpty()) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = if (searchQuery.isBlank())
                            "No prices available yet."
                        else
                            "No results for \"$searchQuery\".\nTry a different name.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(prices) { item ->
                        NationalPriceCard(
                            commodity = item.commodity,
                            price     = item.price
                        )
                    }
                }
            }
        }
    }
}

// ── National price card ───────────────────────────────────────────────────────

@Composable
private fun NationalPriceCard(
    commodity: String,
    price: String
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text     = commodity,
                style    = MaterialTheme.typography.titleMedium.copy(
                    color      = GreenDeep,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GreenSurface
            ) {
                Text(
                    text     = price,
                    style    = MaterialTheme.typography.labelLarge.copy(
                        color      = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
