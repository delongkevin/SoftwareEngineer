package com.infotainment.radio.model

/**
 * Represents the connection state of a Bluetooth device.
 */
enum class BluetoothConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    PLAYING,
    PAUSED
}

/**
 * Represents a paired Bluetooth device.
 */
data class BluetoothDevice(
    val address: String,
    val name: String,
    val isPaired: Boolean = false,
    val isConnected: Boolean = false,
    val connectionState: BluetoothConnectionState = BluetoothConnectionState.DISCONNECTED
)

/**
 * Represents media metadata from a Bluetooth audio source.
 */
data class BluetoothMediaMetadata(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val duration: Long = 0L,
    val position: Long = 0L
)

/**
 * Represents the overall Bluetooth audio state.
 */
data class BluetoothAudioState(
    val connectedDevice: BluetoothDevice? = null,
    val connectionState: BluetoothConnectionState = BluetoothConnectionState.DISCONNECTED,
    val currentMedia: BluetoothMediaMetadata? = null,
    val availableDevices: List<BluetoothDevice> = emptyList(),
    val volume: Int = 50  // 0-100
)
