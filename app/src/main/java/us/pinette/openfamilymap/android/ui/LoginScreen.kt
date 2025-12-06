package us.pinette.openfamilymap.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import us.pinette.openfamilymap.android.data.LoginViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), onDone: () -> Unit) {
    // UI state from ViewModel
    val isServerConfigured by viewModel.isServerConfigured.collectAsState()
    val baseUrl by viewModel.baseUrl.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isServerConfigured) {
                Text(
                    text = "Please log in",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = viewModel::onUsernameChange,
                    label = { Text("Login") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (baseUrl.isNotEmpty() && !errorMessage.isNullOrEmpty()) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Button(
                    onClick = { viewModel.login() },
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Text("Login")
                }

                // Optional: a helpful error text underneath button
                if (!isLoading && errorMessage == null && isSuccess) {
                    Text(
                        text = "You are now logged in",
                        color = MaterialTheme.colorScheme.primary,
                    )

                    onDone()
                }
            } else {
                Text(
                    text ="Enter your server URL",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = viewModel::onBaseUrlChange,
                    label = { Text("Server URL") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (baseUrl.isNotEmpty() && !errorMessage.isNullOrEmpty()) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Button(
                    onClick = { viewModel.checkServerConfiguration() },
                    enabled = !isLoading && baseUrl.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                    }
                    Text("Next")
                }
            }
        }
    }
}