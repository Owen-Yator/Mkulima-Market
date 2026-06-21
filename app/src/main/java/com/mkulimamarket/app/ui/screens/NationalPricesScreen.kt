package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.viewmodel.NationalPricesViewModel

@Composable
fun NationalPricesScreen() {

    val viewModel: NationalPricesViewModel = viewModel()
    val prices = viewModel.prices
    val searchQuery = viewModel.searchQuery

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "🌍 National Prices",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                viewModel.updateSearchQuery(it)
            },
            label = {
                Text("Search commodity")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            if (prices.isEmpty()) {

                item {
                    Text(
                        text = "No commodity found",
                        modifier = Modifier.padding(16.dp)
                    )
                }

            } else {

                items(prices) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = item.commodity,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(text = item.price)
                        }
                    }
                }
            }
        }
    }
}