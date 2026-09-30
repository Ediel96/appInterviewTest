import {Product, ProductListResponse} from '../models/Product';
import {httpClient} from './httpClient';

/**
 * Products API - communicates with Spring Boot backend
 */
export const productsApi = {
  /**
   * Fetches all products from the backend
   */
  async getProducts(signal?: AbortSignal): Promise<ProductListResponse> {
    const response = await httpClient.get<unknown>(
      '/api/products',
      signal,
    );

    // Defensive validation
    if (!response || typeof response !== 'object') {
      throw new Error('Invalid response format');
    }

    const data = response as Record<string, unknown>;

    if (!Array.isArray(data.products)) {
      throw new Error('Invalid products data');
    }

    if (typeof data.total !== 'number') {
      throw new Error('Invalid total count');
    }

    // Validate and normalize products
    const products = data.products.map(normalizeProduct);

    return {
      products,
      total: data.total,
    };
  },

  /**
   * Fetches a single product by ID
   */
  async getProductById(
    productId: number,
    signal?: AbortSignal,
  ): Promise<Product> {
    if (!Number.isInteger(productId) || productId <= 0) {
      throw new Error('Invalid product ID');
    }

    const response = await httpClient.get<unknown>(
      `/api/products/${productId}`,
      signal,
    );

    return normalizeProduct(response);
  },
};

/**
 * Normalizes and validates product data
 */
function normalizeProduct(data: unknown): Product {
  if (!data || typeof data !== 'object') {
    throw new Error('Invalid product data');
  }

  const product = data as Record<string, unknown>;

  // Validate required fields
  if (typeof product.id !== 'number' || product.id <= 0) {
    throw new Error('Invalid product ID');
  }

  if (typeof product.title !== 'string') {
    throw new Error('Invalid product title');
  }

  if (typeof product.description !== 'string') {
    throw new Error('Invalid product description');
  }

  if (typeof product.price !== 'number') {
    throw new Error('Invalid product price');
  }

  if (typeof product.rating !== 'number') {
    throw new Error('Invalid product rating');
  }

  if (typeof product.thumbnail !== 'string') {
    throw new Error('Invalid product thumbnail');
  }

  // Normalize images array
  let images: string[] = [];
  if (Array.isArray(product.images)) {
    images = product.images.filter(img => typeof img === 'string');
  }

  return {
    id: product.id,
    title: product.title,
    description: product.description,
    price: product.price,
    rating: product.rating,
    thumbnail: product.thumbnail,
    images,
    category: typeof product.category === 'string' ? product.category : undefined,
    brand: typeof product.brand === 'string' ? product.brand : null,
  };
}
