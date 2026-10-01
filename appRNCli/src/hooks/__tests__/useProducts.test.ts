import {act, renderHook, waitFor} from '@testing-library/react-native';
import {useProducts} from '../useProducts';
import {productsApi} from '../../api/productsApi';
import type {Product, ProductListResponse} from '../../models/Product';

jest.mock('../../api/productsApi');

const mockGetProducts = productsApi.getProducts as jest.MockedFunction<
  typeof productsApi.getProducts
>;

const mockProducts: Product[] = [
  {
    id: 1,
    title: 'Product 1',
    description: 'Description 1',
    price: 29.99,
    rating: 4.5,
    thumbnail: 'https://example.com/thumb1.jpg',
    images: ['https://example.com/image1.jpg'],
    category: 'electronics',
    brand: 'Brand 1',
  },
  {
    id: 2,
    title: 'Product 2',
    description: 'Description 2',
    price: 49.99,
    rating: 4,
    thumbnail: 'https://example.com/thumb2.jpg',
    images: ['https://example.com/image2.jpg'],
    category: 'clothing',
    brand: 'Brand 2',
  },
];

const mockResponse: ProductListResponse = {
  products: mockProducts,
  total: 2,
};

function deferred<T>() {
  let resolve!: (value: T) => void;
  let reject!: (reason?: unknown) => void;
  const promise = new Promise<T>((resolvePromise, rejectPromise) => {
    resolve = resolvePromise;
    reject = rejectPromise;
  });

  return {promise, resolve, reject};
}

describe('useProducts', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('loads products and exposes the request state', async () => {
    const request = deferred<ProductListResponse>();
    mockGetProducts.mockReturnValue(request.promise);

    const {result} = await renderHook(() => useProducts());

    expect(result.current.isLoading).toBe(true);
    expect(result.current.products).toEqual([]);
    expect(result.current.error).toBeNull();
    expect(mockGetProducts).toHaveBeenCalledWith(expect.any(AbortSignal));

    await act(() => request.resolve(mockResponse));

    await waitFor(() => expect(result.current.isLoading).toBe(false));
    expect(result.current.products).toEqual(mockProducts);
  });

  it('exposes API errors', async () => {
    mockGetProducts.mockRejectedValue(new Error('Failed to fetch products'));

    const {result} = await renderHook(() => useProducts());

    await waitFor(() =>
      expect(result.current.error).toBe('Failed to fetch products'),
    );
    expect(result.current.isLoading).toBe(false);
    expect(result.current.products).toEqual([]);
  });

  it('uses the loading flag for a manual refetch', async () => {
    mockGetProducts.mockResolvedValueOnce(mockResponse);
    const {result} = await renderHook(() => useProducts());
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    const request = deferred<ProductListResponse>();
    mockGetProducts.mockReturnValueOnce(request.promise);
    await act(() => result.current.refetch());

    expect(result.current.isLoading).toBe(true);
    expect(result.current.isRefreshing).toBe(false);

    await act(() => request.resolve(mockResponse));
    await waitFor(() => expect(result.current.isLoading).toBe(false));
  });

  it('uses the refreshing flag for pull to refresh', async () => {
    mockGetProducts.mockResolvedValueOnce(mockResponse);
    const {result} = await renderHook(() => useProducts());
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    const request = deferred<ProductListResponse>();
    mockGetProducts.mockReturnValueOnce(request.promise);
    await act(() => result.current.refresh());

    expect(result.current.isRefreshing).toBe(true);
    expect(result.current.isLoading).toBe(false);

    await act(() => request.resolve(mockResponse));
    await waitFor(() => expect(result.current.isRefreshing).toBe(false));
  });

  it('aborts the previous request before refetching', async () => {
    const firstRequest = deferred<ProductListResponse>();
    mockGetProducts.mockReturnValueOnce(firstRequest.promise);
    const {result, unmount} = await renderHook(() => useProducts());
    const firstSignal = mockGetProducts.mock.calls[0][0];

    mockGetProducts.mockResolvedValueOnce(mockResponse);
    await act(() => result.current.refetch());

    expect(firstSignal?.aborted).toBe(true);
    expect(mockGetProducts).toHaveBeenCalledTimes(2);
    await unmount();
  });

  it.each(['AbortError', 'CanceledError'])(
    'ignores %s request failures',
    async errorName => {
      const error = new Error(errorName);
      error.name = errorName;
      mockGetProducts.mockRejectedValue(error);

      const {result} = await renderHook(() => useProducts());

      await waitFor(() => expect(result.current.isLoading).toBe(false));
      expect(result.current.error).toBeNull();
    },
  );

  it('clears a previous error after a successful refetch', async () => {
    mockGetProducts.mockRejectedValueOnce(new Error('Network error'));
    const {result} = await renderHook(() => useProducts());
    await waitFor(() =>
      expect(result.current.error).toBe('Network error'),
    );

    mockGetProducts.mockResolvedValueOnce(mockResponse);
    await act(() => result.current.refetch());

    await waitFor(() => expect(result.current.error).toBeNull());
    expect(result.current.products).toEqual(mockProducts);
  });

  it('aborts the active request on unmount', async () => {
    const request = deferred<ProductListResponse>();
    mockGetProducts.mockReturnValue(request.promise);
    const {unmount} = await renderHook(() => useProducts());
    const signal = mockGetProducts.mock.calls[0][0];

    await unmount();

    expect(signal?.aborted).toBe(true);
  });
});
