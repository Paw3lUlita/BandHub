import { useRouter } from 'expo-router';
import { useState } from 'react';
import { StyleSheet, TextInput, View } from 'react-native';
import { placeOrder } from '@/lib/api';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';
import { useAuth } from '@/providers/AuthProvider';
import { ApiError } from '@/lib/http';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { PrimaryButton } from '@/components/ui/PrimaryButton';
import { PriceTag } from '@/components/ui/PriceTag';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { colors, radius, spacing } from '@/constants/theme';

export default function CheckoutScreen() {
  const router = useRouter();
  const { items, totalAmount, clear } = useCart();
  const { token } = useAuth();
  const [address, setAddress] = useState('');
  const [paymentProvider, setPaymentProvider] = useState('manual');
  const [result, setResult] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const submit = async () => {
    if (items.length === 0 || !token) {
      return;
    }

    setSubmitting(true);
    setResult(null);
    try {
      const orderItems = items.reduce<Record<string, number>>((acc, item) => {
        acc[item.productId] = item.quantity;
        return acc;
      }, {});
      await placeOrder(orderItems, address, paymentProvider, token);
      clear();
      setResult('Zamowienie przyjete.');
      setTimeout(() => router.replace('/(tabs)/account'), 700);
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setResult('Sesja wygasla - zaloguj sie ponownie w zakladce Konto.');
      } else {
        setResult(err instanceof Error ? err.message : 'Nie udalo sie zlozyc zamowienia');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Screen>
      <SectionHeader title="Checkout merch" subtitle="Podaj adres dostawy i zloz zamowienie" />

      <Card>
        <AppText variant="caption" muted>
          Pozycje: {items.length}
        </AppText>
        <PriceTag amount={totalAmount.toFixed(2)} currency={items[0]?.currency ?? 'PLN'} large />
      </Card>

      <View style={styles.field}>
        <AppText variant="label">Adres dostawy</AppText>
        <TextInput
          value={address}
          onChangeText={setAddress}
          placeholder="Adres dostawy"
          placeholderTextColor={colors.textDim}
          style={styles.input}
        />
      </View>

      <View style={styles.field}>
        <AppText variant="label">Provider platnosci</AppText>
        <TextInput
          value={paymentProvider}
          onChangeText={setPaymentProvider}
          placeholder="Provider platnosci"
          placeholderTextColor={colors.textDim}
          style={styles.input}
        />
      </View>

      <PrimaryButton
        label={submitting ? 'Wysylanie...' : 'Zloz zamowienie'}
        onPress={submit}
        disabled={submitting || items.length === 0 || !token}
        loading={submitting}
      />

      {result ? <AppText variant={result.includes('przyjete') ? 'success' : 'error'}>{result}</AppText> : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  field: {
    gap: spacing.xs,
  },
  input: {
    backgroundColor: colors.surface,
    borderRadius: radius.md,
    borderWidth: 1,
    borderColor: colors.border,
    color: colors.text,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.md,
    fontSize: 15,
  },
});
