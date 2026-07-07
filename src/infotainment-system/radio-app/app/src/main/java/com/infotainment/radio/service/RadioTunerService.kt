package com.infotainment.radio.service

import com.infotainment.radio.hardware.RadioTunerHAL
import com.infotainment.radio.model.RadioBand
import com.infotainment.radio.model.RadioFrequencyRange
import com.infotainment.radio.model.RadioState
import com.infotainment.radio.model.RadioStation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Service that manages radio tuner operations.
 * Acts as a bridge between the UI layer and the hardware abstraction layer.
 */
class RadioTunerService(private val tunerHAL: RadioTunerHAL) {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _radioState = MutableStateFlow(RadioState())
    val radioState: StateFlow<RadioState> = _radioState.asStateFlow()

    private val _scanResults = MutableStateFlow<List<RadioStation>>(emptyList())
    val scanResults: StateFlow<List<RadioStation>> = _scanResults.asStateFlow()

    suspend fun initialize(): Boolean {
        val result = tunerHAL.initialize()
        if (result) {
            observeTunerState()
        }
        return result
    }

    private fun observeTunerState() {
        serviceScope.launch {
            tunerHAL.tunerStateFlow
                .catch { /* Log error in production */ }
                .collect { station ->
                    _radioState.value = _radioState.value.copy(currentStation = station)
                }
        }
    }

    suspend fun tuneTo(frequency: Double) {
        val band = _radioState.value.band
        val clampedFreq = clampFrequency(frequency, band)
        tunerHAL.tuneTo(clampedFreq, band)
    }

    suspend fun switchBand(band: RadioBand) {
        _radioState.value = _radioState.value.copy(band = band)
        val startFreq = if (band == RadioBand.FM) RadioFrequencyRange.FM_MIN
            else RadioFrequencyRange.AM_MIN
        tunerHAL.tuneTo(startFreq, band)
    }

    suspend fun seekUp() {
        _radioState.value = _radioState.value.copy(isScanning = true)
        tunerHAL.seek(seekUp = true, band = _radioState.value.band)
            .catch { _radioState.value = _radioState.value.copy(isScanning = false) }
            .collect { station ->
                _radioState.value = _radioState.value.copy(
                    currentStation = station,
                    isScanning = false
                )
            }
    }

    suspend fun seekDown() {
        _radioState.value = _radioState.value.copy(isScanning = true)
        tunerHAL.seek(seekUp = false, band = _radioState.value.band)
            .catch { _radioState.value = _radioState.value.copy(isScanning = false) }
            .collect { station ->
                _radioState.value = _radioState.value.copy(
                    currentStation = station,
                    isScanning = false
                )
            }
    }

    suspend fun scanAll() {
        _radioState.value = _radioState.value.copy(isScanning = true)
        val stations = mutableListOf<RadioStation>()
        tunerHAL.scanAll(_radioState.value.band)
            .catch { _radioState.value = _radioState.value.copy(isScanning = false) }
            .collect { station ->
                stations.add(station)
                _scanResults.value = stations.toList()
            }
        _radioState.value = _radioState.value.copy(isScanning = false)
    }

    suspend fun setVolume(volume: Int) {
        val clampedVolume = volume.coerceIn(0, 100)
        tunerHAL.setVolume(clampedVolume)
        _radioState.value = _radioState.value.copy(volume = clampedVolume)
    }

    suspend fun toggleMute() {
        val newMuteState = !_radioState.value.isMuted
        tunerHAL.setMute(newMuteState)
        _radioState.value = _radioState.value.copy(isMuted = newMuteState)
    }

    suspend fun addPreset(station: RadioStation) {
        val currentPresets = _radioState.value.presets.toMutableList()
        if (currentPresets.none { it.frequency == station.frequency && it.band == station.band }) {
            currentPresets.add(station.copy(isFavorite = true))
            _radioState.value = _radioState.value.copy(presets = currentPresets)
        }
    }

    suspend fun removePreset(station: RadioStation) {
        val currentPresets = _radioState.value.presets.toMutableList()
        currentPresets.removeAll { it.frequency == station.frequency && it.band == station.band }
        _radioState.value = _radioState.value.copy(presets = currentPresets)
    }

    suspend fun shutdown() {
        serviceScope.cancel()
        tunerHAL.shutdown()
    }

    private fun clampFrequency(frequency: Double, band: RadioBand): Double {
        return when (band) {
            RadioBand.FM -> frequency.coerceIn(RadioFrequencyRange.FM_MIN, RadioFrequencyRange.FM_MAX)
            RadioBand.AM -> frequency.coerceIn(RadioFrequencyRange.AM_MIN, RadioFrequencyRange.AM_MAX)
        }
    }
}
