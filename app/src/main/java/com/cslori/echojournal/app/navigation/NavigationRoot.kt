package com.cslori.echojournal.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.cslori.echojournal.echos.presentation.create_echo.CreateEchoScreenRoot
import com.cslori.echojournal.echos.presentation.echos.EchosRoot
import com.cslori.echojournal.echos.presentation.settings.SettingsRoot
import com.cslori.echojournal.echos.presentation.util.toCreateEchoRoute


const val ACTION_CREATE_ECHO = "com.cslori.echojournal.CREATE_ECHO"

@Composable
fun NavigationRoot(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Echos(
            startRecording = false
        ),
    ) {
        composable<NavigationRoute.Echos>(
            deepLinks = listOf(
                navDeepLink<NavigationRoute.Echos>(
                    basePath = "https://echojournal.com/echos"
                ) {
                    action = ACTION_CREATE_ECHO
                }
            )
        ) {
            EchosRoot(
                onNavigateToCreateEcho = { recordingDetails ->
                    navController.navigate(
                        recordingDetails.toCreateEchoRoute()
                    )
                }, onNavigateToSettings = {
                    navController.navigate(NavigationRoute.Settings)
                }
            )
        }
        composable<NavigationRoute.CreateEcho> {
            CreateEchoScreenRoot(
                onConfirmLeave = navController::navigateUp
            )
        }

        composable<NavigationRoute.Settings> {
            SettingsRoot(
                onGoBack = navController::navigateUp
            )
        }
    }
}