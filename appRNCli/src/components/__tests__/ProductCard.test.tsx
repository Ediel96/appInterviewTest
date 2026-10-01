import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {ProductCard} from '../ProductCard';
import type {Product} from '../../models/Product';

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

describe('ProductCard', () => {
  it('shows the minimum product information', async () => {
    const {getByText, getByRole} = await render(
      <ProductCard product={mockProduct} onPress={jest.fn()} />,
    );

    expect(getByText('Test Product')).toBeTruthy();
    expect(getByText('$29.99')).toBeTruthy();
    expect(
      getByRole('button', {name: 'Test Product, $29.99'}),
    ).toBeTruthy();
  });

  it('notifies when the product is pressed', async () => {
    const onPress = jest.fn();
    const {getByRole} = await render(
      <ProductCard product={mockProduct} onPress={onPress} />,
    );

    await fireEvent.press(
      getByRole('button', {name: 'Test Product, $29.99'}),
    );

    expect(onPress).toHaveBeenCalledTimes(1);
  });
});
