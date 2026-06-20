import { registerPlugin } from '@capacitor/core';

export interface NatureSensorsPlugin {
  /**
   * Fetches atmospheric pressure (hPa/mbar) via native Barometer sensor.
   * Vital for offline altitude tracking and local weather prediction in the wild.
   */
  getAtmosphericPressure(): Promise<{ pressure: number }>;
}

const NatureSensors = registerPlugin<NatureSensorsPlugin>('NatureSensors');

export default NatureSensors;
