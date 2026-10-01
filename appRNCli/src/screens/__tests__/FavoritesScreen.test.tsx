import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {FavoritesScreen} from '../FavoritesScreen';
import {useFavoritesStore} from '../../store/favoritesStore';
import type {Product} from '../../models/Product';

jest.mock('../../store/favoritesStore', () => ({
  useFavoritesStore: jest.fn(),
}));

const mockUseFavoritesStore = useFavoritesStore as unknown as jest.Mock;
const navigation = {navigate: jest.fn()} as any;
const route = {} as any;

const favorite: Product = {
  id: 7,
  title: 'Favorite Product',
  description: 'Saved locally',
  price: 15,
  rating: 4.2,
  thumbnail: 'https://example.com/favorite.jpg',
  images: ['https://example.com/favorite-large.jpg'],
};

function mockStore(favorites: Product[], hasHydrated: boolean) {
  mockUseFavoritesStore.mockImplementation((selector: (state: any) => any) =>
    selector({favorites, hasHydrated}),
  );
}

describe('FavoritesScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('waits for local storage hydration', async () => {
    mockStore([], false);

    const {getByLabelText, getByText} = await render(
      <FavoritesScreen navigation={navigation} route={route} />,
    );

    expect(getByLabelText('Loading favorites...')).toBeTruthy();
    expect(getByText('Loading favorites...')).toBeTruthy();
  });

  it('renders an empty state after hydration', async () => {
    mockStore([], true);

    const {getByText} = await render(
      <FavoritesScreen navigation={navigation} route={route} />,
    );

    expect(getByText('No favorites yet')).toBeTruthy();
    expect(
      getByText('Products you favorite will appear here.'),
    ).toBeTruthy();
  });

  it('renders local favorites and opens their detail', async () => {
    mockStore([favorite], true);

    const {getByRole, getByText} = await render(
      <FavoritesScreen navigation={navigation} route={route} />,
    );

    expect(getByText('Favorite Product')).toBeTruthy();
    expect(getByText('$15.00')).toBeTruthy();

    await fireEvent.press(
      getByRole('button', {name: 'Favorite Product, $15.00'}),
    );
    expect(navigation.navigate).toHaveBeenCalledWith('ProductDetail', {
      productId: 7,
    });
  });
});
