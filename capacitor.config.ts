import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.soodora.app',
  appName: 'Soodora',
  webDir: 'dist',
  server: {
    allowNavigation: ['soodora.com'],
  },
};

export default config;
