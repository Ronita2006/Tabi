/*package com.example.tabi

import androidx.compose.material3.Text
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TabiTheme {
                TabiApp()
            }
        }
    }
}


// =========================
// SCREEN NAVIGATION
// =========================

enum class Screen {
    SPLASH,
    LOGIN,
    REGISTER,
    DASHBOARD,
    TRIP_RECOMMENDER,
    TRIP_DETAILS
}


// =========================
// MAIN APP
// =========================

@Composable
fun TabiApp() {

    var currentScreen by remember {
        mutableStateOf(Screen.SPLASH)
    }

    var currentUserEmail by remember {
        mutableStateOf("")
    }

    var selectedTrip by remember {
        mutableStateOf<RemoteTripPackage?>(null)
    }

    val context = LocalContext.current

    val userPrefs = remember {
        context.getSharedPreferences(
            "tabi_user",
            Context.MODE_PRIVATE
        )
    }

    LaunchedEffect(Unit) {
        delay(2500)
        currentScreen = Screen.LOGIN
    }

    AnimatedContent(
        targetState = currentScreen,
        label = "screen_transition"
    ) { screen ->

        when (screen) {

            // =========================
            // SPLASH
            // =========================

            Screen.SPLASH -> {
                SplashScreen()
            }


            // =========================
            // LOGIN
            // =========================

            Screen.LOGIN -> {

                LoginScreen(

                    onCreateAccount = {
                        currentScreen = Screen.REGISTER
                    },

                    onLoginSuccess = { loggedInEmail ->

                        currentUserEmail =
                            loggedInEmail

                        currentScreen =
                            Screen.DASHBOARD
                    }
                )
            }


            // =========================
            // REGISTER
            // =========================

            Screen.REGISTER -> {

                RegisterScreen(

                    onBackToLogin = {
                        currentScreen =
                            Screen.LOGIN
                    }
                )
            }


            // =========================
            // DASHBOARD
            // =========================

            Screen.DASHBOARD -> {

                val savedUserName =
                    userPrefs.getString(
                        "user_name_${currentUserEmail.trim().lowercase()}",
                        null
                    )

                val userName =
                    savedUserName
                        ?.takeIf { it.isNotBlank() }
                        ?: "there"

                DashboardScreen(

                    userName = userName,

                    userEmail = currentUserEmail,

                    onExplore = {
                        currentScreen =
                            Screen.DASHBOARD
                    },

                    onTripRecommender = {
                        currentScreen =
                            Screen.TRIP_RECOMMENDER
                    },

                    onLogout = {

                        currentUserEmail = ""

                        currentScreen =
                            Screen.LOGIN
                    }
                )
            }


            // =========================
            // TRIP RECOMMENDER
            // =========================

            Screen.TRIP_RECOMMENDER -> {

                TripPackageRecommender(

                    onBack = {
                        currentScreen =
                            Screen.DASHBOARD
                    },

                    onViewPackage = { trip ->

                        selectedTrip = trip

                        currentScreen =
                            Screen.TRIP_DETAILS
                    }
                )
            }


            // =========================
            // TRIP DETAILS
            // =========================

            Screen.TRIP_DETAILS -> {

                selectedTrip?.let { trip ->

                    TripDetailsScreen(

                        packageItem = trip,

                        onBack = {
                            currentScreen =
                                Screen.TRIP_RECOMMENDER
                        }
                    )
                }
            }
        }
    }
}


// =========================
// SPLASH SCREEN
// =========================

@Composable
fun SplashScreen() {

    var showLogo by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        showLogo = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.primary
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = showLogo,
            enter = fadeIn() + scaleIn()
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🌏",
                    fontSize = 72.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text = "TABi",
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Your journey, beautifully planned.",
                    color =
                        Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp
                )
            }
        }
    }
}


// =========================
// LOGIN SCREEN
// =========================

@Composable
fun LoginScreen(
    onCreateAccount: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var loginSuccessful by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "Welcome back 👋",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Continue your journey with Tabi.",
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )

            OutlinedTextField(
                value = email,

                onValueChange = {
                    email = it
                    message = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Email")
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Email
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = password,

                onValueChange = {
                    password = it
                    message = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Password")
                },

                singleLine = true,

                visualTransformation =
                    if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    Text(
                        text =
                            if (showPassword)
                                "Hide"
                            else
                                "Show",

                        modifier =
                            Modifier.padding(
                                end = 12.dp
                            )
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Password
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = "Forgot password?",
                modifier =
                    Modifier.align(
                        Alignment.End
                    ),
                color =
                    MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            if (message.isNotEmpty()) {

                Text(
                    text = message,

                    color =
                        if (loginSuccessful)
                            Color(0xFF2E7D32)
                        else
                            MaterialTheme.colorScheme.error,

                    fontSize = 14.sp,

                    modifier =
                        Modifier.padding(
                            bottom = 12.dp
                        )
                )
            }

            Button(

                onClick = {

                    if (
                        email.isBlank() ||
                        password.isBlank()
                    ) {

                        message =
                            "Please enter your email and password."

                        return@Button
                    }

                    isLoading = true
                    message = ""

                    ApiClient.login(

                        email = email,

                        password = password

                    ) { success, serverMessage ->

                        isLoading = false

                        if (success) {

                            loginSuccessful =
                                true

                            message =
                                "Login successful! 🎉"

                            onLoginSuccess(
                                email.trim()
                                    .lowercase()
                            )

                        } else {

                            loginSuccessful =
                                false

                            message =
                                serverMessage
                        }
                    }
                },

                enabled = !isLoading,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "LOGIN",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "Don't have an account?"
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        "Create Account",

                    color =
                        MaterialTheme.colorScheme.primary,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.padding(4.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            OutlinedButton(

                onClick =
                    onCreateAccount,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                Text(
                    "CREATE ACCOUNT"
                )
            }
        }
    }
}


// =========================
// DASHBOARD
// =========================

@Composable
fun DashboardScreen(
    userName: String,
    userEmail: String,
    onExplore: () -> Unit,
    onTripRecommender: () -> Unit,
    onLogout: () -> Unit
) {

    var search by remember {
        mutableStateOf("")
    }

    val destinations = listOf(
        "Jaipur",
        "Kerala",
        "Goa",
        "Kashmir",
        "Ooty",
        "Manali"
    )

    val filtered =
        destinations.filter {
            it.contains(
                search,
                ignoreCase = true
            )
        }

    Surface(
        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            Text(
                text =
                    "Welcome to TABi, $userName!",

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text =
                    "Discover India, your way.",

                fontSize = 16.sp,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                Modifier.height(20.dp)
            )

            OutlinedTextField(

                value = search,

                onValueChange = {
                    search = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text(
                        "Search places, cities, experiences..."
                    )
                }
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTripRecommender()
                        },

                shape =
                    RoundedCornerShape(20.dp)

            ) {

                Column(
                    Modifier.padding(20.dp)
                ) {

                    Text(
                        "🧳 PLAN YOUR TRIP",

                        fontWeight =
                            FontWeight.Bold,

                        fontSize = 19.sp
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        "Get a personalized India trip package based on your budget, duration and interests."
                    )

                    Spacer(
                        Modifier.height(14.dp)
                    )

                    Button(

                        onClick =
                            onTripRecommender,

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(
                            "TRIP PACKAGE RECOMMENDER"
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                "Popular Destinations",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )

            ) {

                filtered.forEach {
                        destination ->

                    AssistChip(

                        onClick =
                            onExplore,

                        label = {
                            Text(destination)
                        },

                        modifier =
                            Modifier.padding(
                                end = 8.dp
                            )
                    )
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                "Explore India",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )

            ) {

                listOf(
                    "North",
                    "South",
                    "East",
                    "West",
                    "Northeast"
                ).forEach {

                    FilterChip(

                        selected = false,

                        onClick =
                            onExplore,

                        label = {
                            Text(it)
                        },

                        modifier =
                            Modifier.padding(
                                end = 8.dp
                            )
                    )
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                "Experiences",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(
                Modifier.fillMaxWidth()
            ) {

                ExperienceCard(
                    "🍛",
                    "Food & Culture",
                    Modifier.weight(1f)
                )

                Spacer(
                    Modifier.width(10.dp)
                )

                ExperienceCard(
                    "🎉",
                    "Events & Festivals",
                    Modifier.weight(1f)
                )
            }

            Spacer(
                Modifier.height(10.dp)
            )

            Row(
                Modifier.fillMaxWidth()
            ) {

                ExperienceCard(
                    "🏨",
                    "Hotels & Stays",
                    Modifier.weight(1f)
                )

                Spacer(
                    Modifier.width(10.dp)
                )

                ExperienceCard(
                    "🗺️",
                    "Explore",
                    Modifier.weight(1f),
                    onExplore
                )
            }

            Spacer(
                Modifier.height(24.dp)
            )

            OutlinedButton(

                onClick =
                    onLogout,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text("LOGOUT")
            }

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }
}


// =========================
// EXPERIENCE CARD
// =========================

@Composable
private fun ExperienceCard(
    icon: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {

    Card(

        modifier =
            modifier.then(

                if (onClick != null)
                    Modifier.clickable {
                        onClick()
                    }
                else
                    Modifier
            ),

        shape =
            RoundedCornerShape(16.dp)

    ) {

        Column(

            modifier =
                Modifier.padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(
                icon,
                fontSize = 28.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                title,
                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}


// =========================
// TRIP PACKAGE RECOMMENDER
// =========================

@Composable
fun TripPackageRecommender(
    onBack: () -> Unit,
    onViewPackage: (RemoteTripPackage) -> Unit
) {

    var daysText by remember {
        mutableStateOf("5")
    }

    var budget by remember {
        mutableStateOf("")
    }

    var travelersText by remember {
        mutableStateOf("2")
    }

    var interest by remember {
        mutableStateOf("Relaxation")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var recommendations by remember {
        mutableStateOf<List<RemoteTripPackage>>(
            emptyList()
        )
    }

    val days =
        daysText.toIntOrNull()

    val travelers =
        travelersText.toIntOrNull()

    val budgetValue =
        budget.toIntOrNull()

    val daysError =
        days != null &&
                days !in 1..30

    val travelersError =
        travelers != null &&
                travelers !in 1..20

    val canRecommend =
        days != null &&
                days in 1..30 &&
                travelers != null &&
                travelers in 1..20 &&
                (
                        budget.isBlank() ||
                                budgetValue != null
                        )

    fun findTrips() {

        if (!canRecommend)
            return

        isLoading = true
        errorMessage = ""
        recommendations = emptyList()

        TripRecommendationApi.recommendTrips(

            days = days!!,

            travelers = travelers!!,

            budgetPerTraveler =
                budgetValue,

            style = interest

        ) { result ->

            isLoading = false

            result
                .onSuccess { packages ->

                    recommendations =
                        packages
                }

                .onFailure { error ->

                    errorMessage =
                        error.message
                            ?: "Unable to get recommendations."
                }
        }
    }

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            // BACK BUTTON

            Box(

                modifier =
                    Modifier
                        .size(44.dp)
                        .clickable {
                            onBack()
                        },

                contentAlignment =
                    Alignment.Center

            ) {

                Text(
                    text = "‹",

                    fontSize = 38.sp,

                    fontWeight =
                        FontWeight.Light
                )
            }

            Spacer(
                Modifier.height(6.dp)
            )

            Text(

                "🧳 Trip Package Recommender",

                fontSize = 27.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(

                "Tell TABi what kind of trip you want.",

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                Modifier.height(22.dp)
            )

            // DAYS

            Text(

                "Trip duration",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            OutlinedTextField(

                value = daysText,

                onValueChange = {

                    daysText =
                        it.filter(
                            Char::isDigit
                        ).take(2)

                    recommendations =
                        emptyList()

                    errorMessage = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Number of days")
                },

                supportingText = {
                    Text(
                        "Enter a value from 1 to 30 days"
                    )
                },

                isError =
                    daysError,

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    )
            )

            if (daysError) {

                Text(

                    "Please enter between 1 and 30 days.",

                    color =
                        MaterialTheme.colorScheme.error,

                    fontSize = 12.sp
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // TRAVELERS

            Text(

                "Travelers",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            OutlinedTextField(

                value =
                    travelersText,

                onValueChange = {

                    travelersText =
                        it.filter(
                            Char::isDigit
                        ).take(2)

                    recommendations =
                        emptyList()

                    errorMessage = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Number of travelers")
                },

                supportingText = {
                    Text(
                        "Enter a value from 1 to 20 travelers"
                    )
                },

                isError =
                    travelersError,

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    )
            )

            if (travelersError) {

                Text(

                    "Please enter between 1 and 20 travelers.",

                    color =
                        MaterialTheme.colorScheme.error,

                    fontSize = 12.sp
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // BUDGET

            OutlinedTextField(

                value =
                    budget,

                onValueChange = {

                    budget =
                        it.filter(
                            Char::isDigit
                        )

                    recommendations =
                        emptyList()

                    errorMessage = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text(
                        "Maximum budget per traveler (₹)"
                    )
                },

                supportingText = {
                    Text("Optional")
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    )
            )

            Spacer(
                Modifier.height(22.dp)
            )

            // TRAVEL STYLE

            Text(

                "What kind of trip do you want?",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )

            ) {

                val styles =
                    listOf(

                        "Adventure" to "🏔️",

                        "Relaxation" to "🌴",

                        "Family" to "👨‍👩‍👧",

                        "Cultural" to "🏛️",

                        "Spiritual" to "🛕",

                        "Food" to "🍛"
                    )

                styles.forEach {
                        (style, icon) ->

                    Card(

                        modifier =
                            Modifier
                                .width(112.dp)
                                .height(105.dp)
                                .padding(end = 8.dp)
                                .clickable {

                                    interest =
                                        style

                                    recommendations =
                                        emptyList()

                                    errorMessage =
                                        ""
                                },

                        shape =
                            RoundedCornerShape(16.dp),

                        border =
                            if (
                                interest == style
                            ) {

                                BorderStroke(
                                    2.dp,
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                )

                            } else {
                                null
                            }

                    ) {

                        Column(

                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            verticalArrangement =
                                Arrangement.Center

                        ) {

                            Text(
                                icon,
                                fontSize = 28.sp
                            )

                            Spacer(
                                Modifier.height(5.dp)
                            )

                            Text(

                                style,

                                fontSize = 13.sp,

                                fontWeight =
                                    if (
                                        interest ==
                                        style
                                    )
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(22.dp)
            )

            // FIND MY TRIP

            Button(

                onClick = {
                    findTrips()
                },

                enabled =
                    canRecommend &&
                            !isLoading,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                if (isLoading) {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.height(22.dp),

                        color =
                            Color.White,

                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        "FIND MY TRIP"
                    )
                }
            }

            // ERROR

            if (
                errorMessage.isNotBlank()
            ) {

                Spacer(
                    Modifier.height(14.dp)
                )

                Text(

                    errorMessage,

                    color =
                        MaterialTheme
                            .colorScheme
                            .error,

                    fontSize = 14.sp
                )
            }

            // RECOMMENDATIONS

            if (
                !isLoading &&
                recommendations.isNotEmpty()
            ) {

                Spacer(
                    Modifier.height(24.dp)
                )

                Text(

                    "Recommended for you",

                    fontSize = 21.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                recommendations.forEach {
                        packageItem ->

                    RemoteTripPackageCard(

                        packageItem =
                            packageItem,

                        travelers =
                            travelers!!,

                        onViewPackage = {

                            onViewPackage(
                                packageItem
                            )
                        }
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }
}


// =========================
// TRIP PACKAGE CARD
// =========================

@Composable
private fun RemoteTripPackageCard(

    packageItem:
    RemoteTripPackage,

    travelers: Int,

    onViewPackage: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp)

    ) {

        Column(
            Modifier.padding(18.dp)
        ) {

            Text(

                packageItem.title,

                fontSize = 19.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                packageItem.route
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                "${packageItem.days} days • ⭐ ${packageItem.rating}"
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(

                "₹${packageItem.price} / person",

                fontWeight =
                    FontWeight.Bold
            )

            Text(

                "Approx. ₹${packageItem.price * travelers} for $travelers travelers"
            )

            if (
                packageItem.highlights.isNotEmpty()
            ) {

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(

                    "Highlights",

                    fontWeight =
                        FontWeight.SemiBold
                )

                packageItem
                    .highlights
                    .take(3)
                    .forEach {

                        Text(
                            "• $it",
                            fontSize = 13.sp
                        )
                    }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            // NOW WORKS

            Button(

                onClick =
                    onViewPackage,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "VIEW PACKAGE"
                )
            }
        }
    }
}


// =========================
// TRIP DETAILS SCREEN
// =========================

@Composable
fun TripDetailsScreen(

    packageItem:
    RemoteTripPackage,

    onBack: () -> Unit

) {

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme
                .colorScheme
                .background

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            // BACK BUTTON

            Box(

                modifier =
                    Modifier
                        .size(44.dp)
                        .clickable {
                            onBack()
                        },

                contentAlignment =
                    Alignment.Center

            ) {

                Text(

                    text = "‹",

                    fontSize = 38.sp,

                    fontWeight =
                        FontWeight.Light
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            // TITLE

            Text(

                text =
                    packageItem.title,

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(

                text =
                    packageItem.route,

                fontSize = 15.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                Modifier.height(18.dp)
            )

            // OVERVIEW

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)

                ) {

                    Text(

                        text =
                            "Trip Overview",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    Text(
                        "📅 ${packageItem.days} days"
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    Text(
                        "⭐ ${packageItem.rating} rating"
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    Text(
                        "🎯 ${packageItem.style}"
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    Text(

                        "💰 ₹${packageItem.price} per traveler",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )

            // HIGHLIGHTS

            Text(

                text =
                    "✨ Highlights",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            packageItem
                .highlights
                .forEach { highlight ->

                    Card(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = 8.dp
                                ),

                        shape =
                            RoundedCornerShape(14.dp)

                    ) {

                        Text(

                            text =
                                "• $highlight",

                            modifier =
                                Modifier.padding(
                                    15.dp
                                ),

                            fontSize = 15.sp
                        )
                    }
                }

            Spacer(
                Modifier.height(18.dp)
            )

            // ITINERARY

            Text(

                text =
                    "📅 Suggested Itinerary",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            for (
            day in
            1..packageItem.days
            ) {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 10.dp
                            ),

                    shape =
                        RoundedCornerShape(16.dp)

                ) {

                    Column(

                        modifier =
                            Modifier.padding(16.dp)

                    ) {

                        Text(

                            text =
                                "Day $day",

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            Modifier.height(6.dp)
                        )

                        Text(

                            text =
                                when (day) {

                                    1 ->
                                        "Arrival and explore the main attractions."

                                    2 ->
                                        "Explore popular local attractions and experiences."

                                    3 ->
                                        "Discover nearby destinations and local culture."

                                    4 ->
                                        "Enjoy local food, shopping and sightseeing."

                                    5 ->
                                        "Relax, explore remaining attractions and enjoy the destination."

                                    else ->
                                        "Explore more of the destination and enjoy local experiences."
                                },

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            // FOOD

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)

                ) {

                    Text(

                        text =
                            "🍛 Food to Try",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "Try authentic local cuisine, regional specialties, street food and traditional dishes during your trip."
                    )
                }
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // ACTIVITIES

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)

                ) {

                    Text(

                        text =
                            "🎯 Activities",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "Sightseeing • Local experiences • Photography • Shopping • Food exploration"
                    )
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Button(

                onClick =
                    onBack,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                Text(
                    "BACK TO RECOMMENDATIONS"
                )
            }

            Spacer(
                Modifier.height(20.dp)
            )
        }
    }
}


// =========================
// DASHBOARD FEATURE CARD
// =========================

@Composable
fun DashboardFeatureCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {

    Surface(

        modifier =
            modifier
                .height(90.dp)
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        color =
            if (selected)
                MaterialTheme
                    .colorScheme
                    .primaryContainer
            else
                MaterialTheme
                    .colorScheme
                    .surfaceVariant

    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center

        ) {

            Text(
                text = emoji,
                fontSize = 28.sp
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(

                text = title,

                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}


// =========================
// DESTINATION CARD
// =========================

@Composable
fun DestinationCard(
    name: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        color =
            if (selected)
                MaterialTheme
                    .colorScheme
                    .primaryContainer
            else
                MaterialTheme
                    .colorScheme
                    .surfaceVariant

    ) {

        Row(

            modifier =
                Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Text(
                text = emoji,
                fontSize = 34.sp
            )

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column {

                Text(

                    text = name,

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "Explore places, attractions and experiences",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontSize = 13.sp
                )
            }
        }
    }
}


// =========================
// DASHBOARD INFO CARD
// =========================

@Composable
fun DashboardInfoCard(
    emoji: String,
    title: String,
    description: String
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            MaterialTheme
                .colorScheme
                .primaryContainer

    ) {

        Column(

            modifier =
                Modifier.padding(20.dp)

        ) {

            Text(
                text = emoji,
                fontSize = 40.sp
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(

                text = title,

                fontSize = 22.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text = description,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                lineHeight = 20.sp
            )
        }
    }
}


// =========================
// DASHBOARD NAV BUTTON
// =========================

@Composable
fun DashboardNavButton(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {

    Column(

        modifier =
            Modifier.clickable(
                onClick = onClick
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Text(
            text = emoji,
            fontSize = 22.sp
        )

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(

            text = title,

            fontSize = 12.sp
        )
    }
}


// =========================
// REGISTER SCREEN
// =========================

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit
) {

    val context =
        LocalContext.current

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var registrationSuccessful by remember {
        mutableStateOf(false)
    }

    val hasEightCharacters =
        password.length >= 8

    val hasUppercase =
        password.any {
            it.isUpperCase()
        }

    val hasLowercase =
        password.any {
            it.isLowerCase()
        }

    val hasNumber =
        password.any {
            it.isDigit()
        }

    val hasSpecial =
        password.any {
            !it.isLetterOrDigit()
        }

    val passwordValid =
        hasEightCharacters &&
                hasUppercase &&
                hasLowercase &&
                hasNumber &&
                hasSpecial

    val passwordsMatch =
        password.isNotEmpty() &&
                password == confirmPassword

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(24.dp)

    ) {

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        Text(

            text =
                "Create your account ✨",

            fontSize = 30.sp,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(

            text =
                "Let's start planning your next adventure.",

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        OutlinedTextField(

            value = name,

            onValueChange = {
                name = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Full Name")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Email")
            },

            singleLine = true,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email
                )
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Password")
            },

            singleLine = true,

            visualTransformation =
                if (showPassword)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {

                Text(

                    text =
                        if (showPassword)
                            "Hide"
                        else
                            "Show",

                    modifier =
                        Modifier.padding(
                            end = 12.dp
                        )
                )
            }
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(

            text =
                "Password requirements",

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        PasswordRequirement(
            "At least 8 characters",
            hasEightCharacters
        )

        PasswordRequirement(
            "One uppercase letter",
            hasUppercase
        )

        PasswordRequirement(
            "One lowercase letter",
            hasLowercase
        )

        PasswordRequirement(
            "One number",
            hasNumber
        )

        PasswordRequirement(
            "One special character",
            hasSpecial
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        OutlinedTextField(

            value =
                confirmPassword,

            onValueChange = {
                confirmPassword = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Confirm Password")
            },

            singleLine = true,

            visualTransformation =
                PasswordVisualTransformation()
        )

        if (
            confirmPassword.isNotEmpty()
        ) {

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(

                text =
                    if (passwordsMatch)
                        "✓ Passwords match"
                    else
                        "Passwords do not match",

                color =
                    if (passwordsMatch)
                        Color(0xFF2E7D32)
                    else
                        MaterialTheme
                            .colorScheme
                            .error,

                fontSize = 14.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        if (
            message.isNotEmpty()
        ) {

            Text(

                text = message,

                color =
                    if (
                        registrationSuccessful
                    )
                        Color(0xFF2E7D32)
                    else
                        MaterialTheme
                            .colorScheme
                            .error,

                fontSize = 14.sp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }

        Button(

            onClick = {

                if (name.isBlank()) {

                    message =
                        "Please enter your name."

                    return@Button
                }

                if (email.isBlank()) {

                    message =
                        "Please enter your email."

                    return@Button
                }

                if (!passwordValid) {

                    message =
                        "Please satisfy all password requirements."

                    return@Button
                }

                if (!passwordsMatch) {

                    message =
                        "Passwords do not match."

                    return@Button
                }

                isLoading = true
                message = ""

                ApiClient.register(

                    name = name,

                    email = email,

                    password = password

                ) { success, serverMessage ->

                    isLoading = false

                    if (success) {

                        registrationSuccessful =
                            true

                        message =
                            "Account created successfully! 🎉"

                        context
                            .getSharedPreferences(
                                "tabi_user",
                                Context.MODE_PRIVATE
                            )
                            .edit()
                            .putString(

                                "user_name_${
                                    email.trim()
                                        .lowercase()
                                }",

                                name.trim()
                            )
                            .apply()

                    } else {

                        registrationSuccessful =
                            false

                        message =
                            serverMessage
                    }
                }
            },

            enabled =
                !isLoading,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(54.dp),

            shape =
                RoundedCornerShape(14.dp)

        ) {

            if (isLoading) {

                CircularProgressIndicator(

                    modifier =
                        Modifier.height(22.dp),

                    color =
                        Color.White,

                    strokeWidth = 2.dp
                )

            } else {

                Text(

                    text =
                        "CREATE ACCOUNT",

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutlinedButton(

            onClick =
                onBackToLogin,

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(14.dp)

        ) {

            Text(
                "BACK TO LOGIN"
            )
        }

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}


// =========================
// PASSWORD REQUIREMENT
// =========================

@Composable
fun PasswordRequirement(
    text: String,
    satisfied: Boolean
) {

    Text(

        text =
            if (satisfied)
                "✓ $text"
            else
                "○ $text",

        color =
            if (satisfied)
                Color(0xFF2E7D32)
            else
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,

        fontSize = 14.sp,

        modifier =
            Modifier.padding(
                vertical = 2.dp
            )
    )
}


// =========================
// TABi THEME
// =========================

@Composable
fun TabiTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        content = content
    )
}*/

