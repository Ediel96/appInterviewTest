import {useEffect, useState, useCallback, useRef} from 'react';
import {Product} from '../models/Product';
import {productsApi} from '../api/productsApi';
import {getErrorMessage} from '../utils/errorMessage';

interface UseProductResult {
  product: Product | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useProduct(productId: number): UseProductResult {
  const [product, setProduct] = useState<Product | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const isMountedRef = useRef(true);
  const abortControllerRef = useRef<AbortController | null>(null);

  const fetchProduct = useCallback(async () => {
    // Validate productId
    if (!Number.isInteger(productId) || productId <= 0) {
      if (isMountedRef.current) {
        setError('Invalid product ID');
        setIsLoading(false);
      }
      return;
    }

    // Cancel previous request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    // Create new abort controller
    abortControllerRef.current = new AbortController();

    setIsLoading(true);
    setError(null);

    try {
      const data = await productsApi.getProductById(
        productId,
        abortControllerRef.current.signal,
      );

      if (isMountedRef.current) {
        setProduct(data);
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
      }
    }
  }, [productId]);

  const refetch = useCallback(() => {
    fetchProduct();
  }, [fetchProduct]);

  useEffect(() => {
    isMountedRef.current = true;
    fetchProduct();

    return () => {
      isMountedRef.current = false;
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [fetchProduct]);

  return {
    product,
    isLoading,
    error,
    refetch,
  };
}
