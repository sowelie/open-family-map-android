package us.pinette.openfamilymap.android.services

import android.content.SharedPreferences

class AuthService(
    private val sharedPreferences: SharedPreferences,
    private val apiService: APIService
): ServiceBase(sharedPreferences, apiService) {
    suspend fun login(login: String, password: String): LoginResponse {
        val result = apiService.login(LoginRequest(login, password))

        if (result.accessToken.isNotEmpty()) {
            updateTokens(result.accessToken, result.refreshToken)
        }

        return result
    }

    suspend fun getUserInfo(): UserInfoResponse? {
        checkAuth()

        return try {
                apiService.getUserInfo()
            } catch (_: Exception) {
                null
            }
    }
}