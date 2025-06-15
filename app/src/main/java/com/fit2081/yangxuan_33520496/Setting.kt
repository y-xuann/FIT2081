package com.fit2081.yangxuan_33520496

import android.content.Intent
import androidx.compose.runtime.collectAsState
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit2081.yangxuan_33520496.GenAIViewModel.GenAIViewModelFactory
import com.fit2081.yangxuan_33520496.data.AuthManager
import com.fit2081.yangxuan_33520496.data.UserDatabase
import com.fit2081.yangxuan_33520496.ui.theme.YangXuan_33520496Theme

class Setting : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(applicationContext)
        enableEdgeToEdge()
        val clinicianId = AuthManager.getPatientId() ?: ""
        setContent {
            YangXuan_33520496Theme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.LoginViewModelFactory(this@Setting)
                )[PatientViewModel::class.java]

                val navController: NavHostController = rememberNavController()
                var selectedItem by remember { mutableStateOf(0) }
                val context = LocalContext.current

                // Obtain DAO instance from your database singleton
                val dao = UserDatabase.getDatabase(context).motivationalTipDao()

                val genAIViewModel: GenAIViewModel = viewModel(
                    factory = GenAIViewModelFactory(
                        dao = dao,
                        clinicianId = clinicianId
                    )
                )
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MyBottomBar(navController, selectedItem) }
                ) { innerPadding ->
                    Column {
                        MyNavHost(
                            innerPadding,
                            navController,
                            clinicianId,
                            viewModel, genAIViewModel
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
fun SettingScreen(
    innerPadding: PaddingValues,
    clinicianId: String,
    navController: NavHostController,
    viewModel: PatientViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadPatient(clinicianId)
    }
    val name by viewModel.name
    val phone by viewModel.phoneNumber
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "ACCOUNT",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AccountInfoItem(icon = Icons.Default.Person, label = name)
        Spacer(modifier = Modifier.height(10.dp))
        AccountInfoItem(icon = Icons.Default.Call, label = phone)
        Spacer(modifier = Modifier.height(10.dp))
        AccountInfoItem(icon = Icons.Default.DateRange, label = clinicianId)
        Spacer(modifier = Modifier.height(10.dp))
        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "OTHER SETTINGS",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OtherSettingItem(
            icon = Icons.Default.ExitToApp,
            label = "Logout"
        ) {
            showLogoutDialog = true
        }

        OtherSettingItem(
            icon = Icons.Default.Person,
            label = "Clinician Login"
        ) {
            navController.navigate("clinician_login")
        }

        OtherSettingItem(
            icon = Icons.Default.Edit,
            label = "Update Info"
        ) {
            navController.navigate("update_info")
        }

    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Confirm Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        AuthManager.logout()
                        showLogoutDialog = false
                        // Launch the login activity using Intent
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}



@Composable
fun AccountInfoItem(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp)
    }
}

@Composable
fun OtherSettingItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Color.Gray
        )
    }

}
@Composable
fun ClinicianLoginScreen(onLoginSuccess: () -> Unit) {
    var keyInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Clinician Login", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = keyInput,
            onValueChange = {
                keyInput = it
                errorMessage = ""
            },
            label = { Text("Clinician Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (keyInput == "dollar-entry-apples") {
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid key. Please try again."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)) // Purple
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clinician Login")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(errorMessage, color = Color.Red)
        }
    }
}

@Composable
fun ClinicianDashboardScreen(
    innerPadding: PaddingValues,
    viewModel: PatientViewModel = viewModel(),
    navController: NavHostController
) {
    // Load averages once on first composition
    LaunchedEffect(Unit) {
        viewModel.loadHeifaAverages()
    }

    val avgMaleScore = viewModel.averageMale.collectAsState().value
    val avgFemaleScore = viewModel.averageFemale.collectAsState().value
    val rawInsights = viewModel.dataPatterns
    val insightList = parseInsights(rawInsights)
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Clinician Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // HEIFA Averages
        OutlinedTextField(
            value = "Average HEIFA (Male): ${avgMaleScore ?: "Loading..."}",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = "Average HEIFA (Female): ${avgFemaleScore ?: "Loading..."}",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        )

        // Find Data Pattern Button
        Button(
            onClick = {
                Log.d("PatientViewModel", "Button pressed")
                viewModel.findDataPatterns() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0DAD))
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Data Pattern", color = Color.White)
        }

        // Show AI-generated insights
        if (insightList.isEmpty()) {
            Text(
                text = "No insights found yet.",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            insightList.forEach { insight ->
                DataInsightCard(title = stripMarkdown(insight.title), content = stripMarkdown(insight.content))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Done Button (Navigate to settings)
        Button(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0DAD))
        ) {
            Text("Done", color = Color.White)
        }
    }
}



@Composable
fun DataInsightCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(content, style = MaterialTheme.typography.bodyMedium)
        }
    }
    Spacer(modifier = Modifier.width(20.dp))
}

@Composable
fun updateInfo(viewModel: PatientViewModel, navController: NavHostController) {
    val context = LocalContext.current

    var selectedClinicianId = AuthManager.getPatientId() ?: ""
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Update Info", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Confirm Pass
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    name.isBlank()|| phoneNumber.isBlank() ||
                            password.isBlank() || confirmPassword.isBlank() -> {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }

                    password != confirmPassword -> {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        viewModel.registerPatient(
                            selectedClinicianId,
                            phoneNumber,
                            name,
                            password
                        ) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                navController.navigate("settings")
                            }
                        }
                    }
                }
            }
        ) {
            Text("Update")
        }
    }
}


