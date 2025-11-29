package us.pinette.openfamilymap.android.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.pinette.openfamilymap.android.services.APIService
import us.pinette.openfamilymap.android.services.LoginRequest
import javax.inject.Inject

/**
 * A very lightweight ViewModel that exposes UI‑state as StateFlow.
 * Replace the fake login call with a real Retrofit / Ktor repo later.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: APIService
) : ViewModel() {

    private val _baseUrl = MutableStateFlow("")
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    // Input state
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // ---- User interactions ----

    fun onBaseUrlChange(newBaseUrl: String) {
        _baseUrl.value = newBaseUrl
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Triggered by the Login button.
     */
    fun login() {
        // Guard against concurrent calls
        if (_isLoading.value) return

        // Reset old state
        _error.value = null
        _isSuccess.value = false

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fake network delay
                val result = apiService.login(LoginRequest(_username.value, _password.value))

                // Fake validation – you can replace this block
                if (result.accessToken.isNotEmpty()) {
                    _isSuccess.value = true
                } else {
                    // Simulate API error
                    throw IllegalArgumentException("Invalid credentials")
                }

            } catch (e: Exception) {
                // In a real scenario you might want to parse HTTP error codes
                _error.value = e.message ?: "Something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun serverIsConfigured(): Boolean {

    }
}