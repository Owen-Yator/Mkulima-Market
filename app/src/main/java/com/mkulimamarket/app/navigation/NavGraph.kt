package com.mkulimamarket.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mkulimamarket.app.ui.screens.HomeScreen
import com.mkulimamarket.app.ui.screens.LoginScreen
import com.mkulimamarket.app.ui.screens.SignupScreen
import com.mkulimamarket.app.ui.screens.SplashScreen
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Signup = "signup"
    const val Home = "home"
}

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {

        composable(Routes.Splash) {
            SplashScreen(
                onNavigate = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onGoToSignup = {
                    navController.navigate(Routes.Signup)
                }
            )
        }

        composable(Routes.Signup) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Signup) { inclusive = true }
                    }
                },
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Home) {
            HomeScreen()
        }
    }
}