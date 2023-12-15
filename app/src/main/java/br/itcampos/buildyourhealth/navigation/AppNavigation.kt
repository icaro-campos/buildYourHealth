package br.itcampos.buildyourhealth.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.itcampos.buildyourhealth.navigation.Routes.HOME_SCREEN
import br.itcampos.buildyourhealth.navigation.Routes.LOGIN_SCREEN
import br.itcampos.buildyourhealth.navigation.Routes.SIGNUP_SCREEN
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_ID
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.screens.home.HomeScreen
import br.itcampos.buildyourhealth.screens.login.LoginScreen
import br.itcampos.buildyourhealth.screens.sign_up.SignUpScreen
import br.itcampos.buildyourhealth.screens.training.TrainingScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LOGIN_SCREEN) {
        composable(LOGIN_SCREEN) {
            LoginScreen(openAndPopUp = { route, popUp ->
                navController.navigate(route) {
                    launchSingleTop = true
                    popUpTo(popUp) { inclusive = true }
                }
            },
                onSignUpTo = { navController.navigate(SIGNUP_SCREEN) })
        }
        composable(SIGNUP_SCREEN) {
            SignUpScreen(openAndPopUp = { route, popUp ->
                navController.navigate(route) {
                    launchSingleTop = true
                    popUpTo(popUp) { inclusive = true }
                }
            }, onLoginTo = { navController.navigate(LOGIN_SCREEN) })
        }
        composable(HOME_SCREEN) {
            HomeScreen(openScreen = { route ->
                navController.navigate(route) {
                    launchSingleTop = true
                }
            })
        }
        composable(
            route = TRAINING_SCREEN,
            arguments = listOf(navArgument(TRAINING_ID) {
                nullable = true
                defaultValue = null
            })
        ) {
            TrainingScreen(popUpScreen = { navController.popBackStack() })
        }
    }
}