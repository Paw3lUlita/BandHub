import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { fetchMyTicketOrders } from '@/lib/api';
import { MyTicketOrderResponse } from '@/types/api';
import { useAuth } from '@/providers/AuthProvider';

export default function TicketDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { token } = useAuth();
  const [orders, setOrders] = useState<MyTicketOrderResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) {
      setLoading(false);
      return;
    }
    fetchMyTicketOrders(token)
      .then(setOrders)
      .finally(() => setLoading(false));
  }, [token]);

  const order = useMemo(
    () => orders.find((entry) => entry.orderId === id),
    [id, orders],
  );

  if (loading) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <ActivityIndicator color="#38bdf8" />
      </Screen>
    );
  }

  if (!order) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <Text style={styles.error}>Nie znaleziono biletu.</Text>
      </Screen>
    );
  }

  return (
    <Screen>
      <Text style={styles.title}>{order.concertName}</Text>
      <Text style={styles.meta}>Order ID: {order.orderId}</Text>
      <Text style={styles.meta}>Data: {new Date(order.createdAt).toLocaleString()}</Text>
      <Text style={styles.meta}>Status: {order.status}</Text>

      <Text style={styles.section}>Kody biletow</Text>
      {order.ticketCodes.map((code) => (
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
