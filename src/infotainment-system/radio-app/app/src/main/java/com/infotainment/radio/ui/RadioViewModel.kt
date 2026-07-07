package com.infotainment.radio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.infotainment.radio.hardware.SimulatedBluetoothAudioHAL
import com.infotainment.radio.hardware.SimulatedRadioTunerHAL
import com.infotainment.radio.model.AudioSource
import com.infotainment.radio.model.BluetoothAudioState
import com.infotainment.radio.model.BluetoothMediaMetadata
import com.infotainment.radio.model.RadioBand
import com.infotainment.radio.model.RadioState
import com.infotainment.radio.model.RadioStation
import com.infotainment.radio.service.BluetoothAudioService
import com.infotainment.radio.service.RadioTunerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel managing the infotainment radio UI state.
 * Coordinates between RadioTunerService and BluetoothAudioService.
 */
class RadioViewModel(
    private val radioService: RadioTunerService,
    private val bluetoothService: BluetoothAudioService
) : ViewModel() {

    private val _activeSource = MutableStateFlow(AudioSource.FM_RADIO)
    val activeSource: StateFlow<AudioSource> = _activeSource.asStateFlow()

    val radioState: StateFlow<RadioState> = radioService.radioState
    val scanResults: StateFlow<List<RadioStation>> = radioService.scanResults
    val bluetoothState: StateFlow<BluetoothAudioState> = bluetoothService.audioState
    val mediaMetadata: StateFlow<BluetoothMediaMetadata> = bluetoothService.mediaMetadata

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            val radioInit = radioService.initialize()
            val btInit = bluetoothService.initialize()
            _isInitialized.value = radioInit && btInit
        }
    }

    // --- Audio Source ---

    fun switchToFM() {
        viewModelScope.launch {
            _activeSource.value = AudioSource.FM_RADIO
            radioService.switchBand(RadioBand.FM)
        }
    }

    fun switchToAM() {
        viewModelScope.launch {
            _activeSource.value = AudioSource.AM_RADIO
            radioService.switchBand(RadioBand.AM)
        }
    }

    fun switchToBluetooth() {
        _activeSource.value = AudioSource.BLUETOOTH
    }

    // --- Radio Controls ---

    fun tuneTo(frequency: Double) {
        viewModelScope.launch {
            radioService.tuneTo(frequency)
        }
    }

    fun seekUp() {
        viewModelScope.launch {
            radioService.seekUp()
        }
    }

    fun seekDown() {
        viewModelScope.launch {
            radioService.seekDown()
        }
    }

    fun scanAll() {
        viewModelScope.launch {
            radioService.scanAll()
        }
    }

    fun setRadioVolume(volume: Int) {
        viewModelScope.launch {
            radioService.setVolume(volume)
        }
    }

    fun toggleMute() {
        viewModelScope.launch {
            radioService.toggleMute()
        }
    }

    fun addPreset(station: RadioStation) {
        viewModelScope.launch {
            radioService.addPreset(station)
        }
    }

    fun removePreset(station: RadioStation) {
        viewModelScope.launch {
            radioService.removePreset(station)
        }
    }

    // --- Bluetooth Controls ---

    fun startBluetoothDiscovery() {
        viewModelScope.launch {
            bluetoothService.startDiscovery()
        }
    }

    fun connectBluetoothDevice(address: String) {
        viewModelScope.launch {
            bluetoothService.connectDevice(address)
        }
    }

    fun disconnectBluetooth() {
        viewModelScope.launch {
            bluetoothService.disconnectDevice()
        }
    }

    fun btPlay() {
        viewModelScope.launch { bluetoothService.play() }
    }

    fun btPause() {
        viewModelScope.launch { bluetoothService.pause() }
    }

    fun btNext() {
        viewModelScope.launch { bluetoothService.nextTrack() }
    }

    fun btPrevious() {
        viewModelScope.launch { bluetoothService.previousTrack() }
    }

    // --- Lifecycle ---

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            withContext(NonCancellable) {
                radioService.shutdown()
                bluetoothService.shutdown()
            }
        }
    }

    /**
     * Factory for creating RadioViewModel with simulated HAL implementations.
     * Replace SimulatedXxxHAL with real hardware HAL implementations when
     * deploying to actual vehicle hardware.
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val radioHAL = SimulatedRadioTunerHAL()
            val bluetoothHAL = SimulatedBluetoothAudioHAL()
            val radioService = RadioTunerService(radioHAL)
            val bluetoothService = BluetoothAudioService(bluetoothHAL)
            return RadioViewModel(radioService, bluetoothService) as T
        }
    }
}
