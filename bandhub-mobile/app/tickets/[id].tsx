import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { getTicketPurchases } from '@/lib/storage';
import { LocalTicketPurchase } from '@/types/api';

export default function TicketDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [purchases, setPurchases] = useState<LocalTicketPurchase[]>([]);

  useEffect(() => {
    getTicketPurchases().then(setPurchases);
  }, []);

  const purchase = useMemo(() => purchases.find((entry) => entry.id === id), [id, purchases]);

  if (!purchase) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <Text style={styles.error}>Nie znaleziono biletu.</Text>
      </Screen>
    );
  }

  return (
    <Screen>
      <Text style={styles.title}>{purchase.concertName}</Text>
      <Text style={styles.meta}>Order ID: {purchase.orderId}</Text>
      <Text style={styles.meta}>Data: {new Date(purchase.purchasedAt).toLocaleString()}</Text>

      <Text style={styles.section}>Kody biletow</Text>
      {purchase.ticketCodes.map((code) => (
        <View key={code} style={styles.codeBox}>
          <Text style={styles.code}>{code}</Text>
        </View>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
  },
  title: {
    color: '#f8fafc',
    fontSize: 24,
    fontWeight: '700',
  },
  meta: {
    color: '#94a3b8',
  },
  section: {
    color: '#e2e8f0',
    fontSize: 18,
    fontWeight: '600',
    marginTop: 10,
  },
  codeBox: {
    backgroundColor: '#111827',
    borderColor: '#334155',
    borderWidth: 1,
    borderRadius: 10,
    paddingVertical: 10,
    paddingHorizontal: 12,
  },
  code: {
    color: '#22d3ee',
    fontFamily: 'monospace',
    fontWeight: '700',
  },
  error: {
    color: '#fda4af',
  },
});
