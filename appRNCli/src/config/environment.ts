import {Platform} from 'react-native';

/**
 * Backend API configuration
 *
 * Android Emulator: Use 10.0.2.2 to access host machine's localhost
 * iOS Simulator: Use localhost
 * Physical device: Use LAN IP address of development machine
 */
const getBaseUrl = (): string => {
  if (__DEV__) {
    // Development environment
    if (Platform.OS === 'android') {
      return 'http://10.0.2.2:8080';
    }
    // iOS Simulator or default
    return 'http://localhost:8080';
  }

  // Production environment
  // TODO: Replace with production backend URL
  return 'https://api.production.com';
};

export const API_BASE_URL = getBaseUrl();
export const API_TIMEOUT = 10000; // 10 seconds
