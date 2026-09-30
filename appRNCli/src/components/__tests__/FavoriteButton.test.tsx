import React from 'react';
import {FavoriteButton} from '../FavoriteButton';
import type {Product} from '../../models/Product';
import renderer from 'react-test-renderer';

jest.mock('../../store/favoritesStore');

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

describe('FavoriteButton', () => {
  it('renders without crashing', () => {
    const tree = renderer.create(<FavoriteButton product={mockProduct} />);
    expect(tree).toBeTruthy();
  });

  it('renders with correct structure', () => {
    const tree = renderer.create(<FavoriteButton product={mockProduct} />);
    const treeJson = tree.toJSON();
    expect(treeJson).toBeTruthy();
    expect(treeJson).toHaveProperty('type');
  });
});
