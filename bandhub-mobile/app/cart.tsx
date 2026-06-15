import { useRouter } from 'expo-router';
import { Pressable, StyleSheet, View } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { PrimaryButton } from '@/components/ui/PrimaryButton';
import { PriceTag } from '@/components/ui/PriceTag';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { colors, radius, spacing } from '@/constants/theme';

export default function CartScreen() {
  const router = useRouter();
  const { items, removeItem, increment, decrement, totalAmount } = useCart();
  const t = useText();
  const currency = items[0]?.currency ?? 'PLN';

  return (
    <Screen>
      <SectionHeader title={t('merch.button.cart', 'Koszyk')} />

      {items.length === 0 ? (
        <EmptyState message={t('merch.empty', 'Brak produktow w sklepie.')} />
      ) : null}

      {items.map((item) => {
        const atMax = item.stockQuantity != null && item.quantity >= item.stockQuantity;
        return (
          <Card key={item.productId}>
            <AppText variant="h3">{item.name}</AppText>
            <PriceTag amount={item.price} currency={item.currency} />
            <View style={styles.qtyRow}>
              <Pressable onPress={() => decrement(item.productId)} style={styles.qtyButton}>
                <AppText variant="h3">-</AppText>
              </Pressable>
              <AppText variant="body" style={styles.qtyValue}>
                {item.quantity}
              </AppText>
              <Pressable
                onPress={() => increment(item.productId)}
                disabled={atMax}
                style={[styles.qtyButton, atMax && styles.qtyButtonDisabled]}>
                <AppText variant="h3">+</AppText>
              </Pressable>
              <AppText variant="caption" muted>
                = {(item.price * item.quantity).toFixed(2)} {item.currency}
              </AppText>
            </View>
            <PrimaryButton label="Usun" onPress={() => removeItem(item.productId)} variant="danger" style={styles.removeBtn} />
          </Card>
        );
      })}

      {items.length > 0 ? (
        <Card style={styles.summary}>
          <AppText variant="label">Podsumowanie</AppText>
          <PriceTag amount={totalAmount.toFixed(2)} currency={currency} large />
        </Card>
      ) : null}

      {items.length > 0 ? (
        <PrimaryButton label="Przejdz do checkoutu" onPress={() => router.push('/checkout')} />
      ) : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  qtyRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.sm,
    flexWrap: 'wrap',
    marginTop: spacing.sm,
  },
  qtyButton: {
    backgroundColor: colors.surfaceHover,
    borderRadius: radius.sm,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: colors.border,
  },
  qtyButtonDisabled: {
    opacity: 0.4,
  },
  qtyValue: {
    minWidth: 28,
    textAlign: 'center',
    fontWeight: '700',
  },
  removeBtn: {
    marginTop: spacing.sm,
    minHeight: 40,
  },
  summary: {
    gap: spacing.sm,
  },
});