package com.example.tabi

import androidx.compose.material3.Text
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TabiTheme {
                TabiApp()
            }
        }
    }
}


// =========================
// SCREEN NAVIGATION
// =========================

enum class Screen {
    SPLASH,
    LOGIN,
    REGISTER,
    DASHBOARD,
    TRIP_RECOMMENDER,
    TRIP_DETAILS,
    DESTINATION_DETAILS,
    EXPERIENCE_DETAILS
}


// =========================
// DASHBOARD DATA MODELS
// =========================

data class Destination(
    val name: String,
    val region: String,
    val emoji: String,
    val tagline: String,
    val description: String,
    val highlights: List<String>
)

data class ExperienceCategory(
    val icon: String,
    val title: String,
    val description: String,
    val items: List<String>
)

val allDestinations = listOf(
    Destination(
        name = "Jaipur",
        region = "North",
        emoji = "🏰",
        tagline = "The Pink City",
        description = "A city of palaces, forts and bustling bazaars, Jaipur blends royal Rajasthani heritage with vibrant street life.",
        highlights = listOf("Amber Fort", "City Palace", "Hawa Mahal", "Local bazaars for block-print textiles")
    ),
    Destination(
        name = "Kerala",
        region = "South",
        emoji = "🌴",
        tagline = "God's Own Country",
        description = "Backwaters, houseboats and lush greenery make Kerala one of India's most relaxing getaways.",
        highlights = listOf("Alleppey houseboat cruise", "Munnar tea gardens", "Ayurvedic spa treatments", "Kathakali performances")
    ),
    Destination(
        name = "Goa",
        region = "West",
        emoji = "🏖️",
        tagline = "Beaches & Nightlife",
        description = "Golden beaches, Portuguese-era architecture and a laid-back coastal vibe make Goa India's favourite beach escape.",
        highlights = listOf("Calangute & Palolem beaches", "Old Goa churches", "Beach shacks & seafood", "Sunset cruises")
    ),
    Destination(
        name = "Kashmir",
        region = "North",
        emoji = "🏔️",
        tagline = "Paradise on Earth",
        description = "Snow-capped mountains, alpine lakes and houseboats on Dal Lake make Kashmir a breathtaking Himalayan retreat.",
        highlights = listOf("Dal Lake shikara ride", "Gulmarg gondola", "Mughal gardens", "Pahalgam valley")
    ),
    Destination(
        name = "Ooty",
        region = "South",
        emoji = "🍃",
        tagline = "Queen of Hill Stations",
        description = "Rolling tea estates, cool weather and colonial charm define this Nilgiri hill town.",
        highlights = listOf("Nilgiri toy train", "Botanical gardens", "Tea factory tours", "Doddabetta viewpoint")
    ),
    Destination(
        name = "Manali",
        region = "North",
        emoji = "⛰️",
        tagline = "Adventure Capital",
        description = "From paragliding to river rafting, Manali is the Himalayas' hub for adrenaline and mountain scenery.",
        highlights = listOf("Solang Valley paragliding", "Rohtang Pass", "River rafting", "Old Manali cafes")
    ),
    Destination(
        name = "Kolkata",
        region = "East",
        emoji = "🎭",
        tagline = "City of Joy",
        description = "A city of art, literature and colonial history, Kolkata offers rich culture and legendary street food.",
        highlights = listOf("Victoria Memorial", "Howrah Bridge", "College Street bookshops", "Street food trails")
    ),
    Destination(
        name = "Shillong",
        region = "Northeast",
        emoji = "🌧️",
        tagline = "Scotland of the East",
        description = "Misty hills, waterfalls and a lively music scene make Shillong the gateway to the Northeast.",
        highlights = listOf("Living root bridges", "Elephant Falls", "Umiam Lake", "Local music cafes")
    )
)

