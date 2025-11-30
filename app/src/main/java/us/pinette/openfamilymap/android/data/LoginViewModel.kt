package us.pinette.openfamilymap.android.data

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.pinette.openfamilymap.android.services.APIService
import us.pinette.openfamilymap.android.services.LoginRequest
import javax.inject.Inject
import androidx.core.content.edit
import us.pinette.openfamilymap.android.di.BaseUrlInterceptor
import us.pinette.openfamilymap.android.services.AuthService

/**
 * A very lightweight ViewModel that exposes UI‑state as StateFlow.
 * Replace the fake login call with a real Retrofit / Ktor repo later.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: APIService,
    private val authService: AuthService,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _baseUrl = MutableStateFlow(sharedPreferences.getString(BaseUrlInterceptor.API_BASE_URL_PREF, "")!!)
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

    private val _isServerConfigured = MutableStateFlow(false)
    val isServerConfigured: StateFlow<Boolean> = _isServerConfigured.asStateFlow()

    init {
        checkServerConfiguration()
    }

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
                val result = authService.login(_username.value, _password.value)

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

    fun checkServerConfiguration() {
        _isServerConfigured.value = false

        // Reset old state
        _error.value = null

        // Guard against concurrent calls
        if (_isLoading.value || _baseUrl.value.isEmpty()) return

        // update the preference for baseUrl
        sharedPreferences.edit {
            putString(BaseUrlInterceptor.API_BASE_URL_PREF, _baseUrl.value)
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result = apiService.status()

                if (result.openFamilyMapApiVersion.isNotEmpty()) {
                    _isServerConfigured.value = true
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
}