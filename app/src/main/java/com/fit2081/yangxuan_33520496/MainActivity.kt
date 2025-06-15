package com.fit2081.yangxuan_33520496

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fit2081.yangxuan_33520496.data.AuthManager
import com.fit2081.yangxuan_33520496.ui.theme.Purple500
import com.fit2081.yangxuan_33520496.ui.theme.YangXuan_33520496Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthManager.init(applicationContext)

        val clinicianId = AuthManager.getPatientId()
        val isFirstLogin = AuthManager.isFirstLogin()

        when {
            // Case 1: Not logged in → show Welcome/Login screen
            clinicianId == null -> {
                setContent {
                    YangXuan_33520496Theme {
                        WelcomeScreen(context = this)
                    }
                }
            }

            // Case 2: Logged in for first time → force questionnaire
            isFirstLogin -> {
                AuthManager.setFirstLoginDone() // prevent repeat
                val intent = Intent(this, FoodIntakeQuestionnaire::class.java)
                startActivity(intent)
                finish()
            }

            // Case 3: Already logged in and done questionnaire → Home screen
            else -> {
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}


@Composable
fun WelcomeScreen(context: Context) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "NutriTrack",
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        // Logo Image
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with actual drawable
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        // Disclaimer Text
        Text(
            text = "This app provides general health and nutrition information for " +
                    "educational purposes only. It is not intended as medical advice, " +
                    "diagnosis, or treatment. Always consult a qualified healthcare " +
                    "professional before making any changes to your diet, exercise, or " +
                    "health regimen.\n" +
                    "Use this app at your own risk.\n" +
                    "If you’d like to an Accredited Practicing Dietitian (APD),\n" +
                    "please visit the Monash Nutrition/Dietetics Clinic \n" +
                    "(discounted rates for students):\n",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )

        ClickableText(
            text = AnnotatedString("Monash Nutrition/Dietetics Clinic"),
            style = TextStyle(color = Color.Blue, fontSize = 15.sp),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"))
                context.startActivity(intent)
            }
        )

        // Login Button
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple500),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Login", fontSize = 18.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(80.dp))
        Text(text = "Designed by Chew Yang Xuan (33520496)", fontSize = 12.sp, color = Color.Gray)

    }
}

//@Composable
//fun MyApp() {
//    val userId = AuthManager._userId.value
//    val context = LocalContext.current
//
//    LaunchedEffect(userId) {
//        if (userId == null) {
//            Log.d("Errorcheck", "userId is null, going to Login")
//            val intent = Intent(context, LoginActivity::class.java)
//            context.startActivity(intent)
//        } else {
//            Log.d("Errorcheck", "userId found: $userId, going to HomeScreen")
//            val intent = Intent(context, HomeScreen::class.java)
//            context.startActivity(intent)
//        }
//    }
//}
//
//


