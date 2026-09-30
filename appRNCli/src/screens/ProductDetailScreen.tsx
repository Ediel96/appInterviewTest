import React from 'react';
import {View, Text, StyleSheet, ScrollView} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';
import type {
  ProductsStackParamList,
  FavoritesStackParamList,
} from '../navigation/navigationTypes';
import {useProduct} from '../hooks/useProduct';
import {ProductCarousel} from '../components/ProductCarousel';
import {LoadingView} from '../components/LoadingView';
import {ErrorView} from '../components/ErrorView';
import {FavoriteButton} from '../components/FavoriteButton';
import {formatCurrency} from '../utils/currency';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import {typography} from '../theme/typography';

type ProductsProps = NativeStackScreenProps<
  ProductsStackParamList,
  'ProductDetail'
>;
type FavoritesProps = NativeStackScreenProps<
  FavoritesStackParamList,
  'ProductDetail'
>;

type Props = ProductsProps | FavoritesProps;

export function ProductDetailScreen({route}: Props) {
  const {productId} = route.params;
  const {product, isLoading, error, refetch} = useProduct(productId);

  if (isLoading) {
    return <LoadingView message="Loading product details..." />;
  }

  if (error) {
    return <ErrorView message={error} onRetry={refetch} />;
  }

  if (!product) {
    return (
      <ErrorView
        message="Product not found"
        onRetry={refetch}
      />
    );
  }

  return (
    <ScrollView style={styles.container}>
      <ProductCarousel images={product.images} />

      <View style={styles.content}>
        <Text style={styles.title}>{product.title}</Text>

        <View style={styles.metaRow}>
          <Text style={styles.price}>{formatCurrency(product.price)}</Text>
          <View style={styles.ratingContainer}>
            <Text style={styles.ratingIcon}>⭐</Text>
            <Text style={styles.rating}>{product.rating.toFixed(1)}</Text>
          </View>
        </View>

        {product.category && (
          <Text style={styles.category}>Category: {product.category}</Text>
        )}

        {product.brand && (
          <Text style={styles.brand}>Brand: {product.brand}</Text>
        )}

        <View style={styles.descriptionContainer}>
          <Text style={styles.descriptionTitle}>Description</Text>
          <Text style={styles.description}>{product.description}</Text>
        </View>

        <FavoriteButton product={product} />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  content: {
    padding: spacing.md,
  },
  title: {
    fontSize: typography.fontSize.xxl,
    fontWeight: typography.fontWeight.bold,
    color: colors.text,
    marginBottom: spacing.md,
  },
  metaRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  price: {
    fontSize: typography.fontSize.xl,
    fontWeight: typography.fontWeight.bold,
    color: colors.primary,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.backgroundSecondary,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    borderRadius: 20,
  },
  ratingIcon: {
    fontSize: typography.fontSize.md,
    marginRight: spacing.xs,
  },
  rating: {
    fontSize: typography.fontSize.md,
    fontWeight: typography.fontWeight.semibold,
    color: colors.text,
  },
  category: {
    fontSize: typography.fontSize.md,
    color: colors.textSecondary,
    marginBottom: spacing.xs,
  },
  brand: {
    fontSize: typography.fontSize.md,
    color: colors.textSecondary,
    marginBottom: spacing.md,
  },
  descriptionContainer: {
    marginTop: spacing.md,
  },
  descriptionTitle: {
    fontSize: typography.fontSize.lg,
    fontWeight: typography.fontWeight.semibold,
    color: colors.text,
    marginBottom: spacing.sm,
  },
  description: {
    fontSize: typography.fontSize.md,
    color: colors.text,
    lineHeight: typography.fontSize.md * typography.lineHeight.relaxed,
  },
});
