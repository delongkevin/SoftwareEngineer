/**
 * Bluetooth service for communicating with the infotainment backend.
 *
 * In development mode, this uses simulated responses.
 * In production, this connects to the Android app's WebSocket server
 * or REST API for Bluetooth device management.
 */

interface BluetoothDeviceInfo {
  address: string;
  name: string;
  isConnected: boolean;
}

// Backend API base URL
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Simulated devices for development
const SIMULATED_DEVICES: BluetoothDeviceInfo[] = [
  { address: 'AA:BB:CC:DD:EE:01', name: 'iPhone 15 Pro', isConnected: false },
  { address: 'AA:BB:CC:DD:EE:02', name: 'Samsung Galaxy S24', isConnected: false },
  { address: 'AA:BB:CC:DD:EE:03', name: 'Google Pixel 8', isConnected: false },
];

export const BluetoothService = {
  /**
   * Discover available Bluetooth devices.
   */
  async discover(): Promise<BluetoothDeviceInfo[]> {
    // TODO: Replace with actual API call
    // return fetch(`${API_BASE_URL}/bluetooth/discover`).then(res => res.json());

    await simulateDelay(2000);
    return SIMULATED_DEVICES;
  },

  /**
   * Connect to a Bluetooth device by address.
   */
  async connect(address: string): Promise<boolean> {
    // TODO: Replace with actual API call
    // return fetch(`${API_BASE_URL}/bluetooth/connect`, {
    //   method: 'POST', body: JSON.stringify({ address })
    // }).then(res => res.json());

    await simulateDelay(1500);
    return true;
  },

  /**
   * Disconnect from the current Bluetooth device.
   */
  async disconnect(): Promise<boolean> {
    await simulateDelay(500);
    return true;
  },

  /**
   * Send play command.
   */
  async play(): Promise<void> {
    await simulateDelay(100);
  },

  /**
   * Send pause command.
   */
  async pause(): Promise<void> {
    await simulateDelay(100);
  },

  /**
   * Send next track command.
   */
  async nextTrack(): Promise<void> {
    await simulateDelay(100);
  },

  /**
   * Send previous track command.
   */
  async previousTrack(): Promise<void> {
    await simulateDelay(100);
  },
};

function simulateDelay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}