val experienceCategories = listOf(
    ExperienceCategory(
        icon = "🍛",
        title = "Food & Culture",
        description = "Dive into India's regional cuisines, cooking traditions and cultural heritage.",
        items = listOf("Street food walking tours", "Home-cooked thali experiences", "Cooking classes with local chefs", "Heritage & museum walks")
    ),
    ExperienceCategory(
        icon = "🎉",
        title = "Events & Festivals",
        description = "Time your trip around India's colourful festivals and local celebrations.",
        items = listOf("Holi & Diwali celebrations", "Local temple festivals", "Music & folk art festivals", "Harvest festival tours")
    ),
    ExperienceCategory(
        icon = "🏨",
        title = "Hotels & Stays",
        description = "From heritage havelis to beach resorts, find a stay that fits your trip.",
        items = listOf("Heritage palace hotels", "Beachfront resorts", "Houseboats & homestays", "Boutique hill retreats")
    ),
    ExperienceCategory(
        icon = "🗺️",
        title = "Explore",
        description = "Get personalized recommendations across all of India based on your travel style.",
        items = listOf("Adventure trails", "Relaxation getaways", "Family-friendly circuits", "Off-the-beaten-path routes")
    )
)


// =========================
// SEARCH RESULT MODEL
// =========================

sealed class SearchResult {
    data class DestinationResult(val destination: Destination) : SearchResult()
    data class ExperienceResult(val experience: ExperienceCategory) : SearchResult()
}

