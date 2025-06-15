package com.fit2081.yangxuan_33520496

import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.saveable.rememberSaveable
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.yangxuan_33520496.ui.theme.YangXuan_33520496Theme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModelProvider
import com.fit2081.yangxuan_33520496.data.AuthManager
import com.fit2081.yangxuan_33520496.data.FoodIntake
import com.fit2081.yangxuan_33520496.ui.theme.Purple500


class FoodIntakeQuestionnaire : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(applicationContext)
        enableEdgeToEdge()
        // Retrieve clinician ID from Intent
        val clinicianId = AuthManager.getPatientId() ?: ""
        setContent {
            YangXuan_33520496Theme {
                val viewModel: FoodIntakeViewModel = ViewModelProvider(
                    this, FoodIntakeViewModel.FoodIntakeViewModelFactory(this@FoodIntakeQuestionnaire)
                )[FoodIntakeViewModel::class.java]

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FoodIntakeQuestionnaireScreen(
                        clinicianId = clinicianId,
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodIntakeQuestionnaireScreen(clinicianId: String, modifier: Modifier = Modifier, viewModel: FoodIntakeViewModel) {
    val mContext = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadFoodIntakeByUser(clinicianId)
    }
    var persona1 by remember { mutableStateOf<String?>(null) }
    val selectedPersona by viewModel.selectedPersona
    val selectedFoods by viewModel.selectedFoods
    val biggestMealTime by viewModel.biggestMealTime
    val sleepTime by viewModel.sleepTime
    val wakeUpTime by viewModel.wakeUpTime


    // Food categories
    val foodCategories = listOf(
        "Fruits", "Vegetables", "Grains", "Red Meat", "Seafood",
        "Poultry", "Fish", "Eggs", "Nuts/Seeds"
    )
    // Mapping persona names to their respective images
    val personaList = listOf(
        "Health Devotee" to Pair(
            "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
            R.drawable.persona_1
        ),
        "Mindful Eater" to Pair(
            "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
            R.drawable.persona_2
        ),
        "Wellness Striver" to Pair(
            "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
            R.drawable.persona_3
        ),
        "Balance Seeker" to Pair(
            "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
            R.drawable.persona_4
        ),
        "Health Procrastinator" to Pair(
            "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
            R.drawable.persona_5
        ),
        "Food Carefree" to Pair(
            "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.",
            R.drawable.persona_6
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Back Button & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                AuthManager.logout()
                val intent = Intent(mContext, LoginActivity::class.java)
                mContext.startActivity(intent)
            }){
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Food Intake Questionnaire",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp),
        ) {
            // Food Categories Section
            Text(
                "Tick all the food categories you can eat",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Column(horizontalAlignment = Alignment.Start) {
                foodCategories.chunked(3).forEach { rowFoods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowFoods.forEach { food ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = selectedFoods.contains(food),
                                    onCheckedChange = {
                                        viewModel.toggleFoodCategory(clinicianId, food)
                                    }
                                )
                                Text(
                                    text = food,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(0.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text("Your Persona", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    "People can be broadly classified into 6 types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
                    fontSize = 12.sp
                )
                personaList.chunked(3).forEach { rowPersonas ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowPersonas.forEach { (persona, _) ->
                            Button(
                                onClick = {
                                    persona1 = persona
                                    showDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6200EE)
                                ),
                                modifier = Modifier
                                    .width(110.dp) // Set a fixed width to prevent wrapping
                                    .padding(0.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp) // Removes internal padding
                            ) {
                                Text(
                                    text = persona,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(0.dp) // Removes extra padding
                                        .fillMaxWidth(), // Ensures full width usage
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(lineHeight = 12.sp) // Reduces text spacing
                                )
                            }
                        }
                    }
                }

                if (showDialog) {
                    val selectedData = personaList.find { it.first == persona1 }?.second
                    if (selectedData != null) {
                        PersonaDialog(
                            title = persona1.toString(),
                            description = selectedData.first,
                            imageRes = selectedData.second,
                            onDismiss = { showDialog = false }
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(5.dp))

            // Persona Selection Dropdown
            Text("Which persona best fits you?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedPersona,
                    onValueChange = {
                        viewModel.updatePersona(clinicianId, selectedPersona)
                    },
                    readOnly = true,
                    textStyle = TextStyle(fontSize = 14.sp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .height(46.dp) // Adjust height manually if needed
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)) // Custom border
                        .padding(0.dp), // Remove extra padding

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray,
                        disabledBorderColor = Color.LightGray
                    )
                )


                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    personaList.forEach { (persona, _) ->
                        DropdownMenuItem(
                            text = { Text(persona, fontSize = 14.sp) },
                            onClick = {
                                viewModel.updatePersona(clinicianId, persona)
                                expanded = false
                            }
                        )
                    }
                }

            }

        }

        // Timings Section
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Timings", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            TimeRow(
                question = "What time of day approx. do you normally eat your biggest meal?",
                time = biggestMealTime,
                onTimeChange = { newTime ->
                    viewModel.updateBiggestMealTime(clinicianId, newTime)
                }
            )

            TimeRow(
                question = "What time of day approx. do you go to sleep at night?",
                time = sleepTime,
                onTimeChange = { newTime ->
                    viewModel.updateSleepTime(clinicianId, newTime)
                }
            )

            TimeRow(
                question = "What time of day approx. do you wake up in the morning?",
                time = wakeUpTime,
                onTimeChange = { newTime ->
                    viewModel.updateWakeUpTime(clinicianId, newTime)
                }
            )
        }

        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        Button(
            onClick = {
                AuthManager.setFirstLoginNotDone()
                viewModel.saveFoodIntake(userId = clinicianId)
                val intent = Intent(mContext, HomeScreen::class.java)
                Toast.makeText(mContext, "Save Successful", Toast.LENGTH_LONG).show()

                // Create a new Intent and pass Clinician ID to HomeScreen
                intent.putExtra("clinicianId", clinicianId)
                mContext.startActivity(intent)

            },
            colors = ButtonDefaults.buttonColors(Purple500),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(100.dp) // Adjust size as needed
                .height(40.dp)
        ) {
            Text(text = "Save", fontSize = 14.sp, color = Color.White)
        }
    }
}


@Composable
fun PersonaDialog(title: String, description: String, imageRes: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))) {
                Text("Dismiss", color = Color.White)
            }
        },
        title = { Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = description, fontSize = 14.sp)
            }
        }
    )
}

@Composable
fun TimeRow(
    question: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val parts = time.split(":")
    if (parts.size == 2) {
        val hour = parts[0].toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY)
        val minute = parts[1].toIntOrNull() ?: calendar.get(Calendar.MINUTE)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
            onTimeChange(formattedTime) // Notify the caller of the new time
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = question,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { timePickerDialog.show() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Select Time"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = time,
                fontSize = 14.sp,
                modifier = Modifier.wrapContentWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

