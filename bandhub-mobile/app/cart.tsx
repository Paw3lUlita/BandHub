import { Link } from 'expo-router';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';

export default function CartScreen() {
  const { items, removeItem, totalAmount } = useCart();
  const currency = items[0]?.currency ?? 'PLN';

  return (
    <Screen>
      <Text style={styles.header}>Koszyk</Text>

      {items.length === 0 ? <Text style={styles.empty}>Koszyk jest pusty.</Text> : null}

      {items.map((item) => (
        <View key={item.productId} style={styles.card}>
          <Text style={styles.title}>{item.name}</Text>
          <Text style={styles.meta}>
            {item.quantity} x {item.price} {item.currency}
          </Text>
          <Pressable onPress={() => removeItem(item.productId)} style={styles.removeButton}>
            <Text style={styles.removeText}>Usun</Text>
          </Pressable>
        </View>
      ))}

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
    gap: 4,
  },
  title: {
    color: '#f8fafc',
    fontWeight: '600',
  },
  meta: {
    color: '#cbd5e1',
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
