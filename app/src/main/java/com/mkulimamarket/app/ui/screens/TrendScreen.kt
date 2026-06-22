package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.viewmodel.TrendViewModel
import com.mkulimamarket.app.ui.components.PriceLineChart

@Composable
fun TrendScreen() {

    val viewModel: TrendViewModel = viewModel()
    val trends = viewModel.trends
    val insight = viewModel.getInsight()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "📊 Price Trends",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // INSIGHT CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = insight,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ GRAPH ADDED HERE
        PriceLineChart(
            data = trends.map { it.price }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Weekly Prices",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {

            items(trends) { trend ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(trend.week)
                        Text("KSh ${trend.price}")
                    }
                }
            }
        }
    }
}
