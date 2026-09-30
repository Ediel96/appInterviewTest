import {useEffect, useState, useCallback, useRef} from 'react';
import {ProductListResponse} from '../models/Product';
import {productsApi} from '../api/productsApi';
import {getErrorMessage} from '../utils/errorMessage';

interface UseProductsResult {
  products: ProductListResponse['products'];
  isLoading: boolean;
  isRefreshing: boolean;
  error: string | null;
  refetch: () => void;
  refresh: () => void;
}

export function useProducts(): UseProductsResult {
  const [products, setProducts] = useState<ProductListResponse['products']>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const isMountedRef = useRef(true);
  const abortControllerRef = useRef<AbortController | null>(null);

  const fetchProducts = useCallback(async (isRefresh: boolean = false) => {
    // Cancel previous request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    // Create new abort controller
    abortControllerRef.current = new AbortController();

    if (isRefresh) {
      setIsRefreshing(true);
    } else {
      setIsLoading(true);
    }
    setError(null);

    try {
      const data = await productsApi.getProducts(
        abortControllerRef.current.signal,
      );

      if (isMountedRef.current) {
        setProducts(data.products);
        setError(null);
      }
    } catch (err) {
      if (isMountedRef.current) {
        // Don't set error if request was aborted
        if (err instanceof Error && err.name !== 'AbortError' && err.name !== 'CanceledError') {
          setError(getErrorMessage(err));
        }
      }
    } finally {
      if (isMountedRef.current) {
        setIsLoading(false);
        setIsRefreshing(false);
      }
    }
  }, []);

  const refetch = useCallback(() => {
    fetchProducts(false);
  }, [fetchProducts]);

  const refresh = useCallback(() => {
    fetchProducts(true);
  }, [fetchProducts]);

  useEffect(() => {
    isMountedRef.current = true;
    fetchProducts(false);

    return () => {
      isMountedRef.current = false;
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [fetchProducts]);

  return {
    products,
    isLoading,
    isRefreshing,
    error,
    refetch,
    refresh,
  };
}
