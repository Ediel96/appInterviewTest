import {act, renderHook, waitFor} from '@testing-library/react-native';
import {useProduct} from '../useProduct';
import {productsApi} from '../../api/productsApi';
import type {Product} from '../../models/Product';

jest.mock('../../api/productsApi');

const mockGetProductById = productsApi.getProductById as jest.MockedFunction<
  typeof productsApi.getProductById
>;

const mockProduct: Product = {
  id: 1,
  title: 'Test Product',
  description: 'Test description',
  price: 29.99,
  rating: 4.5,
  thumbnail: 'https://example.com/thumb.jpg',
  images: ['https://example.com/image.jpg'],
  category: 'electronics',
  brand: 'Test Brand',
};

function deferred<T>() {
  let resolve!: (value: T) => void;
  const promise = new Promise<T>(resolvePromise => {
    resolve = resolvePromise;
  });

  return {promise, resolve};
}

describe('useProduct', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('loads a product using its ID', async () => {
    const request = deferred<Product>();
    mockGetProductById.mockReturnValue(request.promise);

    const {result} = await renderHook(() => useProduct(1));

    expect(result.current.isLoading).toBe(true);
    expect(result.current.product).toBeNull();
    expect(mockGetProductById).toHaveBeenCalledWith(
      1,
      expect.any(AbortSignal),
    );

    await act(() => request.resolve(mockProduct));

    await waitFor(() => expect(result.current.isLoading).toBe(false));
    expect(result.current.product).toEqual(mockProduct);
    expect(result.current.error).toBeNull();
  });

  it.each([0, -5, 1.5])('rejects invalid product ID %p', async productId => {
    const {result} = await renderHook(() => useProduct(productId));

    await waitFor(() => expect(result.current.isLoading).toBe(false));
    expect(result.current.error).toBe('Invalid product ID');
    expect(result.current.product).toBeNull();
    expect(mockGetProductById).not.toHaveBeenCalled();
  });

  it('exposes API errors', async () => {
    mockGetProductById.mockRejectedValue(new Error('Product not found'));

    const {result} = await renderHook(() => useProduct(1));

    await waitFor(() =>
      expect(result.current.error).toBe('Product not found'),
    );
    expect(result.current.isLoading).toBe(false);
    expect(result.current.product).toBeNull();
  });

  it('refetches the current product', async () => {
    mockGetProductById.mockResolvedValue(mockProduct);
    const {result} = await renderHook(() => useProduct(1));
    await waitFor(() => expect(result.current.isLoading).toBe(false));

    await act(() => result.current.refetch());

    await waitFor(() =>
      expect(mockGetProductById).toHaveBeenCalledTimes(2),
    );
  });

  it('aborts the previous request when the ID changes', async () => {
    const firstRequest = deferred<Product>();
    mockGetProductById.mockReturnValueOnce(firstRequest.promise);
    const {rerender, unmount} = await renderHook<
      ReturnType<typeof useProduct>,
      {id: number}
    >(({id}) => useProduct(id), {initialProps: {id: 1}});
    const firstSignal = mockGetProductById.mock.calls[0][1];

    mockGetProductById.mockResolvedValueOnce({...mockProduct, id: 2});
    await rerender({id: 2});

    expect(firstSignal?.aborted).toBe(true);
    expect(mockGetProductById).toHaveBeenLastCalledWith(
      2,
      expect.any(AbortSignal),
    );
    await unmount();
  });

  it.each(['AbortError', 'CanceledError'])(
    'ignores %s request failures',
    async errorName => {
      const error = new Error(errorName);
      error.name = errorName;
      mockGetProductById.mockRejectedValue(error);

      const {result} = await renderHook(() => useProduct(1));

      await waitFor(() => expect(result.current.isLoading).toBe(false));
      expect(result.current.error).toBeNull();
    },
  );

  it('aborts the active request on unmount', async () => {
    const request = deferred<Product>();
    mockGetProductById.mockReturnValue(request.promise);
    const {unmount} = await renderHook(() => useProduct(1));
    const signal = mockGetProductById.mock.calls[0][1];

    await unmount();

    expect(signal?.aborted).toBe(true);
  });
});
