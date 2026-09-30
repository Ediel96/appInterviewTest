import React from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import {typography} from '../theme/typography';

interface EmptyStateProps {
  title: string;
  description?: string;
}

export function EmptyState({title, description}: EmptyStateProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.icon} accessibilityLabel="Empty" role="img">
        📭
      </Text>
      <Text
        style={styles.title}
        accessibilityRole="header"
        maxFontSizeMultiplier={1.5}>
        {title}
      </Text>
      {description && (
        <Text style={styles.description} maxFontSizeMultiplier={1.5}>
          {description}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: colors.background,
    padding: spacing.lg,
  },
  icon: {
    fontSize: typography.fontSize.xxxl * 2,
    marginBottom: spacing.md,
  },
  title: {
    fontSize: typography.fontSize.lg,
    fontWeight: typography.fontWeight.semibold,
    color: colors.text,
    textAlign: 'center',
    marginBottom: spacing.sm,
  },
  description: {
    fontSize: typography.fontSize.md,
    color: colors.textSecondary,
    textAlign: 'center',
    maxWidth: '80%',
  },
});
