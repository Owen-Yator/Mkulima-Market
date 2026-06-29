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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.viewmodel.CountyMarketViewModel

// Brand Colors
private val GreenDeep = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenLight = Color(0xFF81C784)
private val GreenSurface = Color(0xFFF1F8E9)
private val GoldAccent = Color(0xFFF9A825)

@Composable
fun CountyMarketScreen(
    viewModel: CountyMarketViewModel = viewModel()
) {

    // Observe selected county from ViewModel
    val selectedCounty by viewModel.county.collectAsStateWithLifecycle()

    // Observe prices for selected county from ViewModel StateFlow
    val prices by viewModel.prices.collectAsStateWithLifecycle()

    // Kenya counties
    val counties = listOf(
        "Baringo", "Bomet", "Bungoma", "Busia",
        "Elgeyo Marakwet", "Embu", "Garissa",
        "Homa Bay", "Isiolo", "Kajiado",
        "Kakamega", "Kericho", "Kiambu",
        "Kilifi", "Kirinyaga", "Kisii",
        "Kisumu", "Kitui", "Kwale",
        "Laikipia", "Lamu", "Machakos",
        "Makueni", "Mandera", "Marsabit",
        "Meru", "Migori", "Mombasa",
        "Murang'a", "Nairobi", "Nakuru",
        "Nandi", "Narok", "Nyamira",
        "Nyandarua", "Nyeri", "Samburu",
        "Siaya", "Taita Taveta", "Tana River",
        "Tharaka Nithi", "Trans Nzoia",
        "Turkana", "Uasin Gishu",
        "Vihiga", "Wajir", "West Pokot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenSurface)
    ) {

        // Header
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
                    text = "Live prices from local markets",
                    color = Color.White.copy(alpha = 0.8f)
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
                    viewModel.setCounty(it)
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
                            text = "${prices.size}",
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
                visible = prices.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                EmptyState(
                    message = "No prices found for $selectedCounty.\nCheck back soon."
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
                            market = item.market,
                            commodity = item.commodity,
                            price = item.displayPrice
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountyPriceCard(
    market: String,
    commodity: String,
    price: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    text = commodity,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GreenDeep,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = market,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GreenSurface
            ) {

                Text(
                    text = price,
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
fun CountySelectorDropdown(
    counties: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box {

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = selected
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
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
                        onSelected(county)
                        expanded = false
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray
            )
        )
    }
}
