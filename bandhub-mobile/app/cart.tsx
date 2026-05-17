import { Link } from 'expo-router';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';
import { useText } from '@/providers/DictionaryProvider';

export default function CartScreen() {
  const { items, removeItem, increment, decrement, totalAmount } = useCart();
  const t = useText();
  const currency = items[0]?.currency ?? 'PLN';

  return (
    <Screen>
      <Text style={styles.header}>{t('merch.button.cart', 'Koszyk')}</Text>

      {items.length === 0 ? (
        <Text style={styles.empty}>{t('merch.empty', 'Brak produktow w sklepie.')}</Text>
      ) : null}

      {items.map((item) => {
        const atMax = item.stockQuantity != null && item.quantity >= item.stockQuantity;
        return (
          <View key={item.productId} style={styles.card}>
            <Text style={styles.title}>{item.name}</Text>
            <Text style={styles.meta}>
              {item.price} {item.currency} / szt.
            </Text>
            <View style={styles.qtyRow}>
              <Pressable onPress={() => decrement(item.productId)} style={styles.qtyButton}>
                <Text style={styles.qtyText}>-</Text>
              </Pressable>
              <Text style={styles.qtyValue}>{item.quantity}</Text>
              <Pressable
                onPress={() => increment(item.productId)}
                disabled={atMax}
                style={[styles.qtyButton, atMax && styles.qtyButtonDisabled]}>
                <Text style={styles.qtyText}>+</Text>
              </Pressable>
              <Text style={styles.lineTotal}>
                = {(item.price * item.quantity).toFixed(2)} {item.currency}
              </Text>
            </View>
            <Pressable onPress={() => removeItem(item.productId)} style={styles.removeButton}>
              <Text style={styles.removeText}>Usun</Text>
            </Pressable>
          </View>
        );
      })}

      <View style={styles.summary}>
        <Text style={styles.summaryText}>
          Suma: {totalAmount.toFixed(2)} {currency}
        </Text>
      </View>

      {items.length > 0 ? (
        <Link href="/checkout" asChild>
          <Pressable style={styles.checkoutButton}>
            <Text style={styles.checkoutText}>Przejdz do checkoutu</Text>
          </Pressable>
        </Link>
      ) : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    color: '#f8fafc',
    fontSize: 24,
    fontWeight: '700',
  },
  empty: {
    color: '#cbd5e1',
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 12,
    gap: 8,
  },
  title: {
    color: '#f8fafc',
    fontWeight: '600',
  },
  meta: {
    color: '#cbd5e1',
    fontSize: 13,
  },
  qtyRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    flexWrap: 'wrap',
  },
  qtyButton: {
    backgroundColor: '#334155',
    borderRadius: 8,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  qtyButtonDisabled: {
    opacity: 0.4,
  },
  qtyText: {
    color: '#e2e8f0',
    fontSize: 18,
    fontWeight: '700',
  },
  qtyValue: {
    color: '#f8fafc',
    fontWeight: '600',
    minWidth: 24,
    textAlign: 'center',
  },
  lineTotal: {
    color: '#94a3b8',
    fontSize: 13,
  },
  removeButton: {
    alignSelf: 'flex-start',
    backgroundColor: '#7f1d1d',
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  removeText: {
    color: '#fecaca',
  },
  summary: {
    backgroundColor: '#0f172a',
    borderColor: '#334155',
    borderWidth: 1,
    borderRadius: 10,
    padding: 12,
  },
  summaryText: {
    color: '#e2e8f0',
    fontWeight: '700',
  },
  checkoutButton: {
    backgroundColor: '#22c55e',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  checkoutText: {
    color: '#052e16',
    fontWeight: '700',
  },
});
