package us.pinette.openfamilymap.android.services

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import us.pinette.openfamilymap.android.data.LocationUpdateRequest
import us.pinette.openfamilymap.android.data.LocationUpdateResponse
import java.time.LocalDateTime

interface APIService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): LoginResponse

    @GET("status")
    suspend fun status(): StatusResponse

    @GET("auth/userInfo")
    suspend fun getUserInfo(): UserInfoResponse

    @POST("locationHistory/update")
    suspend fun locationUpdate(@Body body: LocationUpdateRequest): LocationUpdateResponse
}

data class RefreshRequest(val refreshToken: String = "")

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class StatusResponse(
    val status: String,
    val timestamp: String,
    val openFamilyMapApiVersion: String
)

data class UserInfoResponse(
    val id: Int,
    val login: String,
    val displayName: String
)
