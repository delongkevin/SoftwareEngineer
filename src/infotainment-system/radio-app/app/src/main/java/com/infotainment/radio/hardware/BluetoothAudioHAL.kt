package com.infotainment.radio.hardware

import com.infotainment.radio.model.BluetoothAudioState
import com.infotainment.radio.model.BluetoothDevice
import com.infotainment.radio.model.BluetoothMediaMetadata
import kotlinx.coroutines.flow.Flow

/**
 * Hardware Abstraction Layer (HAL) interface for Bluetooth audio operations.
 *
 * This interface defines the contract between the application layer and the
 * Bluetooth hardware/stack. Implementations can target:
 * - Android's native Bluetooth stack (for development/testing)
 * - Vehicle-specific Bluetooth modules via vendor HAL
 * - Custom BT chipset drivers for automotive use
 *
 * To integrate with real vehicle hardware, implement this interface with
 * the appropriate Bluetooth stack communication for your vehicle's
 * head unit hardware.
 */
interface BluetoothAudioHAL {

    /**
     * Initialize the Bluetooth subsystem.
     * @return true if initialization was successful
     */
    suspend fun initialize(): Boolean

    /**
     * Start scanning for available Bluetooth devices.
     * @return Flow emitting discovered devices
     */
    fun startDiscovery(): Flow<BluetoothDevice>

    /**
     * Stop scanning for Bluetooth devices.
     */
    suspend fun stopDiscovery()

    /**
     * Pair with a Bluetooth device.
     * @param address The MAC address of the device to pair
     * @return true if pairing was successful
     */
    suspend fun pair(address: String): Boolean

    /**
     * Connect to a paired Bluetooth device for audio streaming.
     * @param address The MAC address of the device to connect
     * @return true if connection was successful
     */
    suspend fun connect(address: String): Boolean

    /**
     * Disconnect from the currently connected device.
     * @return true if disconnection was successful
     */
    suspend fun disconnect(): Boolean

    /**
     * Get the list of paired devices.
     * @return List of paired BluetoothDevice objects
     */
    suspend fun getPairedDevices(): List<BluetoothDevice>

    /**
     * Send play command to connected device.
     */
    suspend fun play()

    /**
     * Send pause command to connected device.
     */
    suspend fun pause()

    /**
     * Send next track command to connected device.
     */
    suspend fun nextTrack()

    /**
     * Send previous track command to connected device.
     */
    suspend fun previousTrack()

    /**
     * Set volume for Bluetooth audio.
     * @param volume Volume level (0-100)
     */
    suspend fun setVolume(volume: Int)

    /**
     * Observable flow of Bluetooth audio state changes.
     */
    val audioStateFlow: Flow<BluetoothAudioState>

    /**
     * Observable flow of media metadata updates.
     */
    val mediaMetadataFlow: Flow<BluetoothMediaMetadata>

    /**
     * Shut down the Bluetooth subsystem.
     */
    suspend fun shutdown()
}
