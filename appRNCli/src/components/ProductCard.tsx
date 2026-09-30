import React from 'react';
import {
  View,
  Text,
  Image,
  StyleSheet,
  Pressable,
} from 'react-native';
import {Product} from '../models/Product';
import {formatCurrency} from '../utils/currency';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import {typography} from '../theme/typography';

interface ProductCardProps {
  product: Product;
  onPress: () => void;
}

function ProductCardComponent({product, onPress}: ProductCardProps) {
  return (
    <Pressable
      style={({pressed}) => [styles.container, pressed && styles.pressed]}
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={`${product.title}, ${formatCurrency(product.price)}`}
      accessibilityHint="Double tap to view product details">
      <Image
        source={{uri: product.thumbnail}}
        style={styles.thumbnail}
        resizeMode="cover"
        accessibilityIgnoresInvertColors
        accessible={false}
      />
      <View style={styles.content}>
        <Text
          style={styles.title}
          numberOfLines={2}
          maxFontSizeMultiplier={1.3}>
          {product.title}
        </Text>
        <Text style={styles.price} maxFontSizeMultiplier={1.3}>
          {formatCurrency(product.price)}
        </Text>
      </View>
    </Pressable>
  );
}

export const ProductCard = React.memo(ProductCardComponent);

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    backgroundColor: colors.card,
    borderRadius: 8,
    overflow: 'hidden',
    marginHorizontal: spacing.md,
    marginVertical: spacing.xs,
    shadowColor: colors.shadow,
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  pressed: {
    opacity: 0.7,
  },
  thumbnail: {
    width: 100,
    height: 100,
    backgroundColor: colors.backgroundSecondary,
  },
  content: {
    flex: 1,
    padding: spacing.md,
    justifyContent: 'space-between',
  },
  title: {
    fontSize: typography.fontSize.md,
    fontWeight: typography.fontWeight.medium,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  price: {
    fontSize: typography.fontSize.lg,
    fontWeight: typography.fontWeight.bold,
    color: colors.primary,
  },
});
