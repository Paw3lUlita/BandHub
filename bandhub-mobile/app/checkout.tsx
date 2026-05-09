import { useRouter } from 'expo-router';
import { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput } from 'react-native';
import { placeOrder } from '@/lib/api';
import { saveMerchOrder } from '@/lib/storage';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';
import { useAuth } from '@/providers/AuthProvider';

export default function CheckoutScreen() {
  const router = useRouter();
  const { items, totalAmount, clear } = useCart();
  const { token } = useAuth();
  const [address, setAddress] = useState('');
  const [paymentProvider, setPaymentProvider] = useState('manual');
  const [result, setResult] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const submit = async () => {
    if (items.length === 0) {
      return;
    }

    setSubmitting(true);
    try {
      const orderItems = items.reduce<Record<string, number>>((acc, item) => {
        acc[item.productId] = item.quantity;
        return acc;
      }, {});
      const response = await placeOrder(orderItems, address, paymentProvider, token);
      const ref = response.location?.split('/').pop() ?? 'unknown';
      await saveMerchOrder({
        id: `${ref}-${Date.now()}`,
        orderRef: ref,
        createdAt: new Date().toISOString(),
        totalAmount,
        currency: items[0]?.currency ?? 'PLN',
        items,
        deliveryAddress: address,
        paymentProvider,
      });
      clear();
      setResult(`Zamowienie przyjete. Ref: ${ref}`);
      setTimeout(() => router.replace('/(tabs)/account'), 700);
    } catch (err) {
      setResult(err instanceof Error ? err.message : 'Nie udalo sie zlozyc zamowienia');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Screen>
      <Text style={styles.header}>Checkout merch</Text>
      <Text style={styles.meta}>Pozycje: {items.length}</Text>
      <Text style={styles.meta}>
        Suma: {totalAmount.toFixed(2)} {items[0]?.currency ?? 'PLN'}
      </Text>

      <TextInput
        value={address}
        onChangeText={setAddress}
        placeholder="Adres dostawy"
        placeholderTextColor="#94a3b8"
        style={styles.input}
      />

      <TextInput
        value={paymentProvider}
        onChangeText={setPaymentProvider}
        placeholder="Provider platnosci"
        placeholderTextColor="#94a3b8"
        style={styles.input}
      />

      <Pressable disabled={submitting || items.length === 0} onPress={submit} style={styles.button}>
        <Text style={styles.buttonText}>{submitting ? 'Wysylanie...' : 'Zloz zamowienie'}</Text>
      </Pressable>

      {result ? <Text style={styles.result}>{result}</Text> : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    color: '#f8fafc',
    fontSize: 24,
    fontWeight: '700',
  },
  meta: {
    color: '#cbd5e1',
  },
  input: {
    backgroundColor: '#1e293b',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#334155',
    color: '#f8fafc',
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  button: {
    backgroundColor: '#22c55e',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  buttonText: {
    color: '#052e16',
    fontWeight: '700',
  },
  result: {
    color: '#93c5fd',
  },
});
