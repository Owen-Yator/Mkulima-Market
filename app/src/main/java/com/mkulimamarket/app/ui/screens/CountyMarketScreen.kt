package com.mkulimamarket.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.data.model.RawPriceEntry
import com.mkulimamarket.app.ui.viewmodel.CountyMarketViewModel

private val GreenDeep = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSurface = Color(0xFFF1F8E9)
private val GoldAccent = Color(0xFFF9A825)

@Composable
fun CountyMarketScreen(
    viewModel: CountyMarketViewModel = viewModel()
) {

    val selectedCounty by viewModel.selectedCounty.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val successState =
        uiState as? CountyMarketViewModel.UiState.Success

    val prices =
        successState?.prices ?: emptyList()

    val sourceMarket =
        successState?.sourceMarket ?: ""

    val isFallback =
        successState?.isFallback ?: false

    val counties = viewModel.counties

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
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = GoldAccent
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "County Markets",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Live commodity prices from monitored WFP markets",
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            Text(
                text = "Select County",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = GreenDeep,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            CountySelectorDropdown(
                counties = counties,
                selected = selectedCounty,
                onSelected = {
                    viewModel.onCountySelected(it)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Prices in $selectedCounty",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GreenDeep,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (prices.isNotEmpty()) {

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = GreenPrimary
                    ) {

                        Text(
                            text = prices.size.toString(),
                            color = Color.White,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = isFallback
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GoldAccent
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text =
                                "No WFP monitored market exists in $selectedCounty.\nShowing prices from the nearest monitored market: $sourceMarket.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (uiState is CountyMarketViewModel.UiState.Loading) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = GreenPrimary
                    )
                }
            }

            AnimatedVisibility(
                visible =
                    prices.isEmpty() &&
                            uiState !is CountyMarketViewModel.UiState.Loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                EmptyState(
                    message =
                        if (uiState is CountyMarketViewModel.UiState.Error)
                            (uiState as CountyMarketViewModel.UiState.Error).message
                        else
                            "No commodity prices are currently available for $selectedCounty or its mapped market."
                )
            }

            AnimatedVisibility(
                visible = prices.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(prices) { item ->

                        CountyPriceCard(
                            entry = item
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountyPriceCard(
    entry: RawPriceEntry
) {
    val hasLocalEquivalent = entry.displayLocalPrice != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = entry.commodity,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GreenDeep,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Market: ${entry.market}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )

                if (hasLocalEquivalent) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.displayLocalPrice!!,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GreenSurface
            ) {

                Text(
                    text = entry.displayPrice,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun CountySelectorDropdown(
    counties: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box {

        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = GreenDeep
            )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = selected,
                    style = MaterialTheme.typography.bodyLarge
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = GreenPrimary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
        ) {

            counties.forEach { county ->

                DropdownMenuItem(

                    text = {

                        Text(
                            text = county,
                            fontWeight =
                                if (county == selected)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                        )
                    },

                    onClick = {

                        expanded = false
                        onSelected(county)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    message: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No Market Data",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenDeep
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}