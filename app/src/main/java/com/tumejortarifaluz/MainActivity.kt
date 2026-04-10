package com.tumejortarifaluz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tumejortarifaluz.navigation.Screen
import com.tumejortarifaluz.ui.components.OfflineBanner
import com.tumejortarifaluz.ui.screens.*
import com.tumejortarifaluz.ui.theme.TuMejorTarifaLuzTheme
import com.tumejortarifaluz.ui.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: com.tumejortarifaluz.ui.viewmodel.ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            TuMejorTarifaLuzTheme(darkTheme = isDarkTheme) {
                BiometricGate {
                    AppNavigation(themeViewModel = themeViewModel)
                }
            }
        }
    }
}

@Composable
fun BiometricGate(content: @Composable () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsViewModel: com.tumejortarifaluz.ui.viewmodel.SettingsViewModel = hiltViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()

    // Only gate if biometrics is enabled AND device supports it
    val biometricsEnabled = settingsState.biometricsEnabled
    var isAuthenticated by remember { mutableStateOf(!biometricsEnabled) }
    var authError by remember { mutableStateOf<String?>(null) }

    // Check if biometrics available on device
    val biometricManager = remember { BiometricManager.from(context) }
    val canAuthenticate = remember {
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }

    // Update authenticated state if biometrics gets disabled
    LaunchedEffect(biometricsEnabled) {
        if (!biometricsEnabled) {
            isAuthenticated = true
        }
    }

    // Show biometric prompt on launch
    LaunchedEffect(biometricsEnabled, canAuthenticate) {
        if (biometricsEnabled && canAuthenticate && !isAuthenticated) {
            val activity = context as? FragmentActivity ?: return@LaunchedEffect
            val executor = ContextCompat.getMainExecutor(context)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    isAuthenticated = true
                    authError = null
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        authError = errString.toString()
                    }
                }
                override fun onAuthenticationFailed() {
                    authError = "Huella no reconocida. Inténtalo de nuevo."
                }
            }
            val prompt = BiometricPrompt(activity, executor, callback)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("TuMejorTarifaLuz")
                .setSubtitle("Verifica tu identidad para acceder")
                .setNegativeButtonText("Cancelar")
                .build()
            prompt.authenticate(promptInfo)
        }
    }

    if (isAuthenticated || !biometricsEnabled || !canAuthenticate) {
        content()
    } else {
        // Lock screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(48.dp)
            ) {
                Icon(
                    Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "TuMejorTarifaLuz",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Autenticación biométrica requerida",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                
                if (authError != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        authError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        val activity = context as? FragmentActivity ?: return@Button
                        val executor = ContextCompat.getMainExecutor(context)
                        val callback = object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                isAuthenticated = true
                                authError = null
                            }
                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                authError = errString.toString()
                            }
                            override fun onAuthenticationFailed() {
                                authError = "Huella no reconocida"
                            }
                        }
                        val prompt = BiometricPrompt(activity, executor, callback)
                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("TuMejorTarifaLuz")
                            .setSubtitle("Verifica tu identidad para acceder")
                            .setNegativeButtonText("Cancelar")
                            .build()
                        prompt.authenticate(promptInfo)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Desbloquear", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    isOffline: Boolean = false,
    themeViewModel: com.tumejortarifaluz.ui.viewmodel.ThemeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val networkViewModel: NetworkViewModel = hiltViewModel()
    val isOfflineState by networkViewModel.isOffline.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable<Screen.Splash> {
            SplashScreen(onFinished = {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Splash) { inclusive = true }
                }
            })
        }
        composable<Screen.Onboarding1> { OnboardingScreen(title = "Ahorro Inteligente", subtitle = "Compara y ahorra en tu factura de la luz.", onNext = { navController.navigate(Screen.Onboarding2) }) }
        composable<Screen.Onboarding2> { OnboardingScreen(title = "Energía Sostenible", subtitle = "Encuentra las mejores tarifas de energía verde.", onNext = { navController.navigate(Screen.Onboarding3) }) }
        composable<Screen.Onboarding3> { OnboardingScreen(title = "Control Total", subtitle = "Visualiza tu ahorro en tiempo real.", onNext = { navController.navigate(Screen.Login) }) }
        
        composable<Screen.Login> { 
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate(Screen.Home) { 
                        popUpTo(Screen.Home) { inclusive = true } 
                    } 
                }, 
                onBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Screen.Register) }
            ) 
        }
        composable<Screen.Register> { RegisterScreen(onRegisterSuccess = { navController.navigate(Screen.Home) { popUpTo(Screen.Login) { inclusive = true } } }, onBackToLogin = { navController.popBackStack() }) }

        composable<Screen.Home> { MainScreen(navController, themeViewModel, isOfflineState) }
        composable<Screen.History> { MainScreen(navController, themeViewModel, isOfflineState) }
        composable<Screen.Favorites> { MainScreen(navController, themeViewModel, isOfflineState) }
        composable<Screen.Settings> { MainScreen(navController, themeViewModel, isOfflineState) }
        
        composable<Screen.UploadInvoice> { UploadInvoiceScreen(onUploadSuccess = { navController.navigate(Screen.Validation) }, onNavigateToManual = { navController.navigate(Screen.ManualEntry) }) }
        composable<Screen.ManualEntry> { ManualEntryScreen(onDataEntered = { navController.navigate(Screen.Results) }, onBack = { navController.popBackStack() }) }
        composable<Screen.Validation> { ValidationScreen(onConfirm = { navController.navigate(Screen.Results) }, onBack = { navController.popBackStack() }) }
        composable<Screen.Results> { 
            ResultsScreen(
                onNavigateToDetail = { id -> navController.navigate(Screen.TariffDetail(id)) }, 
                onEditConsumption = { navController.navigate(Screen.ManualEntry) }, 
                onNavigateToHome = { 
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            ) 
        }
        composable<Screen.TariffDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Screen.TariffDetail>()
            ComparisonScreen(
                tariffId = detail.tariffId,
                onBack = { navController.popBackStack() },
                onContracted = { /* Skip success screen as requested by user */ }
            )
        }
        composable<Screen.HelpCenter> { HelpCenterScreen(onNavigateToChat = { navController.navigate(Screen.Chat) }, onNavigateToArticle = { title -> navController.navigate(Screen.HelpArticle(title)) }, onBack = { navController.popBackStack() }) }
        composable<Screen.Chat> { ChatScreen(onBack = { navController.popBackStack() }) }
        composable<Screen.Success> { SuccessScreen(onGoHome = { navController.navigate(Screen.Rating) }) }
        composable<Screen.Rating> { RatingScreen(onFinish = { navController.navigate(Screen.Home) { popUpTo(Screen.Home) { inclusive = true } } }) }
        composable<Screen.HelpArticle> { backStackEntry ->
            val article = backStackEntry.toRoute<Screen.HelpArticle>()
            HelpArticleScreen(title = article.articleTitle, onBack = { navController.popBackStack() })
        }
        composable<Screen.Profile> {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MainScreen(
    rootNavController: androidx.navigation.NavHostController,
    themeViewModel: com.tumejortarifaluz.ui.viewmodel.ThemeViewModel,
    isOffline: Boolean = false
) {
    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            OfflineBanner(isOffline = isOffline)
        },
        bottomBar = {
            com.tumejortarifaluz.ui.components.MainFloatingNavbar(
                currentDestination = currentDestination,
                onItemClick = { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true
                    if (!isSelected) {
                        rootNavController.navigate(screen) {
                            popUpTo(rootNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                currentDestination?.hasRoute(Screen.Home::class) == true -> HomeScreen(
                    onNavigateToUpload = { rootNavController.navigate(Screen.UploadInvoice) },
                    onNavigateToManual = { rootNavController.navigate(Screen.ManualEntry) },
                    onNavigateToHelp = { rootNavController.navigate(Screen.HelpCenter) },
                    onNavigateToLogin = { rootNavController.navigate(Screen.Login) },
                    onNavigateToSettings = { rootNavController.navigate(Screen.Settings) }
                )
                currentDestination?.hasRoute(Screen.History::class) == true -> HistoryScreen(
                    onNavigateToResults = { rootNavController.navigate(Screen.Results) }
                )
                currentDestination?.hasRoute(Screen.Favorites::class) == true -> FavoritesScreen(onNavigateToDetail = { id -> rootNavController.navigate(Screen.TariffDetail(id)) })
                currentDestination?.hasRoute(Screen.Settings::class) == true -> SettingsScreen(
                    onBack = { 
                        rootNavController.navigate(Screen.Home) {
                            popUpTo(Screen.Home) { inclusive = true }
                        }
                    },
                    onNavigateToProfile = { rootNavController.navigate(Screen.Profile) },
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}
