package com.fit2081.yangxuan_33520496


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit2081.yangxuan_33520496.data.AuthManager
import com.fit2081.yangxuan_33520496.data.UserDatabase
import com.fit2081.yangxuan_33520496.ui.theme.YangXuan_33520496Theme

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(applicationContext)
        enableEdgeToEdge()

        val clinicianId = AuthManager.getPatientId().toString()
        Log.d("HomeScreen", "Clinician ID: $clinicianId")

        setContent {
            YangXuan_33520496Theme {

                val context = LocalContext.current

                // Obtain DAO instance from your Room database
                val dao = UserDatabase.getDatabase(context).motivationalTipDao()

                // Build ViewModel factory
                val genAIViewModelFactory = GenAIViewModel.GenAIViewModelFactory(
                    dao = dao,
                    clinicianId = clinicianId
                )

                // Obtain GenAIViewModel using factory
                val genAIViewModel: GenAIViewModel = viewModel(factory = genAIViewModelFactory)

                // Other view models
                val viewModel: PatientViewModel = ViewModelProvider(
                    this@HomeScreen,
                    PatientViewModel.LoginViewModelFactory(this@HomeScreen)
                )[PatientViewModel::class.java]

                // Load once on first composition
                LaunchedEffect(clinicianId) {
                    viewModel.loadGenderScores(clinicianId)
                }

                val navController: NavHostController = rememberNavController()
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
                            viewModel,
                            genAIViewModel
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
fun InsightsScreen(innerPadding: PaddingValues, clinicianId: String, navControler: NavHostController, viewModel: PatientViewModel) {

    val context = LocalContext.current
    val genderScores by viewModel.genderScores.collectAsState()
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Insights: Food Score",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(15.dp))
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))


        insightsRow("Vegetables", genderScores!!.vegetablesHeifaScore, 10)
        insightsRow("Fruits", genderScores!!.fruitHeifaScore, 10)
        insightsRow("Grains & Cereals", genderScores!!.grainsAndCerealsHeifaScore, 5)
        insightsRow("Whole Grains", genderScores!!.wholegrainsHeifaScore, 5)
        insightsRow("Meat & Alternatives", genderScores!!.meatAndAlternativesHeifaScore, 10)
        insightsRow("Dairy", genderScores!!.dairyAndAlternativesHeifaScore, 10)
        insightsRow("Water", genderScores!!.waterHeifaScore, 5)
        insightsRow("Unsaturated Fats", genderScores!!.unsaturatedFatHeifaScore, 5)
        insightsRow("Saturated Fats", genderScores!!.saturatedFatHeifaScore, 5)
        insightsRow("Sodium", genderScores!!.sodiumHeifaScore, 10)
        insightsRow("Sugar", genderScores!!.sugarHeifaScore, 10)
        insightsRow("Alcohol", genderScores!!.alcoholHeifaScore, 5)
        insightsRow("Discretionary Foods", genderScores!!.discretionaryHeifaScore, 10)


        Spacer(Modifier.height(35.dp))
        Text("Total Food Quality Score", fontSize = 20.sp, fontWeight = FontWeight.Bold, 
            modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            LinearProgressIndicator(
                progress = { (genderScores!!.heifaTotalScore.toFloat() / 100).coerceIn(0f, 1f) },
                modifier = Modifier
                    .width(300.dp)
                    .height(15.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .align(Alignment.CenterVertically),
                color = Color(0xFF6200EE)
            )
            Text(
                text = "${genderScores!!.heifaTotalScore}/100",
                modifier = Modifier.padding(start = 8.dp),
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "My Food Quality Score: ${genderScores!!.heifaTotalScore}")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            },
            modifier = Modifier
                .fillMaxWidth()
                .width(250.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Share with someone", color = Color.White)
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                navControler.navigate("nutricoach")
            },
            modifier = Modifier
                .fillMaxWidth()
                .width(250.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Icon(Icons.Filled.EmojiObjects, contentDescription = "Improve", tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Improve my diet!", color = Color.White)
        }
    }
}



@Composable
fun HomeScreen(innerPadding: PaddingValues, clinicianId: String,  navControler: NavHostController, viewModel: PatientViewModel){
    LaunchedEffect(Unit) {
        viewModel.loadPatient(clinicianId)
    }
    val patientLoaded by viewModel.patientLoaded
    val localContext = LocalContext.current
    val genderScores by viewModel.genderScores.collectAsState(initial = null)
    val score = genderScores?.heifaTotalScore
    var selectedItem by remember { mutableStateOf(0) }
    val name by viewModel.name

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        if (!patientLoaded) {
            CircularProgressIndicator()
        } else {
            Text("Hello,", color = Color.Gray, fontSize = 14.sp)
            Text(name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f) // Pushes the button to the right
                )
                Button(
                    onClick = {
                        val intent = Intent(localContext, FoodIntakeQuestionnaire::class.java)
                        intent.putExtra("clinicianId", clinicianId)
                        localContext.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", color = Color.White)
                }
            }
            Image(
                painter = painterResource(id = R.drawable.food_quality_image),
                contentDescription = "Food Quality Score Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                // Header Row (Title + "See all scores" Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Score",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "See all scores >",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            navControler.navigate("insights")
                            selectedItem = 1
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Row (Arrow Icon + Text + Score)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Arrow Icon
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Increase",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text
                    Text(
                        text = "Your Food Quality score",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier.weight(1f) // Takes up remaining space
                    )

                    // Score (Green color)
                    Text(
                        text = score.let { "$it/100" } ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF148A1D) // Green color
                    )
                }
            }

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text("What is the Food Quality Score?", fontSize = 20.sp , fontWeight = FontWeight.Bold)
            Text(
                "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n\n" +
                        "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp)
            )
        }
        }

}



@Composable
fun insightsRow(title: String, score: Double, maxScore: Int) {
    Spacer(modifier = Modifier.height(5.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Text(
            text = title,
            modifier = Modifier
                .weight(2f)
                .padding(end = 3.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(40.dp))
        LinearProgressIndicator(
            progress = { (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .width(180.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .padding(end = 3.dp)
                .align(Alignment.CenterVertically),
            color = Color(0xFF6200EE)
        )
        Text(
            text = "$score/$maxScore",
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            fontSize = 12.sp
        )
    }
}





