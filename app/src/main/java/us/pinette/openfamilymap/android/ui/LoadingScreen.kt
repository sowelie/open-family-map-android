package us.pinette.openfamilymap.android.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LoadingScreen() {
    Scaffold() { _ ->
        Text("Loading...")
    }
}