import {ApiError} from '../models/ApiError';

/**
 * Extracts a safe error message from an unknown error
 */
export function getErrorMessage(error: unknown): string {
  if (!error) {
    return 'An unknown error occurred';
  }

  // Check if it's an ApiError from our backend
  if (isApiError(error)) {
    return error.message || 'An error occurred';
  }

  // Check if it's a standard Error object
  if (error instanceof Error) {
    return error.message || 'An error occurred';
  }

  // Check if it's an object with a message property
  if (typeof error === 'object' && 'message' in error) {
    const message = (error as {message: unknown}).message;
    if (typeof message === 'string') {
      return message;
    }
  }

  // Fallback for any other type
  return 'An unexpected error occurred';
}

function isApiError(error: unknown): error is ApiError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    'code' in error &&
    'message' in error &&
    'timestamp' in error &&
    'path' in error
  );
}
