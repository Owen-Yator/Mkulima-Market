package com.mkulimamarket.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mkulimamarket.app.navigation.NavGraph
import com.mkulimamarket.app.ui.theme.MkulimaMarketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MkulimaMarketTheme {

                val navController = rememberNavController()

                NavGraph(navController = navController)
            }
        }
    }
}