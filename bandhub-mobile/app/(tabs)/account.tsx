import { useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { useAuth } from '@/providers/AuthProvider';
import { Screen } from '@/components/ui/Screen';
import { AuthForm } from '@/components/ui/AuthForm';
import { MyOrderResponse, OrderStatus } from '@/types/api';
import { fetchMyOrders } from '@/lib/api';
import { useText } from '@/providers/DictionaryProvider';

function statusLabel(status: OrderStatus, t: (key: string, fallback: string) => string): string {
  return t(`order.status.${status}`, status);
}

function statusBadgeStyle(status: OrderStatus) {
  switch (status) {
    case 'PAID':
      return styles.badgePaid;
    case 'SHIPPED':
      return styles.badgeShipped;
    case 'DELIVERED':
      return styles.badgeDelivered;
    case 'CANCELLED':
      return styles.badgeCancelled;
    default:
      return styles.badgeNew;
  }
}

export default function AccountScreen() {
  const { isAuthenticated, username, logout, token } = useAuth();
  const [orders, setOrders] = useState<MyOrderResponse[]>([]);
  const [loadingOrders, setLoadingOrders] = useState(false);
  const t = useText();

  useFocusEffect(
    useCallback(() => {
      if (!token) {
        setOrders([]);
        return;
      }
      setLoadingOrders(true);
      fetchMyOrders(token)
        .then(setOrders)
        .catch(() => setOrders([]))
        .finally(() => setLoadingOrders(false));
    }, [token]),
  );

  if (!isAuthenticated) {
    return (
      <Screen>
        <Text style={styles.header}>{t('account.title.guest', 'Witaj w BandHub')}</Text>
        <Text style={styles.subheader}>
          {t(
            'account.subtitle.guest',
            'Zaloguj sie lub zaloz konto fana, aby kupowac bilety i merch.',
          )}
        </Text>
        <AuthForm />
      </Screen>
    );
  }

  return (
    <Screen>
      <View style={styles.profileCard}>
        <Text style={styles.greeting}>
          {t('account.greeting', 'Witaj')}
          {username ? `, ${username}` : ''}!
        </Text>
        <Text style={styles.subheader}>
          {t('account.subtitle.user', 'Jestes zalogowany jako fan BandHub.')}
        </Text>
        <Pressable onPress={logout} style={styles.logoutButton}>
          <Text style={styles.logoutText}>{t('account.button.logout', 'Wyloguj')}</Text>
        </Pressable>
      </View>

      <Text style={styles.section}>
        {t('account.section.orders', 'Moje zamowienia merch')}
      </Text>

      {loadingOrders ? (
        <ActivityIndicator color="#38bdf8" style={{ marginVertical: 12 }} />
      ) : null}

      {!loadingOrders && orders.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>
            {t('account.empty.orders', 'Brak zamowien. Zajrzyj do zakladki Merch.')}
          </Text>
        </View>
      ) : null}

      {orders.map((order) => (
        <View key={order.id} style={styles.card}>
          <View style={styles.cardHeader}>
            <Text style={styles.orderTitle}>Zamowienie #{order.id.slice(0, 8)}</Text>
            <View style={[styles.badge, statusBadgeStyle(order.status)]}>
              <Text style={styles.badgeText}>{statusLabel(order.status, t)}</Text>
            </View>
          </View>
          <Text style={styles.meta}>{new Date(order.createdAt).toLocaleString()}</Text>
          <Text style={styles.amount}>
            {Number(order.totalAmount).toFixed(2)} {order.currency}
          </Text>
          {order.items.map((item) => (
            <Text key={item.productId} style={styles.itemLine}>
              {item.quantity}x {item.productName}
            </Text>
          ))}
        </View>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    color: '#f8fafc',
    fontSize: 24,
    fontWeight: '700',
  },
  subheader: {
    color: '#94a3b8',
    fontSize: 13,
    lineHeight: 18,
  },
  profileCard: {
    backgroundColor: '#1e293b',
    borderRadius: 16,
    padding: 16,
    gap: 8,
    borderWidth: 1,
    borderColor: '#334155',
  },
  greeting: {
    color: '#f8fafc',
    fontSize: 22,
    fontWeight: '700',
  },
  section: {
    color: '#e2e8f0',
    fontWeight: '600',
    fontSize: 17,
    marginTop: 8,
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 12,
    gap: 6,
    borderWidth: 1,
    borderColor: '#334155',
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: 8,
  },
  orderTitle: {
    color: '#f8fafc',
    fontWeight: '600',
    flex: 1,
  },
  badge: {
    borderRadius: 8,
    paddingHorizontal: 8,
    paddingVertical: 4,
  },
  badgeText: {
    fontSize: 11,
    fontWeight: '700',
    color: '#0f172a',
  },
  badgeNew: {
    backgroundColor: '#fbbf24',
  },
  badgePaid: {
    backgroundColor: '#86efac',
  },
  badgeShipped: {
    backgroundColor: '#93c5fd',
  },
  badgeDelivered: {
    backgroundColor: '#a5b4fc',
  },
  badgeCancelled: {
    backgroundColor: '#fda4af',
  },
  amount: {
    color: '#22d3ee',
    fontWeight: '700',
  },
  meta: {
    color: '#cbd5e1',
    fontSize: 13,
  },
  itemLine: {
    color: '#94a3b8',
    fontSize: 13,
  },
  emptyCard: {
    backgroundColor: '#0f172a',
    borderRadius: 12,
    padding: 14,
    borderWidth: 1,
    borderColor: '#334155',
    borderStyle: 'dashed',
  },
  emptyText: {
    color: '#94a3b8',
  },
  logoutButton: {
    alignSelf: 'flex-start',
    backgroundColor: '#f43f5e',
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 8,
    marginTop: 4,
  },
  logoutText: {
    color: '#fff1f2',
    fontWeight: '700',
  },
});
