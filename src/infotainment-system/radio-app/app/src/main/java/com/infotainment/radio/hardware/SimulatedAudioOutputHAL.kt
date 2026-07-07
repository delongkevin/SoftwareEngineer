package com.infotainment.radio.hardware

import com.infotainment.radio.model.AudioSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Software simulation of the Audio Output HAL for development and testing.
 *
 * This implementation simulates audio output routing and equalization
 * without requiring actual amplifier/DSP hardware.
 *
 * Replace this implementation with a hardware-specific one when
 * integrating with a real vehicle's audio amplifier (typically
 * controlled via CAN bus or I2C/SPI).
 */
class SimulatedAudioOutputHAL : AudioOutputHAL {

    private val _volumeChangeFlow = MutableStateFlow(50)
    override val volumeChangeFlow: Flow<Int> = _volumeChangeFlow.asStateFlow()

    private var isInitialized = false
    private var currentSource = AudioSource.FM_RADIO
    private var masterVolume = 50
    private var isMuted = false

    override suspend fun initialize(): Boolean {
        delay(200)
        isInitialized = true
        return true
    }

    override suspend fun setAudioSource(source: AudioSource) {
        if (!isInitialized) return
        currentSource = source
    }

    override suspend fun setMasterVolume(volume: Int) {
        if (!isInitialized) return
        masterVolume = volume.coerceIn(0, 100)
        _volumeChangeFlow.value = masterVolume
    }

    override suspend fun setMute(mute: Boolean) {
        if (!isInitialized) return
        isMuted = mute
    }

    override suspend fun setBass(level: Int) {
        if (!isInitialized) return
        // Simulated - in real hardware this would configure DSP
    }

    override suspend fun setTreble(level: Int) {
        if (!isInitialized) return
        // Simulated - in real hardware this would configure DSP
    }

    override suspend fun setBalance(balance: Int) {
        if (!isInitialized) return
        // Simulated - in real hardware this would configure amplifier
    }

    override suspend fun setFade(fade: Int) {
        if (!isInitialized) return
        // Simulated - in real hardware this would configure amplifier
    }

    override suspend fun shutdown() {
        isInitialized = false
    }
}
