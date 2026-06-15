import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { StyleSheet } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { fetchMyTicketOrders } from '@/lib/api';
import { MyTicketOrderResponse } from '@/types/api';
import { useAuth } from '@/providers/AuthProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { LoadingView } from '@/components/ui/LoadingView';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { StatusPill } from '@/components/ui/StatusPill';
import { colors, radius } from '@/constants/theme';

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

  const order = useMemo(() => orders.find((entry) => entry.orderId === id), [id, orders]);

  if (loading) {
    return (
      <Screen scroll={false}>
        <LoadingView />
      </Screen>
    );
  }

  if (!order) {
    return (
      <Screen scroll={false}>
        <AppText variant="error">Nie znaleziono biletu.</AppText>
      </Screen>
    );
  }

  return (
    <Screen>
      <AppText variant="h1">{order.concertName}</AppText>
      <AppText variant="caption" muted>Order ID: {order.orderId}</AppText>
      <AppText variant="caption" muted>Data: {new Date(order.createdAt).toLocaleString()}</AppText>
      <StatusPill label={order.status} tone="info" />

      <SectionHeader title="Kody biletow" />
      {order.ticketCodes.map((code) => (
        <Card key={code} style={styles.codeBox}>
          <AppText variant="body" style={styles.code}>
            {code}
          </AppText>
        </Card>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  codeBox: {
    backgroundColor: colors.backgroundElevated,
    borderColor: colors.accent + '44',
  },
  code: {
    fontFamily: 'monospace',
    fontWeight: '700',
    color: colors.accent,
    fontSize: 16,
    letterSpacing: 1,
  },
});
