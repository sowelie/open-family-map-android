package us.pinette.openfamilymap.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import us.pinette.openfamilymap.android.data.MainViewModel
import us.pinette.openfamilymap.android.services.AuthService
import us.pinette.openfamilymap.android.services.UserInfoResponse
import us.pinette.openfamilymap.android.ui.LoginScreen
import us.pinette.openfamilymap.android.ui.WelcomeScreen
import us.pinette.openfamilymap.android.ui.theme.OpenFamilyMapTheme
import javax.inject.Inject
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    @Inject lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenFamilyMapTheme {
                OpenFamilyMapApp()
            }
        }
    }
}

@Composable
fun OpenFamilyMapApp(
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
        NavHost(
            navController = navController,
            startDestination = if (mainViewModel.userInfo.collectAsState().value == null)
                Screens.Login.name
            else Screens.Welcome.name,
            modifier = Modifier.padding(16.dp)
        ) {
            composable(route = Screens.Login.name) {
                LoginScreen()
            }

            composable(route = Screens.Welcome.name) {
                WelcomeScreen()
            }
        }
    }
}