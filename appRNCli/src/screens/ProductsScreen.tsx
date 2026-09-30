import React, {useCallback} from 'react';
import {FlatList, StyleSheet, View} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';
import type {ProductsStackParamList} from '../navigation/navigationTypes';
import {useProducts} from '../hooks/useProducts';
import {ProductCard} from '../components/ProductCard';
import {LoadingView} from '../components/LoadingView';
import {ErrorView} from '../components/ErrorView';
import {EmptyState} from '../components/EmptyState';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import type {Product} from '../models/Product';

type Props = NativeStackScreenProps<ProductsStackParamList, 'ProductsList'>;

const Separator = () => <View style={styles.separator} />;

const EmptyListComponent = () => (
  <EmptyState
    title="No products"
    description="Pull to refresh or try again later."
  />
);

export function ProductsScreen({navigation}: Props) {
  const {products, isLoading, isRefreshing, error, refetch, refresh} =
    useProducts();

  const handleProductPress = useCallback(
    (productId: number) => {
      navigation.navigate('ProductDetail', {productId});
    },
    [navigation],
  );

  const renderItem = useCallback(
    ({item}: {item: Product}) => (
      <ProductCard
        product={item}
        onPress={() => handleProductPress(item.id)}
      />
    ),
    [handleProductPress],
  );

  const keyExtractor = useCallback((item: Product) => item.id.toString(), []);

  if (isLoading && !isRefreshing) {
    return <LoadingView message="Loading products..." />;
  }

  if (error && !isRefreshing) {
    return <ErrorView message={error} onRetry={refetch} />;
  }

  if (products.length === 0 && !isLoading && !error) {
    return (
      <EmptyState
        title="No products found"
        description="There are no products available at the moment."
      />
    );
  }

  return (
    <View style={styles.container}>
      <FlatList<Product>
        data={products}
        keyExtractor={keyExtractor}
        renderItem={renderItem}
        ItemSeparatorComponent={Separator}
        contentContainerStyle={styles.listContent}
        refreshing={isRefreshing}
        onRefresh={refresh}
        initialNumToRender={10}
        maxToRenderPerBatch={10}
        windowSize={10}
        ListEmptyComponent={EmptyListComponent}
      />
    </View>
  );
}

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
