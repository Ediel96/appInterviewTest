import React from 'react';
import {TouchableOpacity, Text, StyleSheet} from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';
import {useFavoritesStore} from '../store/favoritesStore';
import {Product} from '../models/Product';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import {typography} from '../theme/typography';

interface FavoriteButtonProps {
  product: Product;
}

export function FavoriteButton({product}: FavoriteButtonProps) {
  const isFavorite = useFavoritesStore(state => state.isFavorite(product.id));
  const toggleFavorite = useFavoritesStore(state => state.toggleFavorite);

  const handlePress = () => {
    toggleFavorite(product);
  };

  return (
    <TouchableOpacity
      style={[styles.button, isFavorite && styles.buttonFavorited]}
      onPress={handlePress}
      accessibilityRole="button"
      accessibilityLabel={
        isFavorite ? 'Remove from favorites' : 'Add to favorites'
      }
      accessibilityHint={
        isFavorite
          ? 'Double tap to remove from favorites'
          : 'Double tap to add to favorites'
      }
      accessibilityState={{selected: isFavorite}}>
      <Icon
        name={isFavorite ? 'heart' : 'heart-outline'}
        size={24}
        color={isFavorite ? colors.background : colors.favorite}
      />
      <Text style={[styles.text, isFavorite && styles.textFavorited]}>
        {isFavorite ? 'Remove from favorites' : 'Add to favorites'}
      </Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.background,
    borderWidth: 2,
    borderColor: colors.favorite,
    borderRadius: 8,
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.lg,
    marginTop: spacing.lg,
    minHeight: 48,
  },
  buttonFavorited: {
    backgroundColor: colors.favorite,
    borderColor: colors.favorite,
  },
  text: {
    fontSize: typography.fontSize.md,
    fontWeight: typography.fontWeight.semibold,
    color: colors.favorite,
    marginLeft: spacing.sm,
  },
  textFavorited: {
    color: colors.background,
  },
});
