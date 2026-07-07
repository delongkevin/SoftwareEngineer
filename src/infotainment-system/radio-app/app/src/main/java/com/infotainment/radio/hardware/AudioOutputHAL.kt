package com.infotainment.radio.hardware

import com.infotainment.radio.model.AudioSource
import kotlinx.coroutines.flow.Flow

/**
 * Hardware Abstraction Layer (HAL) interface for the vehicle audio system.
 *
 * This interface manages the audio output routing and equalization
 * settings for the vehicle's speaker system. Implementations can target:
 * - Software mixer (for development/testing)
 * - Vehicle amplifier control via CAN bus
 * - DSP (Digital Signal Processor) hardware control
 *
 * To integrate with real vehicle hardware, implement this interface with
 * appropriate communication to the vehicle's audio amplifier and DSP
 * (typically via CAN bus or I2C).
 */
interface AudioOutputHAL {

    /**
     * Initialize the audio output system.
     * @return true if initialization was successful
     */
    suspend fun initialize(): Boolean

    /**
     * Switch the active audio source.
     * @param source The audio source to activate
     */
    suspend fun setAudioSource(source: AudioSource)

    /**
     * Set the master volume level.
     * @param volume Volume level (0-100)
     */
    suspend fun setMasterVolume(volume: Int)

    /**
     * Set mute state.
     * @param mute true to mute, false to unmute
     */
    suspend fun setMute(mute: Boolean)

    /**
     * Set bass equalization level.
     * @param level Bass level (-10 to +10)
     */
    suspend fun setBass(level: Int)

    /**
     * Set treble equalization level.
     * @param level Treble level (-10 to +10)
     */
    suspend fun setTreble(level: Int)

    /**
     * Set left/right balance.
     * @param balance Balance (-10 left to +10 right)
     */
    suspend fun setBalance(balance: Int)

    /**
     * Set front/rear fade.
     * @param fade Fade (-10 rear to +10 front)
     */
    suspend fun setFade(fade: Int)

    /**
     * Observable flow of volume changes (e.g., from steering wheel controls).
     */
    val volumeChangeFlow: Flow<Int>

    /**
     * Shut down the audio output system.
     */
    suspend fun shutdown()
}
