package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.data.repository.NationalPrice
import com.mkulimamarket.app.ui.viewmodel.NationalPricesViewModel

private val GreenDeep = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSurface = Color(0xFFF1F8E9)
private val GoldAccent = Color(0xFFF9A825)

private val CATEGORY_COLORS = mapOf(
    "Cereals" to Color(0xFFE8F5E9),
    "Pulses" to Color(0xFFFFF8E1),
    "Vegetables" to Color(0xFFE3F2FD),
    "Milk" to Color(0xFFFCE4EC),
    "Oil" to Color(0xFFF3E5F5),
    "Meat" to Color(0xFFEDE7F6),
    "Fish" to Color(0xFFEDE7F6),
    "Egg" to Color(0xFFEDE7F6),
    "Fruit" to Color(0xFFE3F2FD)
)

private fun categoryColor(category: String): Color {
    return CATEGORY_COLORS.entries.firstOrNull {
        category.contains(it.key, ignoreCase = true)
    }?.value ?: Color(0xFFF5F5F5)
}

@Composable
fun NationalPricesScreen(
    viewModel: NationalPricesViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenSurface)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(GreenDeep, GreenPrimary)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = GoldAccent
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "National Prices",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refresh() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Agricultural commodity prices across Kenya",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.onSearchQueryChanged(it)
                },
                placeholder = {
                    Text("Search commodity or category...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GreenPrimary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = GreenPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {

                is NationalPricesViewModel.UiState.Loading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator(
                                color = GreenPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Loading national prices..."
                            )
                        }
                    }
                }

                is NationalPricesViewModel.UiState.Error -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = state.message
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.refresh()
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is NationalPricesViewModel.UiState.Empty -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text =
                                if (searchQuery.isBlank())
                                    "No agricultural price data available."
                                else
                                    "No results found for \"$searchQuery\""
                        )
                    }
                }

                is NationalPricesViewModel.UiState.Success -> {

                    val groupedPrices = state.grouped
                    val totalItems =
                        groupedPrices.values.sumOf { it.size }

                    Text(
                        text = "$totalItems commodities",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        groupedPrices.forEach { (category, prices) ->

                            item {
                                CategoryHeader(category)
                            }

                            items(prices) { price ->

                                NationalPriceCard(price)
                            }

                            item {
                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ───────────────── Category Header ─────────────────

@Composable
private fun CategoryHeader(
    category: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .background(
                    GreenPrimary,
                    RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = category,
            style = MaterialTheme.typography.titleSmall.copy(
                color = GreenDeep,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// ───────────────── National Price Card ─────────────────

@Composable
private fun NationalPriceCard(
    price: NationalPrice
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = price.commodity,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GreenDeep,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${price.marketCount} markets • ${price.latestDate}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )

                // Render local unit equivalent if available (e.g. ≈ KSh 5,580 per 90 kg bag)
                price.displayLocalPrice?.let { localDisplay ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = localDisplay,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = categoryColor(price.category)
            ) {

                Text(
                    text = price.displayPrice,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GreenDeep,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                )
            }
        }
    }
}