import React, { useState } from 'react';
import { BluetoothService } from '../services/BluetoothService';

interface BluetoothDevice {
  address: string;
  name: string;
  isConnected: boolean;
}

/**
 * Bluetooth audio panel for phone connectivity and media playback control.
 */
function BluetoothPanel() {
  const [connectedDevice, setConnectedDevice] = useState<BluetoothDevice | null>(null);
  const [connectionStatus, setConnectionStatus] = useState('Disconnected');
  const [mediaTitle, setMediaTitle] = useState('');
  const [mediaArtist, setMediaArtist] = useState('');
  const [isDiscovering, setIsDiscovering] = useState(false);

  const handleConnect = async () => {
    setIsDiscovering(true);
    setConnectionStatus('Discovering...');
    const devices = await BluetoothService.discover();
    if (devices.length > 0) {
      setConnectionStatus('Connecting...');
      const connected = await BluetoothService.connect(devices[0].address);
      if (connected) {
        setConnectedDevice(devices[0]);
        setConnectionStatus('Connected');
      } else {
        setConnectionStatus('Connection Failed');
      }
    }
    setIsDiscovering(false);
  };

  const handleDisconnect = async () => {
    await BluetoothService.disconnect();
    setConnectedDevice(null);
    setConnectionStatus('Disconnected');
    setMediaTitle('');
    setMediaArtist('');
  };

  const handlePlay = () => BluetoothService.play();
  const handlePause = () => BluetoothService.pause();
  const handleNext = () => BluetoothService.nextTrack();
  const handlePrevious = () => BluetoothService.previousTrack();

  return (
    <div className="bluetooth-panel">
      <div className="bt-info">
        <span className="bt-device">
          {connectedDevice?.name || 'No device'}
        </span>
        <span className="bt-status">{connectionStatus}</span>
      </div>

      {connectedDevice && (
        <div className="media-info">
          <span className="media-title">{mediaTitle}</span>
          <span className="media-artist">{mediaArtist}</span>
        </div>
      )}

      <div className="bt-media-controls">
        <button className="control-btn" onClick={handlePrevious}>
          &#9664;&#9664;
        </button>
        <button className="control-btn play-btn" onClick={handlePlay}>
          &#9654;
        </button>
        <button className="control-btn pause-btn" onClick={handlePause}>
          &#10074;&#10074;
        </button>
        <button className="control-btn" onClick={handleNext}>
          &#9654;&#9654;
        </button>
      </div>

      <div className="bt-connection-controls">
        <button
          className="connect-btn"
          onClick={handleConnect}
          disabled={isDiscovering}
        >
          {isDiscovering ? 'Discovering...' : 'Connect'}
        </button>
        <button className="disconnect-btn" onClick={handleDisconnect}>
          Disconnect
        </button>
      </div>
    </div>
  );
}

export default BluetoothPanel;
