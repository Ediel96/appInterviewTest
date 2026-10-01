import React from 'react';
import {render} from '@testing-library/react-native';
import {RootNavigator} from '../RootNavigator';

jest.mock('@react-navigation/native', () => {
  const ReactModule = require('react');

  return {
    NavigationContainer: ({children}: {children: React.ReactNode}) =>
      ReactModule.createElement(ReactModule.Fragment, null, children),
  };
});

jest.mock('@react-navigation/bottom-tabs', () => {
  const ReactModule = require('react');
  const {View} = require('react-native');

  const Navigator = ({children}: {children: React.ReactNode}) =>
    ReactModule.createElement(View, {testID: 'bottom-tabs'}, children);
  const Screen = ({name, component: Component, options}: any) =>
    ReactModule.createElement(
      View,
      {testID: `tab-${name}`},
      options?.tabBarIcon?.({
        color: '#000000',
        size: 24,
        focused: false,
      }),
      options?.tabBarIcon?.({
        color: '#000000',
        size: 24,
        focused: true,
      }),
      Component ? ReactModule.createElement(Component) : null,
    );

  return {
    createBottomTabNavigator: () => ({Navigator, Screen}),
  };
});

jest.mock('@react-navigation/native-stack', () => {
  const ReactModule = require('react');
  const {View} = require('react-native');

  const Navigator = ({children}: {children: React.ReactNode}) =>
    ReactModule.createElement(View, {testID: 'native-stack'}, children);
  const Screen = ({name}: {name: string}) =>
    ReactModule.createElement(View, {testID: `stack-${name}`});

  return {
    createNativeStackNavigator: () => ({Navigator, Screen}),
  };
});

jest.mock('../../screens/ProductsScreen', () => ({
  ProductsScreen: () => null,
}));
jest.mock('../../screens/FavoritesScreen', () => ({
  FavoritesScreen: () => null,
}));
jest.mock('../../screens/ProductDetailScreen', () => ({
  ProductDetailScreen: () => null,
}));

describe('navigation structure', () => {
  it('registers the product and favorite tabs with their stacks', async () => {
    const {container, getAllByTestId, getByTestId} = await render(
      <RootNavigator />,
    );

    expect(getByTestId('bottom-tabs')).toBeTruthy();
    expect(getByTestId('tab-ProductsTab')).toBeTruthy();
    expect(getByTestId('tab-FavoritesTab')).toBeTruthy();
    expect(getAllByTestId('native-stack')).toHaveLength(2);
    expect(getByTestId('stack-ProductsList')).toBeTruthy();
    expect(getByTestId('stack-FavoritesList')).toBeTruthy();
    expect(getAllByTestId('stack-ProductDetail')).toHaveLength(2);
    expect(
      container
        .queryAll(instance => instance.type === 'Icon')
        .map(instance => instance.props.name),
    ).toEqual([
      'storefront-outline',
      'storefront',
      'heart-outline',
      'heart',
    ]);
  });
});
