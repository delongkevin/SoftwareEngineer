package com.infotainment.radio.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.infotainment.radio.R
import com.infotainment.radio.databinding.ActivityMainBinding
import com.infotainment.radio.model.AudioSource
import com.infotainment.radio.model.BluetoothConnectionState
import com.infotainment.radio.model.RadioBand
import kotlinx.coroutines.launch

/**
 * Main Activity for the Infotainment Radio application.
 * Displays radio tuner controls and Bluetooth audio interface in landscape mode.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RadioViewModel by viewModels { RadioViewModel.Factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSourceButtons()
        setupRadioControls()
        setupBluetoothControls()
        observeState()
    }

    private fun setupSourceButtons() {
        binding.btnFm.setOnClickListener { viewModel.switchToFM() }
        binding.btnAm.setOnClickListener { viewModel.switchToAM() }
        binding.btnBluetooth.setOnClickListener { viewModel.switchToBluetooth() }
    }

    private fun setupRadioControls() {
        binding.btnSeekUp.setOnClickListener { viewModel.seekUp() }
        binding.btnSeekDown.setOnClickListener { viewModel.seekDown() }
        binding.btnScan.setOnClickListener { viewModel.scanAll() }
        binding.btnMute.setOnClickListener { viewModel.toggleMute() }

        binding.volumeSeekBar.setOnSeekBarChangeListener(
            object : android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) viewModel.setRadioVolume(progress)
                }
                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
            }
        )
    }

    private fun setupBluetoothControls() {
        binding.btnBtConnect.setOnClickListener { viewModel.startBluetoothDiscovery() }
        binding.btnBtDisconnect.setOnClickListener { viewModel.disconnectBluetooth() }
        binding.btnBtPlay.setOnClickListener { viewModel.btPlay() }
        binding.btnBtPause.setOnClickListener { viewModel.btPause() }
        binding.btnBtNext.setOnClickListener { viewModel.btNext() }
        binding.btnBtPrevious.setOnClickListener { viewModel.btPrevious() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.activeSource.collect { source ->
                        updateSourceUI(source)
                    }
                }

                launch {
                    viewModel.radioState.collect { state ->
                        binding.tvFrequency.text = when (state.band) {
                            RadioBand.FM -> String.format("%.1f FM", state.currentStation?.frequency ?: 87.5)
                            RadioBand.AM -> String.format("%.0f AM", state.currentStation?.frequency ?: 530.0)
                        }
                        binding.tvStationName.text = state.currentStation?.stationName ?: ""
                        binding.tvSignalStrength.text = "Signal: ${state.currentStation?.signalStrength ?: 0}%"
                        binding.volumeSeekBar.progress = state.volume
                        binding.btnMute.text = if (state.isMuted) "Unmute" else "Mute"
                        binding.tvScanStatus.text = if (state.isScanning) "Scanning..." else ""
                    }
                }

                launch {
                    viewModel.bluetoothState.collect { state ->
                        binding.tvBtDevice.text = state.connectedDevice?.name ?: "No device"
                        binding.tvBtStatus.text = when (state.connectionState) {
                            BluetoothConnectionState.DISCONNECTED -> "Disconnected"
                            BluetoothConnectionState.CONNECTING -> "Connecting..."
                            BluetoothConnectionState.CONNECTED -> "Connected"
                            BluetoothConnectionState.PLAYING -> "Playing"
                            BluetoothConnectionState.PAUSED -> "Paused"
                        }
                    }
                }

                launch {
                    viewModel.mediaMetadata.collect { metadata ->
                        binding.tvMediaTitle.text = metadata.title ?: ""
                        binding.tvMediaArtist.text = metadata.artist ?: ""
                    }
                }
            }
        }
    }

    private fun updateSourceUI(source: AudioSource) {
        binding.btnFm.isSelected = source == AudioSource.FM_RADIO
        binding.btnAm.isSelected = source == AudioSource.AM_RADIO
        binding.btnBluetooth.isSelected = source == AudioSource.BLUETOOTH

        val isRadio = source == AudioSource.FM_RADIO || source == AudioSource.AM_RADIO
        binding.radioPanel.visibility = if (isRadio) android.view.View.VISIBLE else android.view.View.GONE
        binding.bluetoothPanel.visibility = if (source == AudioSource.BLUETOOTH) android.view.View.VISIBLE else android.view.View.GONE
    }
}
