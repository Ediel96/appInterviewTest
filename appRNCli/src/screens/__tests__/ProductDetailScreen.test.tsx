import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {ProductDetailScreen} from '../ProductDetailScreen';
import {useProduct} from '../../hooks/useProduct';
import type {Product} from '../../models/Product';

jest.mock('../../hooks/useProduct');
jest.mock('../../components/FavoriteButton', () => {
  const ReactModule = require('react');
  const {Text} = require('react-native');

  return {
    FavoriteButton: () =>
      ReactModule.createElement(Text, null, 'Favorite action'),
  };
});

const mockUseProduct = useProduct as jest.MockedFunction<typeof useProduct>;
const navigation = {} as any;
const route = {params: {productId: 1}} as any;

const product: Product = {
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

  it('requests the route product and renders loading', async () => {
    mockUseProduct.mockReturnValue({
      product: null,
      isLoading: true,
      error: null,
      refetch: jest.fn(),
    });

    const {getByLabelText, getByText} = await render(
      <ProductDetailScreen navigation={navigation} route={route} />,
    );

    expect(mockUseProduct).toHaveBeenCalledWith(1);
    expect(getByLabelText('Loading product details...')).toBeTruthy();
    expect(getByText('Loading product details...')).toBeTruthy();
  });

  it('renders an error and retries', async () => {
    const refetch = jest.fn();
    mockUseProduct.mockReturnValue({
      product: null,
      isLoading: false,
      error: 'Product not found',
      refetch,
    });

    const {getByRole, getByText} = await render(
      <ProductDetailScreen navigation={navigation} route={route} />,
    );

    expect(getByText('Product not found')).toBeTruthy();
    await fireEvent.press(getByRole('button', {name: 'Retry'}));
    expect(refetch).toHaveBeenCalledTimes(1);
  });

  it('renders the defensive not-found state', async () => {
    mockUseProduct.mockReturnValue({
      product: null,
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    });

    const {getByText} = await render(
      <ProductDetailScreen navigation={navigation} route={route} />,
    );

    expect(getByText('Product not found')).toBeTruthy();
  });

  it('renders all required product details', async () => {
    mockUseProduct.mockReturnValue({
      product,
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    });

    const {getByLabelText, getByText} = await render(
      <ProductDetailScreen navigation={navigation} route={route} />,
    );

    expect(getByText('Test Product')).toBeTruthy();
    expect(getByText('This is a test product description')).toBeTruthy();
    expect(getByText('$99.99')).toBeTruthy();
    expect(getByText('4.7')).toBeTruthy();
    expect(getByText('Category: electronics')).toBeTruthy();
    expect(getByText('Brand: Test Brand')).toBeTruthy();
    expect(getByText('Favorite action')).toBeTruthy();
    expect(getByLabelText('Image 1 of 2')).toBeTruthy();
  });

  it('omits optional category and brand values', async () => {
    mockUseProduct.mockReturnValue({
      product: {...product, category: undefined, brand: null},
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    });

    const {queryByText} = await render(
      <ProductDetailScreen navigation={navigation} route={route} />,
    );

    expect(queryByText(/Category:/)).toBeNull();
    expect(queryByText(/Brand:/)).toBeNull();
  });
});
