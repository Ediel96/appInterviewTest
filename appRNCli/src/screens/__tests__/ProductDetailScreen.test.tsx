import React from 'react';
import {ProductDetailScreen} from '../ProductDetailScreen';
import {useProduct} from '../../hooks/useProduct';
import type {Product} from '../../models/Product';
import renderer from 'react-test-renderer';

jest.mock('../../hooks/useProduct');
const mockUseProduct = useProduct as jest.MockedFunction<typeof useProduct>;

jest.mock('../../store/favoritesStore', () => ({
  useFavoritesStore: jest.fn(() => false),
}));

const mockNavigation = {} as any;
const mockRoute = {
  params: {productId: 1},
} as any;

const mockProduct: Product = {
  id: 1,
  title: 'Test Product',
  description: 'This is a test product description',
  price: 99.99,
  rating: 4.7,
  thumbnail: 'https://example.com/thumb.jpg',
  images: [
    'https://example.com/image1.jpg',
    'https://example.com/image2.jpg',
  ],
  category: 'electronics',
  brand: 'Test Brand',
};

describe('ProductDetailScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders loading state', () => {
    mockUseProduct.mockReturnValue({
      product: null,
      isLoading: true,
      error: null,
      refetch: jest.fn(),
    });

    const tree = renderer.create(
      <ProductDetailScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });

  it('renders product details', () => {
    mockUseProduct.mockReturnValue({
      product: mockProduct,
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    });

    const tree = renderer.create(
      <ProductDetailScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });

  it('renders error state', () => {
    mockUseProduct.mockReturnValue({
      product: null,
      isLoading: false,
      error: 'Product not found',
      refetch: jest.fn(),
    });

    const tree = renderer.create(
      <ProductDetailScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });
});
