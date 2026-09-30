import React from 'react';
import {View, Text, StyleSheet, TouchableOpacity} from 'react-native';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import {typography} from '../theme/typography';

interface ErrorViewProps {
  message: string;
  onRetry?: () => void;
}

export function ErrorView({message, onRetry}: ErrorViewProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.errorIcon} accessibilityLabel="Error" role="img">
        ⚠️
      </Text>
      <Text
        style={styles.message}
        accessibilityRole="alert"
        maxFontSizeMultiplier={1.5}>
        {message}
      </Text>
      {onRetry && (
        <TouchableOpacity
          style={styles.retryButton}
          onPress={onRetry}
          activeOpacity={0.7}
          accessibilityRole="button"
          accessibilityLabel="Retry"
          accessibilityHint="Tap to retry loading">
          <Text style={styles.retryText} maxFontSizeMultiplier={1.3}>
            Retry
          </Text>
        </TouchableOpacity>
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
  errorIcon: {
    fontSize: typography.fontSize.xxxl * 2,
    marginBottom: spacing.md,
  },
  message: {
    fontSize: typography.fontSize.md,
    color: colors.text,
    textAlign: 'center',
    marginBottom: spacing.lg,
    maxWidth: '80%',
  },
  retryButton: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    borderRadius: 8,
    minWidth: 120,
    alignItems: 'center',
  },
  retryText: {
    color: colors.background,
    fontSize: typography.fontSize.md,
    fontWeight: typography.fontWeight.semibold,
  },
});
