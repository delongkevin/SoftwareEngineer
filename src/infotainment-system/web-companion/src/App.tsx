import React, { useState } from 'react';
import RadioPanel from './components/RadioPanel';
import BluetoothPanel from './components/BluetoothPanel';

type AudioSource = 'fm' | 'am' | 'bluetooth';

/**
 * Main application component for the Infotainment Web Companion.
 * Provides a web-based interface that mirrors the Android app's functionality
 * and can communicate with the infotainment backend via WebSocket/REST API.
 */
function App() {
  const [activeSource, setActiveSource] = useState<AudioSource>('fm');

  return (
    <div className="app">
      <header className="source-bar">
        <button
          className={`source-btn ${activeSource === 'fm' ? 'active' : ''}`}
          onClick={() => setActiveSource('fm')}
        >
          FM
        </button>
        <button
          className={`source-btn ${activeSource === 'am' ? 'active' : ''}`}
          onClick={() => setActiveSource('am')}
        >
          AM
        </button>
        <button
          className={`source-btn ${activeSource === 'bluetooth' ? 'active' : ''}`}
          onClick={() => setActiveSource('bluetooth')}
        >
          BT
        </button>
      </header>

      <main className="content">
        {(activeSource === 'fm' || activeSource === 'am') && (
          <RadioPanel band={activeSource} />
        )}
        {activeSource === 'bluetooth' && <BluetoothPanel />}
      </main>

      <footer className="volume-bar">
        <VolumeControl />
      </footer>
    </div>
  );
}

function VolumeControl() {
  const [volume, setVolume] = useState(50);
  const [isMuted, setIsMuted] = useState(false);

  return (
    <div className="volume-control">
      <button
        className="mute-btn"
        onClick={() => setIsMuted(!isMuted)}
      >
        {isMuted ? 'Unmute' : 'Mute'}
      </button>
      <input
        type="range"
        min="0"
        max="100"
        value={volume}
        onChange={(e) => setVolume(Number(e.target.value))}
        className="volume-slider"
      />
      <span className="volume-value">{volume}%</span>
    </div>
  );
}

export default App;
