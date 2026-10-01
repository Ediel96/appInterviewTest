import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/Ionicons';
import type {BottomTabParamList} from './navigationTypes';
import {ProductsStackNavigator} from './ProductsStackNavigator';
import {FavoritesStackNavigator} from './FavoritesStackNavigator';
import {colors} from '../theme/colors';

const Tab = createBottomTabNavigator<BottomTabParamList>();

interface TabIconProps {
  focused: boolean;
  color: string;
  size: number;
}

const ProductsTabIcon = ({focused, color, size}: TabIconProps) => (
  <Icon
    name={focused ? 'storefront' : 'storefront-outline'}
    size={size}
    color={color}
  />
);

const FavoritesTabIcon = ({focused, color, size}: TabIconProps) => (
  <Icon
    name={focused ? 'heart' : 'heart-outline'}
    size={size}
    color={color}
  />
);

export function RootNavigator() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={{
          headerShown: false,
          tabBarActiveTintColor: colors.primary,
          tabBarInactiveTintColor: colors.textSecondary,
        }}>
        <Tab.Screen
          name="ProductsTab"
          component={ProductsStackNavigator}
          options={{
            title: 'Products',
            tabBarIcon: ProductsTabIcon,
          }}
        />
        <Tab.Screen
          name="FavoritesTab"
          component={FavoritesStackNavigator}
          options={{
            title: 'Favorites',
            tabBarIcon: FavoritesTabIcon,
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}
