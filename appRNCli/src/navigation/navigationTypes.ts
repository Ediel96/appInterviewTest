import type {NavigatorScreenParams} from '@react-navigation/native';

// Products Stack
export type ProductsStackParamList = {
  ProductsList: undefined;
  ProductDetail: {productId: number};
};

// Favorites Stack
export type FavoritesStackParamList = {
  FavoritesList: undefined;
  ProductDetail: {productId: number};
};

// Bottom Tab Navigator
export type BottomTabParamList = {
  ProductsTab: NavigatorScreenParams<ProductsStackParamList>;
  FavoritesTab: NavigatorScreenParams<FavoritesStackParamList>;
};

// Root navigation types
declare global {
  namespace ReactNavigation {
    interface RootParamList extends BottomTabParamList {}
  }
}
