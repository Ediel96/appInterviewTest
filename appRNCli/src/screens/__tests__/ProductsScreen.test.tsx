import React from 'react';
import {ProductsScreen} from '../ProductsScreen';
import {useProducts} from '../../hooks/useProducts';
import type {Product} from '../../models/Product';
import renderer from 'react-test-renderer';

jest.mock('../../hooks/useProducts');
const mockUseProducts = useProducts as jest.MockedFunction<typeof useProducts>;

const mockNavigation = {
  navigate: jest.fn(),
} as any;

const mockRoute = {} as any;

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
    brand: 'Brand A',
  },
];

describe('ProductsScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders loading state', () => {
    mockUseProducts.mockReturnValue({
      products: [],
      isLoading: true,
      isRefreshing: false,
      error: null,
      refetch: jest.fn(),
      refresh: jest.fn(),
    });

    const tree = renderer.create(
      <ProductsScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });

  it('renders products list', () => {
    mockUseProducts.mockReturnValue({
      products: mockProducts,
      isLoading: false,
      isRefreshing: false,
      error: null,
      refetch: jest.fn(),
      refresh: jest.fn(),
    });

    const tree = renderer.create(
      <ProductsScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });

  it('renders error state', () => {
    mockUseProducts.mockReturnValue({
      products: [],
      isLoading: false,
      isRefreshing: false,
      error: 'Network error',
      refetch: jest.fn(),
      refresh: jest.fn(),
    });

    const tree = renderer.create(
      <ProductsScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });
});
