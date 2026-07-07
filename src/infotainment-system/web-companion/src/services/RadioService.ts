/**
 * Radio service for communicating with the infotainment backend.
 *
 * In development mode, this uses simulated responses.
 * In production, this connects to the Android app's WebSocket server
 * or REST API endpoint running on the infotainment system.
 */

interface StationResult {
  frequency: number;
  band: 'fm' | 'am';
  signalStrength: number;
  stationName: string | null;
}

// Backend API base URL - configure based on environment
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Simulated stations for development
const SIMULATED_FM_STATIONS: StationResult[] = [
  { frequency: 88.1, band: 'fm', signalStrength: 85, stationName: 'Rock FM' },
  { frequency: 91.5, band: 'fm', signalStrength: 72, stationName: 'Jazz 91.5' },
  { frequency: 94.7, band: 'fm', signalStrength: 90, stationName: 'Top 40' },
  { frequency: 97.3, band: 'fm', signalStrength: 65, stationName: 'Classic Hits' },
  { frequency: 100.1, band: 'fm', signalStrength: 78, stationName: 'News Radio' },
  { frequency: 103.5, band: 'fm', signalStrength: 88, stationName: 'Country' },
  { frequency: 106.9, band: 'fm', signalStrength: 55, stationName: 'Indie FM' },
];

const SIMULATED_AM_STATIONS: StationResult[] = [
  { frequency: 680, band: 'am', signalStrength: 70, stationName: 'AM News' },
  { frequency: 880, band: 'am', signalStrength: 60, stationName: 'Sports Talk' },
  { frequency: 1010, band: 'am', signalStrength: 75, stationName: 'All News' },
  { frequency: 1200, band: 'am', signalStrength: 50, stationName: 'Talk Radio' },
  { frequency: 1490, band: 'am', signalStrength: 45, stationName: 'Oldies AM' },
];

const currentStationIndex: Record<string, number> = { fm: 0, am: 0 };

export const RadioService = {
  /**
   * Seek to the next station up in frequency.
   */
  async seekUp(band: 'fm' | 'am'): Promise<StationResult | null> {
    // TODO: Replace with actual API call when backend is ready
    // return fetch(`${API_BASE_URL}/radio/seek?direction=up&band=${band}`)
    //   .then(res => res.json());

    const stations = band === 'fm' ? SIMULATED_FM_STATIONS : SIMULATED_AM_STATIONS;
    currentStationIndex[band] = (currentStationIndex[band] + 1) % stations.length;
    await simulateDelay(300);
    return stations[currentStationIndex[band]];
  },

  /**
   * Seek to the next station down in frequency.
   */
  async seekDown(band: 'fm' | 'am'): Promise<StationResult | null> {
    const stations = band === 'fm' ? SIMULATED_FM_STATIONS : SIMULATED_AM_STATIONS;
    currentStationIndex[band] = currentStationIndex[band] > 0 ? currentStationIndex[band] - 1 : stations.length - 1;
    await simulateDelay(300);
    return stations[currentStationIndex[band]];
  },

  /**
   * Scan all available stations.
   */
  async scanAll(band: 'fm' | 'am'): Promise<StationResult[]> {
    await simulateDelay(1500);
    return band === 'fm' ? SIMULATED_FM_STATIONS : SIMULATED_AM_STATIONS;
  },

  /**
   * Tune to a specific frequency.
   */
  async tuneTo(frequency: number, band: 'fm' | 'am'): Promise<StationResult | null> {
    const stations = band === 'fm' ? SIMULATED_FM_STATIONS : SIMULATED_AM_STATIONS;
    const found = stations.find(s => s.frequency === frequency);
    await simulateDelay(100);
    return found || { frequency, band, signalStrength: 15, stationName: null };
  },
};

function simulateDelay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}
