import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {ProductsScreen} from '../ProductsScreen';
import {useProducts} from '../../hooks/useProducts';
import type {Product} from '../../models/Product';

jest.mock('../../hooks/useProducts');

const mockUseProducts = useProducts as jest.MockedFunction<
  typeof useProducts
>;
const navigation = {navigate: jest.fn()} as any;
const route = {} as any;

const product: Product = {
  id: 1,
  title: 'Product 1',
  description: 'Description 1',
  price: 29.99,
  rating: 4.5,
  thumbnail: 'https://example.com/thumb1.jpg',
  images: ['https://example.com/image1.jpg'],
  category: 'electronics',
  brand: 'Brand A',
};

const baseState = {
  products: [] as Product[],
  isLoading: false,
  isRefreshing: false,
  error: null,
  refetch: jest.fn(),
  refresh: jest.fn(),
};

describe('ProductsScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the loading state', async () => {
    mockUseProducts.mockReturnValue({...baseState, isLoading: true});

    const {getByLabelText, getByText} = await render(
      <ProductsScreen navigation={navigation} route={route} />,
    );

    expect(getByLabelText('Loading products...')).toBeTruthy();
    expect(getByText('Loading products...')).toBeTruthy();
  });

  it('renders an error and retries the request', async () => {
    const refetch = jest.fn();
    mockUseProducts.mockReturnValue({
      ...baseState,
      error: 'Network error',
      refetch,
    });

    const {getByRole, getByText} = await render(
      <ProductsScreen navigation={navigation} route={route} />,
    );

    expect(getByText('Network error')).toBeTruthy();
    await fireEvent.press(getByRole('button', {name: 'Retry'}));
    expect(refetch).toHaveBeenCalledTimes(1);
  });

  it('renders the empty state', async () => {
    mockUseProducts.mockReturnValue(baseState);

    const {getByText} = await render(
      <ProductsScreen navigation={navigation} route={route} />,
    );

    expect(getByText('No products found')).toBeTruthy();
  });

  it('renders products and navigates with the selected ID', async () => {
    mockUseProducts.mockReturnValue({...baseState, products: [product]});

    const {getByRole, getByText} = await render(
      <ProductsScreen navigation={navigation} route={route} />,
    );

    expect(getByText('Product 1')).toBeTruthy();
    expect(getByText('$29.99')).toBeTruthy();

    await fireEvent.press(
      getByRole('button', {name: 'Product 1, $29.99'}),
    );
    expect(navigation.navigate).toHaveBeenCalledWith('ProductDetail', {
      productId: 1,
    });
  });
});