fun searchDashboard(query: String): List<SearchResult> {

    if (query.isBlank()) return emptyList()

    val destinationMatches = allDestinations.filter {
        it.name.contains(query, ignoreCase = true) ||
                it.region.contains(query, ignoreCase = true) ||
                it.tagline.contains(query, ignoreCase = true)
    }.map { SearchResult.DestinationResult(it) }

    val experienceMatches = experienceCategories.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.items.any { item -> item.contains(query, ignoreCase = true) }
    }.map { SearchResult.ExperienceResult(it) }

    return destinationMatches + experienceMatches
}


// =========================
// MAIN APP
// =========================

@Composable
fun TabiApp() {

    var currentScreen by remember {
        mutableStateOf(Screen.SPLASH)
    }

    var currentUserEmail by remember {
        mutableStateOf("")
    }

    var selectedTrip by remember {
        mutableStateOf<RemoteTripPackage?>(null)
    }

    var selectedDestination by remember {
        mutableStateOf<Destination?>(null)
    }

    var selectedExperience by remember {
        mutableStateOf<ExperienceCategory?>(null)
    }

    val context = LocalContext.current

    val userPrefs = remember {
        context.getSharedPreferences(
            "tabi_user",
            Context.MODE_PRIVATE
        )
    }

    LaunchedEffect(Unit) {
        delay(2500)
        currentScreen = Screen.LOGIN
    }

    AnimatedContent(
        targetState = currentScreen,
        label = "screen_transition"
    ) { screen ->

        when (screen) {

            // =========================
            // SPLASH
            // =========================

            Screen.SPLASH -> {
                SplashScreen()
            }


            // =========================
            // LOGIN
            // =========================

            Screen.LOGIN -> {

                LoginScreen(

                    onCreateAccount = {
                        currentScreen = Screen.REGISTER
                    },

                    onLoginSuccess = { loggedInEmail ->

                        currentUserEmail =
                            loggedInEmail

                        currentScreen =
                            Screen.DASHBOARD
                    }
                )
            }


            // =========================
            // REGISTER
            // =========================

            Screen.REGISTER -> {

                RegisterScreen(

                    onBackToLogin = {
                        currentScreen =
                            Screen.LOGIN
                    }
                )
            }


            // =========================
            // DASHBOARD
            // =========================

            Screen.DASHBOARD -> {

                val savedUserName =
                    userPrefs.getString(
                        "user_name_${currentUserEmail.trim().lowercase()}",
                        null
                    )

                val userName =
                    savedUserName
                        ?.takeIf { it.isNotBlank() }
                        ?: "there"

                DashboardScreen(

                    userName = userName,

                    userEmail = currentUserEmail,

                    onExplore = {
                        currentScreen =
                            Screen.DASHBOARD
                    },

                    onTripRecommender = {
                        currentScreen =
                            Screen.TRIP_RECOMMENDER
                    },

                    onOpenDestination = { destination ->

                        selectedDestination = destination

                        currentScreen =
                            Screen.DESTINATION_DETAILS
                    },

                    onOpenExperience = { experience ->

                        selectedExperience = experience

                        currentScreen =
                            Screen.EXPERIENCE_DETAILS
                    },

                    onLogout = {

                        currentUserEmail = ""

                        currentScreen =
                            Screen.LOGIN
                    }
                )
            }


            // =========================
            // DESTINATION DETAILS
            // =========================

            Screen.DESTINATION_DETAILS -> {

                selectedDestination?.let { destination ->

                    DestinationDetailsScreen(

                        destination = destination,

                        onBack = {
                            currentScreen =
                                Screen.DASHBOARD
                        },

                        onPlanTrip = {
                            currentScreen =
                                Screen.TRIP_RECOMMENDER
                        }
                    )
                }
            }


            // =========================
            // EXPERIENCE DETAILS
            // =========================

            Screen.EXPERIENCE_DETAILS -> {

                selectedExperience?.let { experience ->

                    ExperienceDetailsScreen(

                        experience = experience,

                        onBack = {
                            currentScreen =
                                Screen.DASHBOARD
                        }
                    )
                }
            }


            // =========================
            // TRIP RECOMMENDER
            // =========================

            Screen.TRIP_RECOMMENDER -> {

                TripPackageRecommender(

                    onBack = {
                        currentScreen =
                            Screen.DASHBOARD
                    },

                    onViewPackage = { trip ->

                        selectedTrip = trip

                        currentScreen =
                            Screen.TRIP_DETAILS
                    }
                )
            }


            // =========================
            // TRIP DETAILS
            // =========================

            Screen.TRIP_DETAILS -> {

                selectedTrip?.let { trip ->

                    TripDetailsScreen(

                        packageItem = trip,

                        onBack = {
                            currentScreen =
                                Screen.TRIP_RECOMMENDER
                        }
                    )
                }
            }
        }
    }
}


