package br.itcampos.buildyourhealth

import android.Manifest
import android.content.res.Resources
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.itcampos.buildyourhealth.Routes.HOME_SCREEN
import br.itcampos.buildyourhealth.Routes.LOGIN_SCREEN
import br.itcampos.buildyourhealth.Routes.SIGNUP_SCREEN
import br.itcampos.buildyourhealth.Routes.TRAINING_ID
import br.itcampos.buildyourhealth.Routes.TRAINING_ID_ARG
import br.itcampos.buildyourhealth.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.commom.PermissionDialog
import br.itcampos.buildyourhealth.commom.RationaleDialog
import br.itcampos.buildyourhealth.commom.snackbar.SnackbarManager
import br.itcampos.buildyourhealth.screens.home.HomeScreen
import br.itcampos.buildyourhealth.screens.login.LoginScreen
import br.itcampos.buildyourhealth.screens.sign_up.SignUpScreen
import br.itcampos.buildyourhealth.screens.training.TrainingScreen
import br.itcampos.buildyourhealth.ui.theme.BuildYourHealthTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BuildYourHealthApp() {
    BuildYourHealthTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(
                                snackbarData,
                                contentColor = androidx.compose.material.MaterialTheme.colors.onPrimary
                            )
                        }
                    )
                },
                scaffoldState = appState.scaffoldState
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SIGNUP_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    buildYourHealthGraph(appState)
                }
            }
        }
    }
}


@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    resources: Resources = resources()
) = remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
    BuildYourHealthAppRoute(
        scaffoldState,
        navController,
        snackbarManager,
        resources,
        coroutineScope
    )
}

@Composable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.buildYourHealthGraph(appState: BuildYourHealthAppRoute) {
    composable(SIGNUP_SCREEN) {
        SignUpScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            onLoginTo = { appState.navigate(LOGIN_SCREEN) }
        )
    }
    composable(LOGIN_SCREEN) {
        LoginScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
            onSignUpTo = { appState.popUp() }
        )
    }
    composable(HOME_SCREEN) {
        HomeScreen(openScreen =  { route -> appState.navigate(route) })
    }
    composable(
        route = "$TRAINING_SCREEN/{$TRAINING_ID}",
        arguments = listOf(navArgument(TRAINING_ID) {
            nullable = true
            defaultValue = null
        })
    ) { backStackEntry ->
        val trainingId = backStackEntry.arguments?.getString(TRAINING_ID)
        TrainingScreen(popUpScreen = { appState.popUp() })
        Log.d("Navigation", "TrainingScreen: $trainingId")
    }
}