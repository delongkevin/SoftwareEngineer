import React, { useState } from 'react';
import { RadioService } from '../services/RadioService';

interface RadioPanelProps {
  band: 'fm' | 'am';
}

/**
 * Radio tuner panel component displaying frequency, station info, and controls.
 */
function RadioPanel({ band }: RadioPanelProps) {
  const [frequency, setFrequency] = useState(band === 'fm' ? 87.5 : 530);
  const [stationName, setStationName] = useState('');
  const [signalStrength, setSignalStrength] = useState(0);
  const [isScanning, setIsScanning] = useState(false);

  const handleSeekUp = async () => {
    setIsScanning(true);
    const result = await RadioService.seekUp(band);
    if (result) {
      setFrequency(result.frequency);
      setStationName(result.stationName || '');
      setSignalStrength(result.signalStrength);
    }
    setIsScanning(false);
  };

  const handleSeekDown = async () => {
    setIsScanning(true);
    const result = await RadioService.seekDown(band);
    if (result) {
      setFrequency(result.frequency);
      setStationName(result.stationName || '');
      setSignalStrength(result.signalStrength);
    }
    setIsScanning(false);
  };

  const handleScan = async () => {
    setIsScanning(true);
    await RadioService.scanAll(band);
    setIsScanning(false);
  };

  const formatFrequency = () => {
    if (band === 'fm') {
      return `${frequency.toFixed(1)} FM`;
    }
    return `${frequency.toFixed(0)} AM`;
  };

  return (
    <div className="radio-panel">
      <div className="frequency-display">
        <span className="frequency">{formatFrequency()}</span>
        {stationName && <span className="station-name">{stationName}</span>}
        <span className="signal">Signal: {signalStrength}%</span>
        {isScanning && <span className="scanning">Scanning...</span>}
      </div>

      <div className="radio-controls">
        <button className="control-btn" onClick={handleSeekDown}>
          &#9664;&#9664;
        </button>
        <button className="control-btn scan-btn" onClick={handleScan}>
          SCAN
        </button>
        <button className="control-btn" onClick={handleSeekUp}>
          &#9654;&#9654;
        </button>
      </div>
    </div>
  );
}

export default RadioPanel;
