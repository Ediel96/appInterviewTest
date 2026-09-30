import {useFavoritesStore} from '../favoritesStore';
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

const mockProduct2: Product = {
  id: 2,
  title: 'Test Product 2',
  description: 'Test description 2',
  price: 49.99,
  rating: 4.0,
  thumbnail: 'https://example.com/thumb2.jpg',
  images: ['https://example.com/image2.jpg'],
  category: 'clothing',
  brand: 'Test Brand 2',
};

describe('favoritesStore', () => {
  beforeEach(() => {
    // Reset store before each test
    useFavoritesStore.setState({favorites: [], hasHydrated: true});
  });

  it('adds a product to favorites', () => {
    useFavoritesStore.getState().addFavorite(mockProduct);
    const state = useFavoritesStore.getState();

    expect(state.favorites.length).toBe(1);
    expect(state.favorites[0].id).toBe(mockProduct.id);
  });

  it('does not duplicate product IDs', () => {
    useFavoritesStore.getState().addFavorite(mockProduct);
    useFavoritesStore.getState().addFavorite(mockProduct);
    const state = useFavoritesStore.getState();

    expect(state.favorites.length).toBe(1);
  });

  it('removes a product from favorites', () => {
    useFavoritesStore.getState().addFavorite(mockProduct);
    expect(useFavoritesStore.getState().favorites.length).toBe(1);

    useFavoritesStore.getState().removeFavorite(mockProduct.id);
    expect(useFavoritesStore.getState().favorites.length).toBe(0);
  });

  it('toggles favorite status', () => {
    // Add via toggle
    useFavoritesStore.getState().toggleFavorite(mockProduct);
    expect(useFavoritesStore.getState().isFavorite(mockProduct.id)).toBe(true);

    // Remove via toggle
    useFavoritesStore.getState().toggleFavorite(mockProduct);
    expect(useFavoritesStore.getState().isFavorite(mockProduct.id)).toBe(false);
  });

  it('checks if product is favorite', () => {
    expect(useFavoritesStore.getState().isFavorite(mockProduct.id)).toBe(false);

    useFavoritesStore.getState().addFavorite(mockProduct);
    expect(useFavoritesStore.getState().isFavorite(mockProduct.id)).toBe(true);
  });

  it('handles multiple products', () => {
    useFavoritesStore.getState().addFavorite(mockProduct);
    useFavoritesStore.getState().addFavorite(mockProduct2);
    const state = useFavoritesStore.getState();

    expect(state.favorites.length).toBe(2);
    expect(state.isFavorite(mockProduct.id)).toBe(true);
    expect(state.isFavorite(mockProduct2.id)).toBe(true);
  });

  it('preserves necessary product fields', () => {
    useFavoritesStore.getState().addFavorite(mockProduct);
    const saved = useFavoritesStore.getState().favorites[0];

    expect(saved).toHaveProperty('id');
    expect(saved).toHaveProperty('title');
    expect(saved).toHaveProperty('description');
    expect(saved).toHaveProperty('price');
    expect(saved).toHaveProperty('rating');
    expect(saved).toHaveProperty('thumbnail');
    expect(saved).toHaveProperty('images');
    expect(saved).toHaveProperty('category');
    expect(saved).toHaveProperty('brand');
  });
});
