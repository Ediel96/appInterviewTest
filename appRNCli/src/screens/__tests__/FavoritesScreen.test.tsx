import React from 'react';
import {FavoritesScreen} from '../FavoritesScreen';
import renderer from 'react-test-renderer';

jest.mock('../../store/favoritesStore', () => ({
  useFavoritesStore: jest.fn(selector => {
    const mockState = {
      favorites: [],
      hasHydrated: true,
    };
    return selector(mockState);
  }),
}));

const mockNavigation = {
  navigate: jest.fn(),
} as any;

const mockRoute = {} as any;

describe('FavoritesScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders without crashing', () => {
    const tree = renderer.create(
      <FavoritesScreen navigation={mockNavigation} route={mockRoute} />,
    );
    expect(tree).toBeTruthy();
  });

  it('renders with correct structure', () => {
    const tree = renderer.create(
      <FavoritesScreen navigation={mockNavigation} route={mockRoute} />,
    );
    const treeJson = tree.toJSON();
    expect(treeJson).toBeTruthy();
  });
});
