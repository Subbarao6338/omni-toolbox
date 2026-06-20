import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.omniweb.app',
  appName: 'Omni Web',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
};

export default config;
