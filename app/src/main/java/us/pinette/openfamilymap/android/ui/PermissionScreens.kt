package us.pinette.openfamilymap.android.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import us.pinette.openfamilymap.android.permissions.PermissionStatus
import us.pinette.openfamilymap.android.permissions.getPermissionStatus

@Composable
fun ForegroundAndActivityPermissionScreen(onComplete: () -> Unit) {
    val context = LocalContext.current

    var locationStatus by remember { mutableStateOf(context.getPermissionStatus(Manifest.permission.ACCESS_FINE_LOCATION)) }
    var activityStatus by remember { mutableStateOf(context.getPermissionStatus(Manifest.permission.ACTIVITY_RECOGNITION)) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        val loc = map[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val act = map[Manifest.permission.ACTIVITY_RECOGNITION] == true

        locationStatus = if (loc) PermissionStatus.Granted else context.getPermissionStatus(Manifest.permission.ACCESS_FINE_LOCATION)
        activityStatus = if (act) PermissionStatus.Granted else context.getPermissionStatus(Manifest.permission.ACTIVITY_RECOGNITION)

        if (loc && act) onComplete()
    }

    val ready = locationStatus is PermissionStatus.Granted &&
            activityStatus is PermissionStatus.Granted

    Column(Modifier.padding(24.dp)) {
        Text("Permissions Needed", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        Text("We need your location and physical activity so we can detect when you begin walking, biking, or driving.")

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    )
                )
            }
        ) { Text("Allow permissions") }

        if (ready) {
            LaunchedEffect(Unit) { onComplete() }
        }
    }
}

@Composable
fun BackgroundLocationExplanation(onNext: () -> Unit, onSkip: () -> Unit) {
    Column(Modifier.padding(24.dp)) {

        Text("Allow background location", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text(
            "Your movement is detected even when the app is closed. " +
                    "Android requires special permission for this."
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text("Not now")
        }
    }
}

@Composable
fun BackgroundLocationRequestScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity

    var status by remember {
        mutableStateOf(context.getPermissionStatus(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        status = if (granted) PermissionStatus.Granted
        else context.getPermissionStatus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        if (granted) onComplete()
    }

    handlePermissionCheckOnReturnToFocus(context, onComplete)

    Column(Modifier.padding(24.dp)) {
        Text("Background location", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                launcher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Allow background location")
        }

        when (status) {
            PermissionStatus.Granted -> {
                LaunchedEffect(Unit) { onComplete() }
            }
            is PermissionStatus.Denied -> {
                val s = status as PermissionStatus.Denied
                Spacer(Modifier.height(16.dp))
                Text(
                    if (s.shouldShowRationale)
                        "We need this to detect movement automatically."
                    else
                        "Background location is denied. Enable it in settings."
                )

                if (!s.shouldShowRationale) {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            activity.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                            )
                        }
                    ) {
                        Text("Open settings")
                    }
                }
            }
        }
    }
}

@Composable
fun handlePermissionCheckOnReturnToFocus(context: Context, onComplete: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        Log.d("Lifecycle observer", "Observer started.")

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val newStatus = context.getPermissionStatus(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                Log.d("Focus", "Status: $newStatus, constant: ${PermissionStatus.Granted}, boolean: ${newStatus == PermissionStatus.Granted}")

                if (newStatus == PermissionStatus.Granted) {
                    onComplete()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

