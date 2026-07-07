package com.infotainment.radio.hardware

import com.infotainment.radio.model.BluetoothAudioState
import com.infotainment.radio.model.BluetoothConnectionState
import com.infotainment.radio.model.BluetoothDevice
import com.infotainment.radio.model.BluetoothMediaMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Software simulation of the Bluetooth Audio HAL for development and testing.
 *
 * This implementation simulates Bluetooth device discovery, pairing,
 * connection, and media playback without requiring actual Bluetooth hardware.
 *
 * Replace this implementation with a hardware-specific one when
 * integrating with a real vehicle's Bluetooth module.
 */
class SimulatedBluetoothAudioHAL : BluetoothAudioHAL {

    private val _audioStateFlow = MutableStateFlow(BluetoothAudioState())
    override val audioStateFlow: Flow<BluetoothAudioState> = _audioStateFlow.asStateFlow()

    private val _mediaMetadataFlow = MutableStateFlow(BluetoothMediaMetadata())
    override val mediaMetadataFlow: Flow<BluetoothMediaMetadata> = _mediaMetadataFlow.asStateFlow()

    private var isInitialized = false
    private val pairedDevices = mutableListOf<BluetoothDevice>()
    private var connectedDevice: BluetoothDevice? = null

    // Simulated discoverable devices
    private val simulatedDevices = listOf(
        BluetoothDevice("AA:BB:CC:DD:EE:01", "iPhone 15 Pro"),
        BluetoothDevice("AA:BB:CC:DD:EE:02", "Samsung Galaxy S24"),
        BluetoothDevice("AA:BB:CC:DD:EE:03", "Google Pixel 8")
    )

    override suspend fun initialize(): Boolean {
        delay(300)
        isInitialized = true
        return true
    }

    override fun startDiscovery(): Flow<BluetoothDevice> = flow {
        if (!isInitialized) return@flow

        for (device in simulatedDevices) {
            delay(800) // Simulate discovery time
            emit(device)
        }

        _audioStateFlow.value = _audioStateFlow.value.copy(
            availableDevices = simulatedDevices
        )
    }

    override suspend fun stopDiscovery() {
        // No-op in simulation
    }

    override suspend fun pair(address: String): Boolean {
        if (!isInitialized) return false

        delay(1500) // Simulate pairing process
        val device = simulatedDevices.find { it.address == address } ?: return false
        val pairedDevice = device.copy(isPaired = true)
        pairedDevices.add(pairedDevice)
        return true
    }

    override suspend fun connect(address: String): Boolean {
        if (!isInitialized) return false

        val device = pairedDevices.find { it.address == address }
            ?: simulatedDevices.find { it.address == address }
            ?: return false

        delay(1000) // Simulate connection time

        connectedDevice = device.copy(
            isConnected = true,
            connectionState = BluetoothConnectionState.CONNECTED
        )

        _audioStateFlow.value = _audioStateFlow.value.copy(
            connectedDevice = connectedDevice,
            connectionState = BluetoothConnectionState.CONNECTED
        )

        return true
    }

    override suspend fun disconnect(): Boolean {
        connectedDevice = null
        _audioStateFlow.value = _audioStateFlow.value.copy(
            connectedDevice = null,
            connectionState = BluetoothConnectionState.DISCONNECTED,
            currentMedia = null
        )
        _mediaMetadataFlow.value = BluetoothMediaMetadata()
        return true
    }

    override suspend fun getPairedDevices(): List<BluetoothDevice> {
        return pairedDevices.toList()
    }

    override suspend fun play() {
        if (connectedDevice == null) return

        _audioStateFlow.value = _audioStateFlow.value.copy(
            connectionState = BluetoothConnectionState.PLAYING
        )

        // Simulate media metadata
        _mediaMetadataFlow.value = BluetoothMediaMetadata(
            title = "Simulated Track",
            artist = "Test Artist",
            album = "Test Album",
            duration = 240000L,
            position = 0L
        )
    }

    override suspend fun pause() {
        _audioStateFlow.value = _audioStateFlow.value.copy(
            connectionState = BluetoothConnectionState.PAUSED
        )
    }

    override suspend fun nextTrack() {
        _mediaMetadataFlow.value = BluetoothMediaMetadata(
            title = "Next Simulated Track",
            artist = "Test Artist",
            album = "Test Album",
            duration = 195000L,
            position = 0L
        )
    }

    override suspend fun previousTrack() {
        _mediaMetadataFlow.value = BluetoothMediaMetadata(
            title = "Previous Simulated Track",
            artist = "Test Artist",
            album = "Test Album",
            duration = 210000L,
            position = 0L
        )
    }

    override suspend fun setVolume(volume: Int) {
        _audioStateFlow.value = _audioStateFlow.value.copy(
            volume = volume.coerceIn(0, 100)
        )
    }

    override suspend fun shutdown() {
        disconnect()
        isInitialized = false
    }
}
