import { useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useAuth } from '@/providers/AuthProvider';
import { Screen } from '@/components/ui/Screen';
import { AuthForm } from '@/components/ui/AuthForm';
import { LocalMerchOrder } from '@/types/api';
import { getMerchOrders } from '@/lib/storage';
import { useText } from '@/providers/DictionaryProvider';

export default function AccountScreen() {
  const { isAuthenticated, username, logout } = useAuth();
  const [orders, setOrders] = useState<LocalMerchOrder[]>([]);
  const t = useText();

  useFocusEffect(
    useCallback(() => {
      getMerchOrders().then(setOrders);
    }, []),
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
      {orders.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>
            {t('account.empty.orders', 'Brak zamowien. Zajrzyj do zakladki Merch.')}
          </Text>
        </View>
      ) : null}

      {orders.map((order) => (
        <View key={order.id} style={styles.card}>
          <Text style={styles.orderTitle}>Zamówienie #{order.orderRef.slice(0, 8)}</Text>
          <Text style={styles.meta}>{new Date(order.createdAt).toLocaleString()}</Text>
          <Text style={styles.amount}>
            {order.totalAmount.toFixed(2)} {order.currency}
          </Text>
          <Text style={styles.meta}>Pozycje: {order.items.length}</Text>
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
    gap: 4,
    borderWidth: 1,
    borderColor: '#334155',
  },
  orderTitle: {
    color: '#f8fafc',
    fontWeight: '600',
  },
  amount: {
    color: '#22d3ee',
    fontWeight: '700',
  },
  meta: {
    color: '#cbd5e1',
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
