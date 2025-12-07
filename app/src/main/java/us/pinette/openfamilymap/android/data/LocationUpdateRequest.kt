package us.pinette.openfamilymap.android.data

data class LocationUpdateRequest(
    val userId: Int,
    
    // Platform info
    val platform: String,
    val rawProvider: String?,
    
    // Core position
    val latitude: Double,
    val longitude: Double,
    
    // Altitude
    val altitudeMeters: Double?,
    val verticalAccuracyMeters: Double?,
    
    // Horizontal accuracy
    val horizontalAccuracyMeters: Double?,
    
    // Heading / bearing / course
    val bearingDegrees: Double?,
    val bearingAccuracyDegrees: Double?,
    
    // Speed
    val speedMetersPerSecond: Double?,
    val speedAccuracyMetersPerSecond: Double?,
    
    val elapsedRealtimeSinceBoot: String = "00:00:00",
    
    // Indoor / extra info
    val floorLevel: Int?,
    val isMock: Boolean?,
)