import { colors } from '@/constants/theme';
import { ActivityIndicator, StyleSheet, View, ViewStyle } from 'react-native';

type LoadingViewProps = {
  style?: ViewStyle;
};

export function LoadingView({ style }: LoadingViewProps) {
  return (
    <View style={[styles.center, style]}>
      <ActivityIndicator size="large" color={colors.accent} />
    </View>
  );
}

const styles = StyleSheet.create({
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 32,
  },
});
