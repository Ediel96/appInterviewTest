import axios, {AxiosError, AxiosInstance} from 'axios';
import {API_BASE_URL, API_TIMEOUT} from '../config/environment';
import {ApiError} from '../models/ApiError';

/**
 * HTTP client configured for the backend API
 */
class HttpClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: API_TIMEOUT,
      headers: {
        Accept: 'application/json',
      },
    });
  }

  async get<T>(url: string, signal?: AbortSignal): Promise<T> {
    try {
      const response = await this.client.get<T>(url, {signal});
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  private handleError(error: unknown): Error {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ApiError>;

      // Network error or timeout
      if (!axiosError.response) {
        if (axiosError.code === 'ECONNABORTED') {
          return new Error('Request timeout. Please try again.');
        }
        if (axiosError.code === 'ERR_NETWORK') {
          return new Error('Network error. Please check your connection.');
        }
        return new Error('Unable to connect to the server.');
      }

      // API error response
      if (axiosError.response.data) {
        const apiError = axiosError.response.data;
        return new Error(apiError.message || 'An error occurred');
      }

      // HTTP status error
      return new Error(`Server error: ${axiosError.response.status}`);
    }

    // Unknown error
    if (error instanceof Error) {
      return error;
    }

    return new Error('An unexpected error occurred');
  }
}

export const httpClient = new HttpClient();
