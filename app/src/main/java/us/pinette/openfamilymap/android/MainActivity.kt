package us.pinette.openfamilymap.android

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import us.pinette.openfamilymap.android.data.MainViewModel
import us.pinette.openfamilymap.android.permissions.PermissionStatus
import us.pinette.openfamilymap.android.permissions.getPermissionStatus
import us.pinette.openfamilymap.android.services.APIService
import us.pinette.openfamilymap.android.services.ActivityTransitionManager
import us.pinette.openfamilymap.android.services.AuthService
import us.pinette.openfamilymap.android.ui.BackgroundLocationExplanation
import us.pinette.openfamilymap.android.ui.BackgroundLocationRequestScreen
import us.pinette.openfamilymap.android.ui.ForegroundAndActivityPermissionScreen
import us.pinette.openfamilymap.android.ui.LoadingScreen
import us.pinette.openfamilymap.android.ui.LoginScreen
import us.pinette.openfamilymap.android.ui.WelcomeScreen
import us.pinette.openfamilymap.android.ui.theme.OpenFamilyMapTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    @Inject lateinit var authService: AuthService

    @Inject lateinit var apiService: APIService
    @Inject lateinit var activityTransitionManager: ActivityTransitionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenFamilyMapTheme {
                OpenFamilyMapApp(activityTransitionManager = activityTransitionManager)
            }
        }
    }
}

@Composable
fun OpenFamilyMapApp(
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    activityTransitionManager: ActivityTransitionManager
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        NavHost(
            navController = navController,
            startDestination = if (mainViewModel.loading.collectAsState().value)
                    Screens.Loading.name
                else if (mainViewModel.userInfo.collectAsState().value == null)
                    Screens.Login.name
                else Screens.Welcome.name,
            modifier = Modifier.padding(16.dp)
        ) {
            composable(route = Screens.Login.name) {
                LoginScreen {
                    navController.navigate(Screens.Welcome.name)
                }
            }

            composable(route = Screens.Welcome.name) {
                PermissionsFlow {
                    activityTransitionManager.startMonitoring()

                    mainViewModel.completePermissionFlow()
                }

                if (mainViewModel.permissionFlowComplete.collectAsState().value) {
                    WelcomeScreen()
                }
            }

            composable(route = Screens.Loading.name) {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun PermissionsFlow(onDone: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    val context = LocalContext.current
    var status by remember {
        mutableStateOf(context.getPermissionStatus(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
    }

    if (status == PermissionStatus.Granted) {
        onDone()
    }

    when (step) {
        0 -> ForegroundAndActivityPermissionScreen(onComplete = { step = 1 })
        1 -> BackgroundLocationExplanation(onNext = { step = 2 }, onSkip = onDone)
        2 -> BackgroundLocationRequestScreen(onComplete = onDone)
    }
}

