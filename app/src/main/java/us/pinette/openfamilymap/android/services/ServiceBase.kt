package us.pinette.openfamilymap.android.services

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import java.time.Duration
import java.time.Instant

abstract class ServiceBase(
    private val sharedPreferences: SharedPreferences,
    private val apiService: APIService
) {
    protected suspend fun checkAuth() {
        val savedInstant: Instant? =
            sharedPreferences.getLong("savedAtUtc", -1L)
                .takeIf { it != -1L }?.let { Instant.ofEpochMilli(it) }

        if (Duration.between(savedInstant ?: Instant.MIN, Instant.now()).toMinutes() > 10) {
            Log.d("open-family-map", "accessToken: ${sharedPreferences.getString("accessToken", "")} refreshToken: ${sharedPreferences.getString("refreshToken", "")}")

            try {
                val result = apiService.refresh(RefreshRequest(refreshToken = sharedPreferences.getString("refreshToken", "")!!))

                if (result.accessToken.isEmpty()) {
                    throw IllegalArgumentException("Refresh failed.")
                } else {
                    updateTokens(result.accessToken, result.refreshToken)
                }
            } catch (ex: Exception) {
                Log.w("open-family-map", "Auth refresh failed")
            }
        }
    }

    protected fun updateTokens(accessToken: String, refreshToken: String) {
        // save the tokens
        sharedPreferences.edit {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            putString("lastTokenUpdate", System.currentTimeMillis().toString())
        }
    }
}