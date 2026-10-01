import axios, {type AxiosError} from 'axios';
import {httpClient} from '../httpClient';
import type {ApiError} from '../../models/ApiError';

jest.mock('axios', () => {
  const client = {get: jest.fn()};

  return {
    __esModule: true,
    default: {
      create: jest.fn(() => client),
      isAxiosError: jest.fn(
        (error: unknown) =>
          typeof error === 'object' &&
          error !== null &&
          'isAxiosError' in error,
      ),
    },
  };
});

const mockedAxios = axios as jest.Mocked<typeof axios>;
const mockClient = mockedAxios.create.mock.results[0].value as {
  get: jest.Mock;
};
const mockGet = mockClient.get;

describe('httpClient', () => {
  beforeEach(() => {
    mockGet.mockReset();
    mockedAxios.isAxiosError.mockClear();
  });

  it('configures Axios and returns response data', async () => {
    const responseData = {id: 1, name: 'Test'};
    mockGet.mockResolvedValue({data: responseData});

    const result = await httpClient.get<typeof responseData>('/test');

    expect(mockedAxios.create).toHaveBeenCalledWith(
      expect.objectContaining({
        timeout: 10000,
        headers: {Accept: 'application/json'},
      }),
    );
    expect(mockGet).toHaveBeenCalledWith('/test', {signal: undefined});
    expect(result).toEqual(responseData);
  });

  it('passes an abort signal to Axios', async () => {
    mockGet.mockResolvedValue({data: {}});
    const signal = new AbortController().signal;

    await httpClient.get('/test', signal);

    expect(mockGet).toHaveBeenCalledWith('/test', {signal});
  });

  it.each([
    ['ECONNABORTED', 'Request timeout. Please try again.'],
    ['ERR_NETWORK', 'Network error. Please check your connection.'],
    ['ERR_CONNECTION_REFUSED', 'Unable to connect to the server.'],
  ])('maps network code %s', async (code, expectedMessage) => {
    const error: Partial<AxiosError> = {
      isAxiosError: true,
      code,
      name: 'AxiosError',
      message: 'Request failed',
      response: undefined,
    };
    mockGet.mockRejectedValue(error);

    await expect(httpClient.get('/test')).rejects.toThrow(expectedMessage);
  });

  it('uses the API error message when present', async () => {
    const apiError: ApiError = {
      timestamp: '2026-10-01T00:00:00Z',
      status: 404,
      code: 'NOT_FOUND',
      message: 'Product not found',
      path: '/api/products/999',
    };
    mockGet.mockRejectedValue({
      isAxiosError: true,
      response: {data: apiError, status: 404},
    });

    await expect(httpClient.get('/test')).rejects.toThrow('Product not found');
  });

  it('uses a fallback for an API error without a message', async () => {
    mockGet.mockRejectedValue({
      isAxiosError: true,
      response: {data: {}, status: 500},
    });

    await expect(httpClient.get('/test')).rejects.toThrow('An error occurred');
  });

  it('uses the status when the error response has no body', async () => {
    mockGet.mockRejectedValue({
      isAxiosError: true,
      response: {data: undefined, status: 503},
    });

    await expect(httpClient.get('/test')).rejects.toThrow('Server error: 503');
  });

  it('preserves a generic Error message', async () => {
    mockGet.mockRejectedValue(new Error('Something went wrong'));

    await expect(httpClient.get('/test')).rejects.toThrow(
      'Something went wrong',
    );
  });

  it('maps an unknown thrown value', async () => {
    mockGet.mockRejectedValue('string error');

    await expect(httpClient.get('/test')).rejects.toThrow(
      'An unexpected error occurred',
    );
  });
});
