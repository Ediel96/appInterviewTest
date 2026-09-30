import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import type {FavoritesStackParamList} from './navigationTypes';
import {FavoritesScreen} from '../screens/FavoritesScreen';
import {ProductDetailScreen} from '../screens/ProductDetailScreen';

const Stack = createNativeStackNavigator<FavoritesStackParamList>();

export function FavoritesStackNavigator() {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name="FavoritesList"
        component={FavoritesScreen}
        options={{
          title: 'Favorites',
          headerLargeTitle: true,
        }}
      />
      <Stack.Screen
        name="ProductDetail"
        component={ProductDetailScreen}
        options={{
          title: 'Product Detail',
        }}
      />
    </Stack.Navigator>
  );
}
