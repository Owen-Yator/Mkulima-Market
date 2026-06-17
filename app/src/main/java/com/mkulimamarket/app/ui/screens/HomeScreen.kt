package com.mkulimamarket.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "🌾 Mkulima Market",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome Farmer",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        DashboardCard(
            title = "National Prices",
            description = "View commodity prices across Kenya"
        )

        DashboardCard(
            title = "County Markets",
            description = "Compare prices in different counties"
        )

        DashboardCard(
            title = "Price Trends",
            description = "Analyze market trends"
        )

        DashboardCard(
            title = "Profile",
            description = "Manage account settings"
        )
    }
}

@Composable
fun DashboardCard(
    title: String,
    description: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = description)
        }
    }
}