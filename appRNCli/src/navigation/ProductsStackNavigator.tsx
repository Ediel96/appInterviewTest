import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import type {ProductsStackParamList} from './navigationTypes';
import {ProductsScreen} from '../screens/ProductsScreen';
import {ProductDetailScreen} from '../screens/ProductDetailScreen';

const Stack = createNativeStackNavigator<ProductsStackParamList>();

export function ProductsStackNavigator() {
  return (
    <Stack.Navigator>
      <Stack.Screen
        name="ProductsList"
        component={ProductsScreen}
        options={{
          title: 'Products',
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
