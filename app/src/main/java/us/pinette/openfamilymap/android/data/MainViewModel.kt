package us.pinette.openfamilymap.android.data

import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import us.pinette.openfamilymap.android.services.APIService
import us.pinette.openfamilymap.android.services.AuthService
import us.pinette.openfamilymap.android.services.UserInfoResponse
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthService,
    private val apiService: APIService,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo: StateFlow<UserInfoResponse?> = _userInfo.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _permissionFlowComplete = MutableStateFlow(false)
    val permissionFlowComplete: StateFlow<Boolean> = _permissionFlowComplete.asStateFlow()

    init {
        viewModelScope.launch {
            _userInfo.value = authService.getUserInfo()
            _loading.value = false

            updateLocation()
        }
    }

    fun completePermissionFlow() {
        _permissionFlowComplete.value = true
    }

    fun updateLocation() {

    }
}