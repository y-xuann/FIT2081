package com.fit2081.yangxuan_33520496

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fit2081.yangxuan_33520496.data.AuthManager
import com.fit2081.yangxuan_33520496.ui.theme.Purple500
import com.fit2081.yangxuan_33520496.ui.theme.YangXuan_33520496Theme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YangXuan_33520496Theme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.LoginViewModelFactory(this@LoginActivity)
                )[PatientViewModel::class.java]
                LoginScreen(viewModel)

                viewModel.loadInitialDataFromCSV(this@LoginActivity)
                Log.d("LoginActivity", "onCreate")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: PatientViewModel) {
    val localContext = LocalContext.current

    // Collects the StateFlow from the ViewModel and converts it into a Compose State.
    // This ensures the UI automatically re-composes whenever the registeredPatients list updates.
    val listOfRegisteredPatients by viewModel.registeredPatients.collectAsState(initial = emptyList())

    var password by remember { mutableStateOf("") }
    var selectedClinicianId by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // ** Exposed Dropdown Menu for Clinician ID **
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedClinicianId,
                onValueChange = {},
                readOnly = true,
                label = { Text("My ID (Provided by your Clinician)") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // iterate through the list of the patients and add their user id to the dropdown menu
                listOfRegisteredPatients.forEach { patient ->
                    DropdownMenuItem(
                        text = { Text(patient.userId) },
                        onClick = {
                            selectedClinicianId = patient.userId
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // **Phone Number Input**
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This app is only for pre-registered users. Please have your ID and phone number handy before continuing.",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                if (selectedClinicianId.isNotEmpty() && password.isNotEmpty()) {
                    Log.d("LoginScreen", selectedClinicianId)
                    Log.d("LoginScreen", password)
                    viewModel.validateLogin(selectedClinicianId, password) { success ->
                        if (success) {
                            Toast.makeText(localContext, "Login Successful", Toast.LENGTH_LONG).show()

                            // Navigate to next activity
                            val intent = Intent(localContext, FoodIntakeQuestionnaire::class.java)
                            localContext.startActivity(intent)

                            AuthManager.login(selectedClinicianId)
                        } else {
                            Toast.makeText(localContext, "Incorrect Credentials", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(localContext, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Purple500)
        ) {
            Text(text = "Continue", color = Color.White)
        }

        // Register Button
        Button(
            onClick = {
                // Navigate to RegisterActivity
                val intent = Intent(localContext, RegisterActivity::class.java)
                localContext.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(Purple500)
        ) {
            Text(text = "Register", color = Color.White)
        }

    }
}

