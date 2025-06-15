package com.fit2081.yangxuan_33520496

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MyBottomBar(navController: NavHostController, selectedItem: Int) {
    val items = listOf("Home", "Insights", "NutriCoach", "Settings")

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (item) {
                        "Home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "Insights" -> Icon(Icons.Filled.Info, contentDescription = "Insights")
                        "NutriCoach" -> Icon(Icons.Filled.Face, contentDescription = "NutriCoach")
                        "Settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    navController.navigate(item)
                }
            )
        }
    }
}

@Composable
fun MyNavHost(
    innerPadding: PaddingValues,
    navControler: NavHostController,
    clinicianId: String,
    viewModel: PatientViewModel,
    genAiViewModel: GenAIViewModel,
    onItemSelected: (Int) -> Unit
) {
    NavHost(
        navController = navControler,
        startDestination = "home"
    ) {
        composable("home") {
            onItemSelected(0)
            HomeScreen(innerPadding, clinicianId, navControler, viewModel)
        }
        composable("insights") {
            onItemSelected(1)
            InsightsScreen(innerPadding, clinicianId, navControler, viewModel)
        }
        composable("nutricoach") {
            onItemSelected(2)
            NutriCoachScreen(innerPadding, clinicianId, navControler, viewModel, genAiViewModel)
        }
        composable("settings") {
            onItemSelected(3)
            SettingScreen(innerPadding, clinicianId, navControler, viewModel)
        }
        composable("clinician_login") {
            ClinicianLoginScreen(
                onLoginSuccess = {
                    navControler.navigate("admin_view")
                }
            )
        }
        composable("admin_view") {
            ClinicianDashboardScreen(innerPadding, viewModel, navControler)
        }

        composable("update_info") {
            updateInfo(viewModel, navControler)
        }
    }

}


