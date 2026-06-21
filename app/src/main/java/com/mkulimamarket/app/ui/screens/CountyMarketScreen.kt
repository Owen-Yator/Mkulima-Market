package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.ui.viewmodel.CountyMarketViewModel

@Composable
fun CountyMarketScreen(
    viewModel: CountyMarketViewModel = viewModel()
) {
    val prices = viewModel.prices
    var selectedCounty by remember { mutableStateOf("Nakuru") }

    val counties = listOf("Nakuru", "Kiambu", "Machakos", "Narok", "Bungoma")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📍 County Markets",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        DropdownMenuBox(
            counties = counties,
            selected = selectedCounty,
            onSelected = { selectedCounty = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(prices) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.market, style = MaterialTheme.typography.titleLarge)
                        Text("${item.commodity} - ${item.price}")
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    counties: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(selected)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            counties.forEach { county ->
                DropdownMenuItem(
                    text = { Text(county) },
                    onClick = {
                        onSelected(county)
                        expanded = false
                    }
                )
            }
        }
    }
}
