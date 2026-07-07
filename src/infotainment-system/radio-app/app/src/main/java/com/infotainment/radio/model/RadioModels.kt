package com.infotainment.radio.model

/**
 * Represents the radio band type.
 */
enum class RadioBand {
    AM,
    FM
}

/**
 * Represents the current state of a radio station.
 */
data class RadioStation(
    val frequency: Double,
    val band: RadioBand,
    val signalStrength: Int = 0,  // 0-100
    val stationName: String? = null,
    val isFavorite: Boolean = false
)

/**
 * Represents the overall radio state.
 */
data class RadioState(
    val currentStation: RadioStation? = null,
    val band: RadioBand = RadioBand.FM,
    val isScanning: Boolean = false,
    val isMuted: Boolean = false,
    val volume: Int = 50,  // 0-100
    val presets: List<RadioStation> = emptyList()
)

/**
 * Frequency range constants for AM/FM bands.
 */
object RadioFrequencyRange {
    const val FM_MIN = 87.5
    const val FM_MAX = 108.0
    const val FM_STEP = 0.1

    const val AM_MIN = 530.0
    const val AM_MAX = 1710.0
    const val AM_STEP = 10.0
}
