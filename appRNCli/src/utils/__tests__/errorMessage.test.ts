import {getErrorMessage} from '../errorMessage';
import {ApiError} from '../../models/ApiError';

describe('getErrorMessage', () => {
  it('extracts message from ApiError', () => {
    const apiError: ApiError = {
      timestamp: '2024-01-01T00:00:00Z',
      status: 404,
      code: 'NOT_FOUND',
      message: 'Product not found',
      path: '/api/products/999',
    };

    expect(getErrorMessage(apiError)).toBe('Product not found');
  });

  it('extracts message from Error object', () => {
    const error = new Error('Network error');
    expect(getErrorMessage(error)).toBe('Network error');
  });

  it('handles unknown error', () => {
    expect(getErrorMessage(null)).toBe('An unknown error occurred');
  });

  it('handles string error', () => {
    const result = getErrorMessage('Something went wrong');
    expect(result).toBe('An unexpected error occurred');
  });

  it('handles object with message', () => {
    const error = {message: 'Custom error'};
    expect(getErrorMessage(error)).toBe('Custom error');
  });
});