// =========================
// SPLASH SCREEN
// =========================

@Composable
fun SplashScreen() {

    var showLogo by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        showLogo = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.primary
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible = showLogo,
            enter = fadeIn() + scaleIn()
        ) {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🌏",
                    fontSize = 72.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text = "TABi",
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Your journey, beautifully planned.",
                    color =
                        Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp
                )
            }
        }
    }
}


// =========================
// LOGIN SCREEN
// =========================

@Composable
fun LoginScreen(
    onCreateAccount: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var loginSuccessful by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "Welcome back 👋",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Continue your journey with Tabi.",
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )

            OutlinedTextField(
                value = email,

                onValueChange = {
                    email = it
                    message = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Email")
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Email
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = password,

                onValueChange = {
                    password = it
                    message = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Password")
                },

                singleLine = true,

                visualTransformation =
                    if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    Text(
                        text =
                            if (showPassword)
                                "Hide"
                            else
                                "Show",

                        modifier =
                            Modifier.padding(
                                end = 12.dp
                            )
                    )
                },

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Password
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = "Forgot password?",
                modifier =
                    Modifier.align(
                        Alignment.End
                    ),
                color =
                    MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            if (message.isNotEmpty()) {

                Text(
                    text = message,

                    color =
                        if (loginSuccessful)
                            Color(0xFF2E7D32)
                        else
                            MaterialTheme.colorScheme.error,

                    fontSize = 14.sp,

                    modifier =
                        Modifier.padding(
                            bottom = 12.dp
                        )
                )
            }

            Button(

                onClick = {

                    if (
                        email.isBlank() ||
                        password.isBlank()
                    ) {

                        message =
                            "Please enter your email and password."

                        return@Button
                    }

                    isLoading = true
                    message = ""

                    ApiClient.login(

                        email = email,

                        password = password

                    ) { success, serverMessage ->

                        isLoading = false

                        if (success) {

                            loginSuccessful =
                                true

                            message =
                                "Login successful! 🎉"

                            onLoginSuccess(
                                email.trim()
                                    .lowercase()
                            )

                        } else {

                            loginSuccessful =
                                false

                            message =
                                serverMessage
                        }
                    }
                },

                enabled = !isLoading,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.height(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "LOGIN",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "Don't have an account?"
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        "Create Account",

                    color =
                        MaterialTheme.colorScheme.primary,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.padding(4.dp)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            OutlinedButton(

                onClick =
                    onCreateAccount,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                Text(
                    "CREATE ACCOUNT"
                )
            }
        }
    }
}


// =========================
// DASHBOARD
// =========================

