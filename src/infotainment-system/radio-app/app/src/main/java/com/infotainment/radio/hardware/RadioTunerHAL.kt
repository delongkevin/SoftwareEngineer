package com.infotainment.radio.hardware

import com.infotainment.radio.model.RadioBand
import com.infotainment.radio.model.RadioStation
import kotlinx.coroutines.flow.Flow

/**
 * Hardware Abstraction Layer (HAL) interface for radio tuner operations.
 *
 * This interface defines the contract between the application layer and the
 * physical radio tuner hardware. Implementations can target:
 * - Software simulation (for development/testing)
 * - Real FM/AM tuner hardware via Android HAL or serial interface
 * - SDR (Software Defined Radio) hardware
 *
 * To integrate with real vehicle hardware, implement this interface with
 * the appropriate hardware driver communication (e.g., serial/UART, I2C,
 * CAN bus, or vendor-specific HAL).
 */
interface RadioTunerHAL {

    /**
     * Initialize the radio tuner hardware.
     * @return true if initialization was successful
     */
    suspend fun initialize(): Boolean

    /**
     * Tune to a specific frequency.
     * @param frequency The target frequency (MHz for FM, kHz for AM)
     * @param band The radio band (AM or FM)
     * @return The tuned RadioStation, or null if tuning failed
     */
    suspend fun tuneTo(frequency: Double, band: RadioBand): RadioStation?

    /**
     * Seek to the next station with sufficient signal strength.
     * @param seekUp true to seek up in frequency, false to seek down
     * @param band The radio band to seek in
     * @return Flow emitting stations found during seek
     */
    fun seek(seekUp: Boolean, band: RadioBand): Flow<RadioStation>

    /**
     * Scan all available stations in the current band.
     * @param band The radio band to scan
     * @return Flow emitting all stations found during scan
     */
    fun scanAll(band: RadioBand): Flow<RadioStation>

    /**
     * Get the current signal strength for the tuned frequency.
     * @return Signal strength value (0-100)
     */
    suspend fun getSignalStrength(): Int

    /**
     * Set the radio volume at hardware level.
     * @param volume Volume level (0-100)
     */
    suspend fun setVolume(volume: Int)

    /**
     * Mute/unmute the radio output.
     * @param mute true to mute, false to unmute
     */
    suspend fun setMute(mute: Boolean)

    /**
     * Power off the radio tuner hardware.
     */
    suspend fun shutdown()

    /**
     * Observable flow of the current tuner state changes.
     */
    val tunerStateFlow: Flow<RadioStation?>
}
