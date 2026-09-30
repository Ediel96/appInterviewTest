import React from 'react';
import {View, ActivityIndicator, StyleSheet, Text} from 'react-native';
import {colors} from '../theme/colors';
import {spacing} from '../theme/spacing';
import {typography} from '../theme/typography';

interface LoadingViewProps {
  message?: string;
}

export function LoadingView({message = 'Loading...'}: LoadingViewProps) {
  return (
    <View style={styles.container} accessibilityRole="progressbar">
      <ActivityIndicator
        size="large"
        color={colors.primary}
        accessibilityLabel={message}
      />
      {message && (
        <Text style={styles.message} accessibilityLiveRegion="polite">
          {message}
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
  message: {
    marginTop: spacing.md,
    fontSize: typography.fontSize.md,
    color: colors.textSecondary,
    textAlign: 'center',
  },
});
