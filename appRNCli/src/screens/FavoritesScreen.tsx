import React from 'react';
import {FlatList, StyleSheet, View} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';
import type {FavoritesStackParamList} from '../navigation/navigationTypes';
import {useFavoritesStore} from '../store/favoritesStore';
import {ProductCard} from '../components/ProductCard';
import {LoadingView} from '../components/LoadingView';
import {EmptyState} from '../components/EmptyState';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import type {Product} from '../models/Product';

type Props = NativeStackScreenProps<FavoritesStackParamList, 'FavoritesList'>;

export function FavoritesScreen({navigation}: Props) {
  const favorites = useFavoritesStore(state => state.favorites);
  const hasHydrated = useFavoritesStore(state => state.hasHydrated);

  const handleProductPress = (productId: number) => {
    navigation.navigate('ProductDetail', {productId});
  };

  if (!hasHydrated) {
    return <LoadingView message="Loading favorites..." />;
  }

  if (favorites.length === 0) {
    return (
      <EmptyState
        title="No favorites yet"
        description="Products you favorite will appear here."
      />
    );
  }

  return (
    <View style={styles.container}>
      <FlatList<Product>
        data={favorites}
        keyExtractor={item => item.id.toString()}
        renderItem={({item}) => (
          <ProductCard
            product={item}
            onPress={() => handleProductPress(item.id)}
          />
        )}
        ItemSeparatorComponent={Separator}
        contentContainerStyle={styles.listContent}
        initialNumToRender={10}
        maxToRenderPerBatch={10}
        windowSize={10}
      />
    </View>
  );
}

const Separator = () => <View style={styles.separator} />;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  listContent: {
    paddingVertical: spacing.sm,
  },
  separator: {
    height: spacing.xs,
  },
});
