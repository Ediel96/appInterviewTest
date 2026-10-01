import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {FavoriteButton} from '../FavoriteButton';
import {useFavoritesStore} from '../../store/favoritesStore';
import type {Product} from '../../models/Product';

jest.mock('../../store/favoritesStore', () => ({
  useFavoritesStore: jest.fn(),
}));

const mockUseFavoritesStore = useFavoritesStore as unknown as jest.Mock;
const toggleFavorite = jest.fn();

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

function mockStore(isFavorite: boolean) {
  mockUseFavoritesStore.mockImplementation((selector: (state: any) => any) =>
    selector({
      isFavorite: () => isFavorite,
      toggleFavorite,
    }),
  );
}

describe('FavoriteButton', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('adds a product that is not a favorite', async () => {
    mockStore(false);
    const {getByRole, getByText} = await render(
      <FavoriteButton product={mockProduct} />,
    );
    const button = getByRole('button', {name: 'Add to favorites'});

    expect(getByText('Add to favorites')).toBeTruthy();
    expect(button.props.accessibilityState).toEqual({selected: false});

    await fireEvent.press(button);

    expect(toggleFavorite).toHaveBeenCalledWith(mockProduct);
  });

  it('shows the selected state for an existing favorite', async () => {
    mockStore(true);
    const {getByRole, getByText} = await render(
      <FavoriteButton product={mockProduct} />,
    );
    const button = getByRole('button', {name: 'Remove from favorites'});

    expect(getByText('Remove from favorites')).toBeTruthy();
    expect(button.props.accessibilityState).toEqual({selected: true});
  });
});
