package com.fit2081.yangxuan_33520496

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Icon
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.fit2081.yangxuan_33520496.GenAIViewModel.GenAIViewModelFactory
import com.fit2081.yangxuan_33520496.data.UserDatabase
import com.fit2081.yangxuan_33520496.ui.theme.YangXuan_33520496Theme


class NutriCoach: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val clinicianId = intent.getStringExtra("clinicianId") ?: ""
        setContent {
            YangXuan_33520496Theme {
                val context = LocalContext.current

                // Obtain DAO instance from your database singleton
                val dao = UserDatabase.getDatabase(context).motivationalTipDao()

                val genAIViewModel: GenAIViewModel = viewModel(
                    factory = GenAIViewModelFactory(
                        dao = dao,
                        clinicianId = clinicianId
                    )
                )

                val patientViewModel: PatientViewModel = viewModel(
                    factory = PatientViewModel.LoginViewModelFactory(this@NutriCoach)
                )

                // Load gender scores once on first composition
                LaunchedEffect(clinicianId) {
                    patientViewModel.loadGenderScores(clinicianId)
                }

                val navController = rememberNavController()
                var selectedItem by remember { mutableStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MyBottomBar(navController, selectedItem) }
                ) { innerPadding ->
                    Column {
                        MyNavHost(
                            innerPadding,
                            navController,
                            clinicianId,
                            patientViewModel, genAIViewModel
                        ) { selectedIndex ->
                            selectedItem = selectedIndex
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun NutriCoachScreen(
    innerPadding: PaddingValues,
    clinicianId: String,
    navControler: NavHostController,
    viewModel: PatientViewModel,genAiViewModel: GenAIViewModel
) {
    var fruitName by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    var fruitDetails by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    val uiState = genAiViewModel.uiState
    var showAllDialog by remember { mutableStateOf(false) }
    LaunchedEffect(clinicianId) {
        viewModel.checkOptimalScore(clinicianId)
    }
    val isOptimalScore by viewModel.isOptimalScore
    val tips = genAiViewModel.tips


    var motivationalMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NutriCoach",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        Spacer(modifier = Modifier.height(10.dp))

        Log.d("NutriCoachScreen", "isOptimalScore: $isOptimalScore")
        if (isOptimalScore == true) {
            Text(
                text = "A touch of inspiration \uD83D\uDCF8",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // show random image from https://picsum.photos/
            AsyncImage(
                model = "https://picsum.photos/300/200",
                contentDescription = "Random image",
                modifier = Modifier.fillMaxWidth(),
            )
        } else if (isOptimalScore == false) {
            Text(
                text = "Fruit Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = fruitName,
                    onValueChange = { fruitName = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = {
                        if (fruitName.isNotBlank()) {
                            isLoading = true
                            scope.launch {
                                try {
                                    val response = withContext(Dispatchers.IO) {
                                        val apiUrl = "https://www.fruityvice.com/api/fruit/${fruitName.lowercase()}"
                                        val json = java.net.URL(apiUrl).readText()
                                        org.json.JSONObject(json)
                                    }

                                    val nutritions = response.getJSONObject("nutritions")
                                    fruitDetails = mapOf(
                                        "family" to response.optString("family", "N/A"),
                                        "calories" to nutritions.optString("calories", "N/A"),
                                        "fat" to nutritions.optString("fat", "N/A"),
                                        "sugar" to nutritions.optString("sugar", "N/A"),
                                        "carbohydrates" to nutritions.optString("carbohydrates", "N/A"),
                                        "protein" to nutritions.optString("protein", "N/A")
                                    )
                                } catch (e: Exception) {
                                    fruitDetails = mapOf("Error" to "Invalid fruit name.")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier.height(56.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (fruitDetails.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            fruitDetails.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$key",
                                        modifier = Modifier.weight(1f),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = ": $value",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showMessage = true
                genAiViewModel.sendPrompt("Generate a short encouraging message to help someone improve their fruit intake.", fruitDetails,
                    isOptimalScore == true
                )},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "AI Message",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Motivational Message (AI)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

        }

        if (showMessage) {
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Success -> {
                    motivationalMessage = (uiState as UiState.Success).outputText
                    Text(
                        text = motivationalMessage,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                is UiState.Error -> {
                    Text(
                        text = "Something went wrong. Please try again!",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                else -> {}
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        // Show All Tips button
        Button(
            onClick = { showAllDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier
                .padding(16.dp)
                .width(180.dp) // shorter width
                .height(48.dp)

        ) {
            Icon(Icons.Default.Chat, contentDescription = "Tips", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Show All Tips", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        if (showAllDialog) {
            AlertDialog(
                onDismissRequest = { showAllDialog = false },
                confirmButton = {
                    TextButton(onClick = { showAllDialog = false }) {
                        Text("Close")
                    }
                },
                title = { Text("All Motivational Tips") },
                text = {
                    if (tips.isEmpty()) {
                        Text("No tips found for this clinician.")
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            tips.forEach { tip ->
                                Column(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = tip.message)
                                    IconButton(onClick = {
                                        scope.launch {
                                            genAiViewModel.deleteTip(tip)
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                    Divider(modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}