@Composable
fun DashboardScreen(
    userName: String,
    userEmail: String,
    onExplore: () -> Unit,
    onTripRecommender: () -> Unit,
    onOpenDestination: (Destination) -> Unit,
    onOpenExperience: (ExperienceCategory) -> Unit,
    onLogout: () -> Unit
) {

    var search by remember {
        mutableStateOf("")
    }

    var selectedRegion by remember {
        mutableStateOf<String?>(null)
    }

    val searchResults =
        remember(search) {
            searchDashboard(search)
        }

    val visibleDestinations =
        remember(selectedRegion) {
            if (selectedRegion == null)
                allDestinations
            else
                allDestinations.filter {
                    it.region == selectedRegion
                }
        }

    Surface(
        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            Text(
                text =
                    "Welcome to TABi, $userName!",

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text =
                    "Discover India, your way.",

                fontSize = 16.sp,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                Modifier.height(20.dp)
            )

            OutlinedTextField(

                value = search,

                onValueChange = {
                    search = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                singleLine = true,

                label = {
                    Text(
                        "Search places, cities, experiences..."
                    )
                }
            )

            if (search.isNotBlank()) {

                Spacer(
                    Modifier.height(10.dp)
                )

                if (searchResults.isEmpty()) {

                    Text(
                        text =
                            "No matches for \"$search\"",

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 14.sp,

                        modifier =
                            Modifier.padding(
                                vertical = 8.dp
                            )
                    )

                } else {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp)

                    ) {

                        Column(
                            Modifier.padding(
                                vertical = 6.dp
                            )
                        ) {

                            searchResults.forEach { result ->

                                when (result) {

                                    is SearchResult.DestinationResult -> {

                                        SearchResultRow(
                                            emoji =
                                                result.destination.emoji,

                                            title =
                                                result.destination.name,

                                            subtitle =
                                                "${result.destination.region} · ${result.destination.tagline}",

                                            onClick = {
                                                onOpenDestination(
                                                    result.destination
                                                )
                                            }
                                        )
                                    }

                                    is SearchResult.ExperienceResult -> {

                                        SearchResultRow(
                                            emoji =
                                                result.experience.icon,

                                            title =
                                                result.experience.title,

                                            subtitle =
                                                "Experience",

                                            onClick = {
                                                onOpenExperience(
                                                    result.experience
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTripRecommender()
                        },

                shape =
                    RoundedCornerShape(20.dp)

            ) {

                Column(
                    Modifier.padding(20.dp)
                ) {

                    Text(
                        "🧳 PLAN YOUR TRIP",

                        fontWeight =
                            FontWeight.Bold,

                        fontSize = 19.sp
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    Text(
                        "Get a personalized India trip package based on your budget, duration and interests."
                    )

                    Spacer(
                        Modifier.height(14.dp)
                    )

                    Button(

                        onClick =
                            onTripRecommender,

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(
                            "TRIP PACKAGE RECOMMENDER"
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                "Explore India",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )

            ) {

                listOf(
                    "North",
                    "South",
                    "East",
                    "West",
                    "Northeast"
                ).forEach { region ->

                    FilterChip(

                        selected =
                            selectedRegion == region,

                        onClick = {

                            selectedRegion =
                                if (selectedRegion == region)
                                    null
                                else
                                    region
                        },

                        label = {
                            Text(region)
                        },

                        modifier =
                            Modifier.padding(
                                end = 8.dp
                            )
                    )
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                text =
                    if (selectedRegion == null)
                        "Popular Destinations"
                    else
                        "Destinations · $selectedRegion",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            if (visibleDestinations.isEmpty()) {

                Text(
                    text =
                        "No destinations in this region yet.",

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 14.sp
                )

            } else {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            )

                ) {

                    visibleDestinations.forEach { destination ->

                        DestinationCard(
                            destination = destination,
                            onClick = {
                                onOpenDestination(destination)
                            }
                        )

                        Spacer(
                            Modifier.width(10.dp)
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Text(
                "Experiences",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(
                Modifier.fillMaxWidth()
            ) {

                ExperienceCard(
                    icon = experienceCategories[0].icon,
                    title = experienceCategories[0].title,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onOpenExperience(experienceCategories[0])
                    }
                )

                Spacer(
                    Modifier.width(10.dp)
                )

                ExperienceCard(
                    icon = experienceCategories[1].icon,
                    title = experienceCategories[1].title,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onOpenExperience(experienceCategories[1])
                    }
                )
            }

            Spacer(
                Modifier.height(10.dp)
            )

            Row(
                Modifier.fillMaxWidth()
            ) {

                ExperienceCard(
                    icon = experienceCategories[2].icon,
                    title = experienceCategories[2].title,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onOpenExperience(experienceCategories[2])
                    }
                )

                Spacer(
                    Modifier.width(10.dp)
                )

                ExperienceCard(
                    icon = experienceCategories[3].icon,
                    title = experienceCategories[3].title,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onOpenExperience(experienceCategories[3])
                    }
                )
            }

            Spacer(
                Modifier.height(24.dp)
            )

            OutlinedButton(

                onClick =
                    onLogout,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text("LOGOUT")
            }

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }
}


// =========================
// SEARCH RESULT ROW
// =========================

@Composable
private fun SearchResultRow(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Text(
            emoji,
            fontSize = 22.sp
        )

        Spacer(
            Modifier.width(12.dp)
        )

        Column {

            Text(
                title,
                fontWeight =
                    FontWeight.SemiBold
            )

            Text(
                subtitle,
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// =========================
// DESTINATION CARD
// =========================

@Composable
private fun DestinationCard(
    destination: Destination,
    onClick: () -> Unit
) {

    Card(

        modifier =
            Modifier
                .width(160.dp)
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(18.dp)

    ) {

        Column(
            Modifier.padding(14.dp)
        ) {

            Text(
                destination.emoji,
                fontSize = 30.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                destination.name,
                fontWeight =
                    FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(
                Modifier.height(2.dp)
            )

            Text(
                destination.tagline,
                fontSize = 12.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// =========================
// EXPERIENCE CARD
// =========================

@Composable
private fun ExperienceCard(
    icon: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {

    Card(

        modifier =
            modifier.then(

                if (onClick != null)
                    Modifier.clickable {
                        onClick()
                    }
                else
                    Modifier
            ),

        shape =
            RoundedCornerShape(16.dp)

    ) {

        Column(

            modifier =
                Modifier.padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(
                icon,
                fontSize = 28.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                title,
                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}


// =========================
// DESTINATION DETAILS SCREEN
// =========================

@Composable
fun DestinationDetailsScreen(
    destination: Destination,
    onBack: () -> Unit,
    onPlanTrip: () -> Unit
) {

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            OutlinedButton(
                onClick = onBack
            ) {
                Text("← BACK")
            }

            Spacer(
                Modifier.height(20.dp)
            )

            Text(
                destination.emoji,
                fontSize = 56.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = destination.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "${destination.region} India · ${destination.tagline}",
                fontSize = 15.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                Modifier.height(16.dp)
            )

            Text(
                text = destination.description,
                fontSize = 15.sp
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Text(
                text = "Highlights",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    Modifier.padding(16.dp)
                ) {

                    destination.highlights.forEachIndexed { index, highlight ->

                        Text(
                            text = "• $highlight",
                            fontSize = 15.sp,
                            modifier =
                                Modifier.padding(
                                    bottom =
                                        if (index == destination.highlights.lastIndex)
                                            0.dp
                                        else
                                            8.dp
                                )
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Button(
                onClick = onPlanTrip,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("PLAN A TRIP TO ${destination.name.uppercase()}")
            }

            Spacer(
                Modifier.height(20.dp)
            )
        }
    }
}


// =========================
// EXPERIENCE DETAILS SCREEN
// =========================

@Composable
fun ExperienceDetailsScreen(
    experience: ExperienceCategory,
    onBack: () -> Unit
) {

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            OutlinedButton(
                onClick = onBack
            ) {
                Text("← BACK")
            }

            Spacer(
                Modifier.height(20.dp)
            )

            Text(
                experience.icon,
                fontSize = 56.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = experience.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                text = experience.description,
                fontSize = 15.sp
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Text(
                text = "What's included",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            experience.items.forEach { item ->

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = item,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )
        }
    }
}


// =========================
// TRIP PACKAGE RECOMMENDER
// =========================

@Composable
fun TripPackageRecommender(
    onBack: () -> Unit,
    onViewPackage: (RemoteTripPackage) -> Unit
) {

    var daysText by remember {
        mutableStateOf("5")
    }

    var budget by remember {
        mutableStateOf("")
    }

    var travelersText by remember {
        mutableStateOf("2")
    }

    var interest by remember {
        mutableStateOf("Relaxation")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var recommendations by remember {
        mutableStateOf<List<RemoteTripPackage>>(
            emptyList()
        )
    }

    val days =
        daysText.toIntOrNull()

    val travelers =
        travelersText.toIntOrNull()

    val budgetValue =
        budget.toIntOrNull()

    val daysError =
        days != null &&
                days !in 1..30

    val travelersError =
        travelers != null &&
                travelers !in 1..20

    val canRecommend =
        days != null &&
                days in 1..30 &&
                travelers != null &&
                travelers in 1..20 &&
                (
                        budget.isBlank() ||
                                budgetValue != null
                        )

    fun findTrips() {

        if (!canRecommend)
            return

        isLoading = true
        errorMessage = ""
        recommendations = emptyList()

        TripRecommendationApi.recommendTrips(

            days = days!!,

            travelers = travelers!!,

            budgetPerTraveler =
                budgetValue,

            style = interest

        ) { result ->

            isLoading = false

            result
                .onSuccess { packages ->

                    recommendations =
                        packages
                }

                .onFailure { error ->

                    errorMessage =
                        error.message
                            ?: "Unable to get recommendations."
                }
        }
    }

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme.colorScheme.background

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            // BACK BUTTON

            Box(

                modifier =
                    Modifier
                        .size(44.dp)
                        .clickable {
                            onBack()
                        },

                contentAlignment =
                    Alignment.Center

            ) {

                Text(
                    text = "‹",

                    fontSize = 38.sp,

                    fontWeight =
                        FontWeight.Light
                )
            }

            Spacer(
                Modifier.height(6.dp)
            )

            Text(

                "🧳 Trip Package Recommender",

                fontSize = 27.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(

                "Tell TABi what kind of trip you want.",

                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                Modifier.height(22.dp)
            )

            // DAYS

            Text(

                "Trip duration",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            OutlinedTextField(

                value = daysText,

                onValueChange = {

                    daysText =
                        it.filter(
                            Char::isDigit
                        ).take(2)

                    recommendations =
                        emptyList()

                    errorMessage = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Number of days")
                },

                supportingText = {
                    Text(
                        "Enter a value from 1 to 30 days"
                    )
                },

                isError =
                    daysError,

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    )
            )

            if (daysError) {

                Text(

                    "Please enter between 1 and 30 days.",

                    color =
                        MaterialTheme.colorScheme.error,

                    fontSize = 12.sp
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // TRAVELERS

            Text(

                "Travelers",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(7.dp)
            )

            OutlinedTextField(

                value =
                    travelersText,

                onValueChange = {

                    travelersText =
                        it.filter(
                            Char::isDigit
                        ).take(2)

                    recommendations =
                        emptyList()

                    errorMessage = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text("Number of travelers")
                },

                supportingText = {
                    Text(
                        "Enter a value from 1 to 20 travelers"
                    )
                },

                isError =
                    travelersError,

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    )
            )

            if (travelersError) {

                Text(

                    "Please enter between 1 and 20 travelers.",

                    color =
                        MaterialTheme.colorScheme.error,

                    fontSize = 12.sp
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // BUDGET

            OutlinedTextField(

                value =
                    budget,

                onValueChange = {

                    budget =
                        it.filter(
                            Char::isDigit
                        )

                    recommendations =
                        emptyList()

                    errorMessage = ""
                },

                modifier =
                    Modifier.fillMaxWidth(),

                label = {
                    Text(
                        "Maximum budget per traveler (₹)"
                    )
                },

                supportingText = {
                    Text("Optional")
                },

                singleLine = true,

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    )
            )

            Spacer(
                Modifier.height(22.dp)
            )

            // TRAVEL STYLE

            Text(

                "What kind of trip do you want?",

                fontSize = 17.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(
                            rememberScrollState()
                        )

            ) {

                val styles =
                    listOf(

                        "Adventure" to "🏔️",

                        "Relaxation" to "🌴",

                        "Family" to "👨‍👩‍👧",

                        "Cultural" to "🏛️",

                        "Spiritual" to "🛕",

                        "Food" to "🍛"
                    )

                styles.forEach {
                        (style, icon) ->

                    Card(

                        modifier =
                            Modifier
                                .width(112.dp)
                                .height(105.dp)
                                .padding(end = 8.dp)
                                .clickable {

                                    interest =
                                        style

                                    recommendations =
                                        emptyList()

                                    errorMessage =
                                        ""
                                },

                        shape =
                            RoundedCornerShape(16.dp),

                        border =
                            if (
                                interest == style
                            ) {

                                BorderStroke(
                                    2.dp,
                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                )

                            } else {
                                null
                            }

                    ) {

                        Column(

                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            verticalArrangement =
                                Arrangement.Center

                        ) {

                            Text(
                                icon,
                                fontSize = 28.sp
                            )

                            Spacer(
                                Modifier.height(5.dp)
                            )

                            Text(

                                style,

                                fontSize = 13.sp,

                                fontWeight =
                                    if (
                                        interest ==
                                        style
                                    )
                                        FontWeight.Bold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(22.dp)
            )

            // FIND MY TRIP

            Button(

                onClick = {
                    findTrips()
                },

                enabled =
                    canRecommend &&
                            !isLoading,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                if (isLoading) {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.height(22.dp),

                        color =
                            Color.White,

                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        "FIND MY TRIP"
                    )
                }
            }

            // ERROR

            if (
                errorMessage.isNotBlank()
            ) {

                Spacer(
                    Modifier.height(14.dp)
                )

                Text(

                    errorMessage,

                    color =
                        MaterialTheme
                            .colorScheme
                            .error,

                    fontSize = 14.sp
                )
            }

            // RECOMMENDATIONS

            if (
                !isLoading &&
                recommendations.isNotEmpty()
            ) {

                Spacer(
                    Modifier.height(24.dp)
                )

                Text(

                    "Recommended for you",

                    fontSize = 21.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                recommendations.forEach {
                        packageItem ->

                    RemoteTripPackageCard(

                        packageItem =
                            packageItem,

                        travelers =
                            travelers!!,

                        onViewPackage = {

                            onViewPackage(
                                packageItem
                            )
                        }
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )
                }
            }

            Spacer(
                Modifier.height(16.dp)
            )
        }
    }
}


// =========================
// TRIP PACKAGE CARD
// =========================

@Composable
private fun RemoteTripPackageCard(

    packageItem:
    RemoteTripPackage,

    travelers: Int,

    onViewPackage: () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp)

    ) {

        Column(
            Modifier.padding(18.dp)
        ) {

            Text(

                packageItem.title,

                fontSize = 19.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                packageItem.route
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(
                "${packageItem.days} days • ⭐ ${packageItem.rating}"
            )

            Spacer(
                Modifier.height(5.dp)
            )

            Text(

                "₹${packageItem.price} / person",

                fontWeight =
                    FontWeight.Bold
            )

            Text(

                "Approx. ₹${packageItem.price * travelers} for $travelers travelers"
            )

            if (
                packageItem.highlights.isNotEmpty()
            ) {

                Spacer(
                    Modifier.height(8.dp)
                )

                Text(

                    "Highlights",

                    fontWeight =
                        FontWeight.SemiBold
                )

                packageItem
                    .highlights
                    .take(3)
                    .forEach {

                        Text(
                            "• $it",
                            fontSize = 13.sp
                        )
                    }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            // NOW WORKS

            Button(

                onClick =
                    onViewPackage,

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    "VIEW PACKAGE"
                )
            }
        }
    }
}


// =========================
// TRIP DETAILS SCREEN
// =========================

@Composable
fun TripDetailsScreen(

    packageItem:
    RemoteTripPackage,

    onBack: () -> Unit

) {

    Surface(

        modifier =
            Modifier.fillMaxSize(),

        color =
            MaterialTheme
                .colorScheme
                .background

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(20.dp)

        ) {

            // BACK BUTTON

            Box(

                modifier =
                    Modifier
                        .size(44.dp)
                        .clickable {
                            onBack()
                        },

                contentAlignment =
                    Alignment.Center

            ) {

                Text(

                    text = "‹",

                    fontSize = 38.sp,

                    fontWeight =
                        FontWeight.Light
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            // TITLE

            Text(

                text =
                    packageItem.title,

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(

                text =
                    packageItem.route,

                fontSize = 15.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                Modifier.height(18.dp)
            )

            // OVERVIEW

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)

                ) {

                    Text(

                        text =
                            "Trip Overview",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    Text(
                        "📅 ${packageItem.days} days"
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    Text(
                        "⭐ ${packageItem.rating} rating"
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    Text(
                        "🎯 ${packageItem.style}"
                    )

                    Spacer(
                        Modifier.height(7.dp)
                    )

                    Text(

                        "💰 ₹${packageItem.price} per traveler",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )

            // HIGHLIGHTS

            Text(

                text =
                    "✨ Highlights",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            packageItem
                .highlights
                .forEach { highlight ->

                    Card(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = 8.dp
                                ),

                        shape =
                            RoundedCornerShape(14.dp)

                    ) {

                        Text(

                            text =
                                "• $highlight",

                            modifier =
                                Modifier.padding(
                                    15.dp
                                ),

                            fontSize = 15.sp
                        )
                    }
                }

            Spacer(
                Modifier.height(18.dp)
            )

            // ITINERARY

            Text(

                text =
                    "📅 Suggested Itinerary",

                fontSize = 21.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            for (
            day in
            1..packageItem.days
            ) {

                Card(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 10.dp
                            ),

                    shape =
                        RoundedCornerShape(16.dp)

                ) {

                    Column(

                        modifier =
                            Modifier.padding(16.dp)

                    ) {

                        Text(

                            text =
                                "Day $day",

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            Modifier.height(6.dp)
                        )

                        Text(

                            text =
                                when (day) {

                                    1 ->
                                        "Arrival and explore the main attractions."

                                    2 ->
                                        "Explore popular local attractions and experiences."

                                    3 ->
                                        "Discover nearby destinations and local culture."

                                    4 ->
                                        "Enjoy local food, shopping and sightseeing."

                                    5 ->
                                        "Relax, explore remaining attractions and enjoy the destination."

                                    else ->
                                        "Explore more of the destination and enjoy local experiences."
                                },

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(
                Modifier.height(10.dp)
            )

            // FOOD

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)

                ) {

                    Text(

                        text =
                            "🍛 Food to Try",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "Try authentic local cuisine, regional specialties, street food and traditional dishes during your trip."
                    )
                }
            }

            Spacer(
                Modifier.height(14.dp)
            )

            // ACTIVITIES

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp)

            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)

                ) {

                    Text(

                        text =
                            "🎯 Activities",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            "Sightseeing • Local experiences • Photography • Shopping • Food exploration"
                    )
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )

            Button(

                onClick =
                    onBack,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(14.dp)

            ) {

                Text(
                    "BACK TO RECOMMENDATIONS"
                )
            }

            Spacer(
                Modifier.height(20.dp)
            )
        }
    }
}


// =========================
// DASHBOARD FEATURE CARD
// =========================

@Composable
fun DashboardFeatureCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {

    Surface(

        modifier =
            modifier
                .height(90.dp)
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        color =
            if (selected)
                MaterialTheme
                    .colorScheme
                    .primaryContainer
            else
                MaterialTheme
                    .colorScheme
                    .surfaceVariant

    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center

        ) {

            Text(
                text = emoji,
                fontSize = 28.sp
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(

                text = title,

                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}


// =========================
// DESTINATION CARD
// =========================

@Composable
fun DestinationCard(
    name: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        color =
            if (selected)
                MaterialTheme
                    .colorScheme
                    .primaryContainer
            else
                MaterialTheme
                    .colorScheme
                    .surfaceVariant

    ) {

        Row(

            modifier =
                Modifier.padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Text(
                text = emoji,
                fontSize = 34.sp
            )

            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )

            Column {

                Text(

                    text = name,

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(

                    text =
                        "Explore places, attractions and experiences",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontSize = 13.sp
                )
            }
        }
    }
}


// =========================
// DASHBOARD INFO CARD
// =========================

@Composable
fun DashboardInfoCard(
    emoji: String,
    title: String,
    description: String
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            MaterialTheme
                .colorScheme
                .primaryContainer

    ) {

        Column(

            modifier =
                Modifier.padding(20.dp)

        ) {

            Text(
                text = emoji,
                fontSize = 40.sp
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(

                text = title,

                fontSize = 22.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text = description,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                lineHeight = 20.sp
            )
        }
    }
}


// =========================
// DASHBOARD NAV BUTTON
// =========================

@Composable
fun DashboardNavButton(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {

    Column(

        modifier =
            Modifier.clickable(
                onClick = onClick
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {

        Text(
            text = emoji,
            fontSize = 22.sp
        )

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(

            text = title,

            fontSize = 12.sp
        )
    }
}


// =========================
// REGISTER SCREEN
// =========================

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit
) {

    val context =
        LocalContext.current

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    var registrationSuccessful by remember {
        mutableStateOf(false)
    }

    val hasEightCharacters =
        password.length >= 8

    val hasUppercase =
        password.any {
            it.isUpperCase()
        }

    val hasLowercase =
        password.any {
            it.isLowerCase()
        }

    val hasNumber =
        password.any {
            it.isDigit()
        }

    val hasSpecial =
        password.any {
            !it.isLetterOrDigit()
        }

    val passwordValid =
        hasEightCharacters &&
                hasUppercase &&
                hasLowercase &&
                hasNumber &&
                hasSpecial

    val passwordsMatch =
        password.isNotEmpty() &&
                password == confirmPassword

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(24.dp)

    ) {

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )

        Text(

            text =
                "Create your account ✨",

            fontSize = 30.sp,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(

            text =
                "Let's start planning your next adventure.",

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        OutlinedTextField(

            value = name,

            onValueChange = {
                name = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Full Name")
            },

            singleLine = true
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Email")
            },

            singleLine = true,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email
                )
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Password")
            },

            singleLine = true,

            visualTransformation =
                if (showPassword)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {

                Text(

                    text =
                        if (showPassword)
                            "Hide"
                        else
                            "Show",

                    modifier =
                        Modifier.padding(
                            end = 12.dp
                        )
                )
            }
        )

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        Text(

            text =
                "Password requirements",

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        PasswordRequirement(
            "At least 8 characters",
            hasEightCharacters
        )

        PasswordRequirement(
            "One uppercase letter",
            hasUppercase
        )

        PasswordRequirement(
            "One lowercase letter",
            hasLowercase
        )

        PasswordRequirement(
            "One number",
            hasNumber
        )

        PasswordRequirement(
            "One special character",
            hasSpecial
        )

        Spacer(
            modifier =
                Modifier.height(14.dp)
        )

        OutlinedTextField(

            value =
                confirmPassword,

            onValueChange = {
                confirmPassword = it
                message = ""
            },

            modifier =
                Modifier.fillMaxWidth(),

            label = {
                Text("Confirm Password")
            },

            singleLine = true,

            visualTransformation =
                PasswordVisualTransformation()
        )

        if (
            confirmPassword.isNotEmpty()
        ) {

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(

                text =
                    if (passwordsMatch)
                        "✓ Passwords match"
                    else
                        "Passwords do not match",

                color =
                    if (passwordsMatch)
                        Color(0xFF2E7D32)
                    else
                        MaterialTheme
                            .colorScheme
                            .error,

                fontSize = 14.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        if (
            message.isNotEmpty()
        ) {

            Text(

                text = message,

                color =
                    if (
                        registrationSuccessful
                    )
                        Color(0xFF2E7D32)
                    else
                        MaterialTheme
                            .colorScheme
                            .error,

                fontSize = 14.sp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }

        Button(

            onClick = {

                if (name.isBlank()) {

                    message =
                        "Please enter your name."

                    return@Button
                }

                if (email.isBlank()) {

                    message =
                        "Please enter your email."

                    return@Button
                }

                if (!passwordValid) {

                    message =
                        "Please satisfy all password requirements."

                    return@Button
                }

                if (!passwordsMatch) {

                    message =
                        "Passwords do not match."

                    return@Button
                }

                isLoading = true
                message = ""

                ApiClient.register(

                    name = name,

                    email = email,

                    password = password

                ) { success, serverMessage ->

                    isLoading = false

                    if (success) {

                        registrationSuccessful =
                            true

                        message =
                            "Account created successfully! 🎉"

                        context
                            .getSharedPreferences(
                                "tabi_user",
                                Context.MODE_PRIVATE
                            )
                            .edit()
                            .putString(

                                "user_name_${
                                    email.trim()
                                        .lowercase()
                                }",

                                name.trim()
                            )
                            .apply()

                    } else {

                        registrationSuccessful =
                            false

                        message =
                            serverMessage
                    }
                }
            },

            enabled =
                !isLoading,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(54.dp),

            shape =
                RoundedCornerShape(14.dp)

        ) {

            if (isLoading) {

                CircularProgressIndicator(

                    modifier =
                        Modifier.height(22.dp),

                    color =
                        Color.White,

                    strokeWidth = 2.dp
                )

            } else {

                Text(

                    text =
                        "CREATE ACCOUNT",

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        OutlinedButton(

            onClick =
                onBackToLogin,

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(14.dp)

        ) {

            Text(
                "BACK TO LOGIN"
            )
        }

        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}


// =========================
// PASSWORD REQUIREMENT
// =========================

@Composable
fun PasswordRequirement(
    text: String,
    satisfied: Boolean
) {

    Text(

        text =
            if (satisfied)
                "✓ $text"
            else
                "○ $text",

        color =
            if (satisfied)
                Color(0xFF2E7D32)
            else
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,

        fontSize = 14.sp,

        modifier =
            Modifier.padding(
                vertical = 2.dp
            )
    )
}


// =========================
// TABi THEME
// =========================

@Composable
fun TabiTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        content = content
    )
}