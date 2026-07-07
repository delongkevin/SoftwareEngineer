package com.infotainment.radio.service

import com.infotainment.radio.hardware.BluetoothAudioHAL
import com.infotainment.radio.model.BluetoothAudioState
import com.infotainment.radio.model.BluetoothConnectionState
import com.infotainment.radio.model.BluetoothDevice
import com.infotainment.radio.model.BluetoothMediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Service that manages Bluetooth audio operations for phone connectivity.
 * Handles device discovery, pairing, connection, and media playback control.
 */
class BluetoothAudioService(private val bluetoothHAL: BluetoothAudioHAL) {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _audioState = MutableStateFlow(BluetoothAudioState())
    val audioState: StateFlow<BluetoothAudioState> = _audioState.asStateFlow()

    private val _mediaMetadata = MutableStateFlow(BluetoothMediaMetadata())
    val mediaMetadata: StateFlow<BluetoothMediaMetadata> = _mediaMetadata.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    suspend fun initialize(): Boolean {
        val result = bluetoothHAL.initialize()
        if (result) {
            observeStateChanges()
        }
        return result
    }

    private fun observeStateChanges() {
        serviceScope.launch {
            bluetoothHAL.audioStateFlow
                .catch { /* Log error */ }
                .collect { state ->
                    _audioState.value = state
                }
        }
        serviceScope.launch {
            bluetoothHAL.mediaMetadataFlow
                .catch { /* Log error */ }
                .collect { metadata ->
                    _mediaMetadata.value = metadata
                }
        }
    }

    suspend fun startDiscovery() {
        _isDiscovering.value = true
        val devices = mutableListOf<BluetoothDevice>()
        bluetoothHAL.startDiscovery()
            .catch {
                _isDiscovering.value = false
            }
            .collect { device ->
                devices.add(device)
                _discoveredDevices.value = devices.toList()
            }
        _isDiscovering.value = false
    }

    suspend fun stopDiscovery() {
        bluetoothHAL.stopDiscovery()
        _isDiscovering.value = false
    }

    suspend fun pairDevice(address: String): Boolean {
        return bluetoothHAL.pair(address)
    }

    suspend fun connectDevice(address: String): Boolean {
        _audioState.value = _audioState.value.copy(
            connectionState = BluetoothConnectionState.CONNECTING
        )
        return bluetoothHAL.connect(address)
    }

    suspend fun disconnectDevice(): Boolean {
        return bluetoothHAL.disconnect()
    }

    suspend fun getPairedDevices(): List<BluetoothDevice> {
        return bluetoothHAL.getPairedDevices()
    }

    suspend fun play() {
        bluetoothHAL.play()
    }

    suspend fun pause() {
        bluetoothHAL.pause()
    }

    suspend fun nextTrack() {
        bluetoothHAL.nextTrack()
    }

    suspend fun previousTrack() {
        bluetoothHAL.previousTrack()
    }

    suspend fun setVolume(volume: Int) {
        bluetoothHAL.setVolume(volume.coerceIn(0, 100))
    }

    fun isConnected(): Boolean {
        return _audioState.value.connectionState == BluetoothConnectionState.CONNECTED ||
               _audioState.value.connectionState == BluetoothConnectionState.PLAYING ||
               _audioState.value.connectionState == BluetoothConnectionState.PAUSED
    }

    suspend fun shutdown() {
        bluetoothHAL.shutdown()
    }
}
