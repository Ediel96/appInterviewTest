import React from 'react';
import {ProductCard} from '../ProductCard';
import type {Product} from '../../models/Product';
import renderer from 'react-test-renderer';

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
  it('renders without crashing', () => {
    const mockOnPress = jest.fn();
    const tree = renderer.create(
      <ProductCard product={mockProduct} onPress={mockOnPress} />,
    );
    expect(tree).toBeTruthy();
    expect(tree.toJSON()).toBeTruthy();
  });

  it('matches snapshot structure', () => {
    const mockOnPress = jest.fn();
    const tree = renderer.create(
      <ProductCard product={mockProduct} onPress={mockOnPress} />,
    );
    const treeJson = tree.toJSON();
    expect(treeJson).toBeTruthy();
    expect(treeJson).toHaveProperty('type');
  });
});
