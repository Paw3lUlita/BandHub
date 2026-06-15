import { colors, radius, spacing, typography } from '@/constants/theme';
import { StyleSheet, Text, View } from 'react-native';

type PriceTagProps = {
  amount: number | string;
  currency?: string;
  large?: boolean;
};

export function PriceTag({ amount, currency = 'PLN', large }: PriceTagProps) {
  return (
    <View style={[styles.wrap, large && styles.wrapLarge]}>
      <Text style={[styles.amount, large && styles.amountLarge]}>
        {amount} {currency}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    alignSelf: 'flex-start',
    backgroundColor: colors.primary + '22',
    borderRadius: radius.sm,
    paddingHorizontal: spacing.sm,
    paddingVertical: 4,
    borderWidth: 1,
    borderColor: colors.primary + '44',
  },
  wrapLarge: {
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
  },
  amount: {
    ...typography.caption,
    color: colors.primaryLight,
    fontWeight: '700',
  },
  amountLarge: {
    fontSize: 18,
    fontWeight: '800',
  },
});
