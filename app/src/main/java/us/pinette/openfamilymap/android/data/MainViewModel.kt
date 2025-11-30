package us.pinette.openfamilymap.android.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import us.pinette.openfamilymap.android.services.AuthService
import us.pinette.openfamilymap.android.services.UserInfoResponse
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authService: AuthService
): ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfoResponse?>(null)
    val userInfo: StateFlow<UserInfoResponse?> = _userInfo.asStateFlow()

    init {
        viewModelScope.launch {
            _userInfo.value = authService.getUserInfo()
        }
    }
}