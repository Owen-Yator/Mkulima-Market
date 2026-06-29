package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.components.PriceLineChart
import com.mkulimamarket.app.ui.viewmodel.AlertViewModel
import com.mkulimamarket.app.ui.viewmodel.TrendViewModel

private val GreenDeep = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSurface = Color(0xFFF1F8E9)
private val GoldAccent = Color(0xFFF9A825)

@Composable
fun TrendScreen() {

    val viewModel: TrendViewModel = viewModel()
    val trends = viewModel.trends
    val insight = viewModel.getInsight()

    val alertViewModel: AlertViewModel = viewModel()
    val alert = alertViewModel.getAlert()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenSurface)
    ) {

        // Header Banner
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
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = "Price Trends",
                        tint = GoldAccent,
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
                    text = "Weekly price movements at a glance",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.75f)
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {

            // Insight Card
            item {

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
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .fillMaxHeight()
                                .background(
                                    color = GoldAccent,
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        bottomStart = 14.dp
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Market Insight",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = insight,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GreenDeep
                                )
                            )
                        }
                    }
                }
            }

            // Alert Card
            alert?.let {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {

                        Text(
                            text = "🔔 ${it.message}",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Chart Card
            if (trends.isNotEmpty()) {

                item {

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

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Price Chart",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = GreenDeep,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            PriceLineChart(
                                data = trends.map { it.price }
                            )
                        }
                    }
                }
            }

            // Section Title
            item {

                Text(
                    text = "Weekly Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GreenDeep,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Headers
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Week",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.Gray
                        )
                    )

                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            // Empty State
            if (trends.isEmpty()) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No trend data available.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }

            } else {

                items(trends) { trend ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 14.dp
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = trend.week,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GreenDeep
                                )
                            )

                            Text(
                                text = "KSh ${trend.price}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
