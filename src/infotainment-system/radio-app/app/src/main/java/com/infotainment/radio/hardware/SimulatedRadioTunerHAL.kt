package com.infotainment.radio.hardware

import com.infotainment.radio.model.RadioBand
import com.infotainment.radio.model.RadioFrequencyRange
import com.infotainment.radio.model.RadioStation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Software simulation of the Radio Tuner HAL for development and testing.
 *
 * This implementation simulates radio tuner behavior without requiring
 * actual hardware. It generates simulated stations and signal strengths
 * for development purposes.
 *
 * Replace this implementation with a hardware-specific one when
 * integrating with a real vehicle's radio tuner module.
 */
class SimulatedRadioTunerHAL : RadioTunerHAL {

    private val _tunerStateFlow = MutableStateFlow<RadioStation?>(null)
    override val tunerStateFlow: Flow<RadioStation?> = _tunerStateFlow.asStateFlow()

    private var currentVolume = 50
    private var isMuted = false
    private var isInitialized = false

    // Simulated FM stations with signal strengths
    private val simulatedFMStations = listOf(
        RadioStation(88.1, RadioBand.FM, 85, "Rock FM"),
        RadioStation(91.5, RadioBand.FM, 72, "Jazz 91.5"),
        RadioStation(94.7, RadioBand.FM, 90, "Top 40"),
        RadioStation(97.3, RadioBand.FM, 65, "Classic Hits"),
        RadioStation(100.1, RadioBand.FM, 78, "News Radio"),
        RadioStation(103.5, RadioBand.FM, 88, "Country"),
        RadioStation(106.9, RadioBand.FM, 55, "Indie FM")
    )

    // Simulated AM stations
    private val simulatedAMStations = listOf(
        RadioStation(680.0, RadioBand.AM, 70, "AM News"),
        RadioStation(880.0, RadioBand.AM, 60, "Sports Talk"),
        RadioStation(1010.0, RadioBand.AM, 75, "All News"),
        RadioStation(1200.0, RadioBand.AM, 50, "Talk Radio"),
        RadioStation(1490.0, RadioBand.AM, 45, "Oldies AM")
    )

    override suspend fun initialize(): Boolean {
        delay(500) // Simulate hardware init time
        isInitialized = true
        return true
    }

    override suspend fun tuneTo(frequency: Double, band: RadioBand): RadioStation? {
        if (!isInitialized) return null

        delay(100) // Simulate tuning delay

        val stations = if (band == RadioBand.FM) simulatedFMStations else simulatedAMStations
        val station = stations.find { it.frequency == frequency }
            ?: RadioStation(frequency, band, (10..30).random(), null)

        _tunerStateFlow.value = station
        return station
    }

    override fun seek(seekUp: Boolean, band: RadioBand): Flow<RadioStation> = flow {
        if (!isInitialized) return@flow

        val stations = if (band == RadioBand.FM) simulatedFMStations else simulatedAMStations
        val currentFreq = _tunerStateFlow.value?.frequency ?: getMinFreq(band)

        val sortedStations = if (seekUp) {
            stations.filter { it.frequency > currentFreq }.sortedBy { it.frequency }
        } else {
            stations.filter { it.frequency < currentFreq }.sortedByDescending { it.frequency }
        }

        if (sortedStations.isNotEmpty()) {
            delay(300) // Simulate seek time
            val found = sortedStations.first()
            _tunerStateFlow.value = found
            emit(found)
        } else {
            // Wrap around
            val wrapped = if (seekUp) stations.minByOrNull { it.frequency }
                else stations.maxByOrNull { it.frequency }
            if (wrapped != null) {
                delay(500)
                _tunerStateFlow.value = wrapped
                emit(wrapped)
            }
        }
    }

    override fun scanAll(band: RadioBand): Flow<RadioStation> = flow {
        if (!isInitialized) return@flow

        val stations = if (band == RadioBand.FM) simulatedFMStations else simulatedAMStations
        for (station in stations.sortedBy { it.frequency }) {
            delay(200) // Simulate scan progress
            emit(station)
        }
    }

    override suspend fun getSignalStrength(): Int {
        return _tunerStateFlow.value?.signalStrength ?: 0
    }

    override suspend fun setVolume(volume: Int) {
        currentVolume = volume.coerceIn(0, 100)
    }

    override suspend fun setMute(mute: Boolean) {
        isMuted = mute
    }

    override suspend fun shutdown() {
        _tunerStateFlow.value = null
        isInitialized = false
    }

    private fun getMinFreq(band: RadioBand): Double {
        return if (band == RadioBand.FM) RadioFrequencyRange.FM_MIN
        else RadioFrequencyRange.AM_MIN
    }
}
