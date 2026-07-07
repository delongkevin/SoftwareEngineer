package com.infotainment.radio.model

/**
 * Represents the active audio source in the infotainment system.
 */
enum class AudioSource {
    FM_RADIO,
    AM_RADIO,
    BLUETOOTH
}

/**
 * Represents the overall infotainment audio system state.
 */
data class AudioSystemState(
    val activeSource: AudioSource = AudioSource.FM_RADIO,
    val masterVolume: Int = 50,  // 0-100
    val isMuted: Boolean = false,
    val bassLevel: Int = 0,      // -10 to +10
    val trebleLevel: Int = 0,    // -10 to +10
    val balance: Int = 0,        // -10 (left) to +10 (right)
    val fade: Int = 0            // -10 (rear) to +10 (front)
)
