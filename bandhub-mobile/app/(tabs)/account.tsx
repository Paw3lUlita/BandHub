import { useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { View } from 'react-native';
import { useAuth } from '@/providers/AuthProvider';
import { Screen } from '@/components/ui/Screen';
import { AuthForm } from '@/components/ui/AuthForm';
import { MyOrderResponse, OrderStatus } from '@/types/api';
import { fetchMyOrders } from '@/lib/api';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { LoadingView } from '@/components/ui/LoadingView';
import { PrimaryButton } from '@/components/ui/PrimaryButton';
import { PriceTag } from '@/components/ui/PriceTag';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { StatusPill } from '@/components/ui/StatusPill';

function statusLabel(status: OrderStatus, t: (key: string, fallback: string) => string): string {
  return t(`order.status.${status}`, status);
}

function statusTone(status: OrderStatus): 'default' | 'success' | 'warning' | 'error' | 'info' {
  switch (status) {
    case 'PAID':
      return 'success';
    case 'SHIPPED':
      return 'info';
    case 'DELIVERED':
      return 'success';
    case 'CANCELLED':
      return 'error';
    default:
      return 'warning';
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
        <SectionHeader
          title={t('account.title.guest', 'Witaj w BandHub')}
          subtitle={t('account.subtitle.guest', 'Zaloguj sie lub zaloz konto fana, aby kupowac bilety i merch.')}
        />
        <AuthForm />
      </Screen>
    );
  }

  return (
    <Screen>
      <Card accent>
        <AppText variant="h2">
          {t('account.greeting', 'Witaj')}
          {username ? `, ${username}` : ''}!
        </AppText>
        <AppText variant="caption" muted>
          {t('account.subtitle.user', 'Jestes zalogowany jako fan BandHub.')}
        </AppText>
        <PrimaryButton label={t('account.button.logout', 'Wyloguj')} onPress={logout} variant="danger" />
      </Card>

      <SectionHeader title={t('account.section.orders', 'Moje zamowienia merch')} />

      {loadingOrders ? <LoadingView /> : null}

      {!loadingOrders && orders.length === 0 ? (
        <EmptyState message={t('account.empty.orders', 'Brak zamowien. Zajrzyj do zakladki Merch.')} />
      ) : null}

      {orders.map((order) => (
        <Card key={order.id}>
          <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', gap: 8 }}>
            <AppText variant="h3" style={{ flex: 1 }}>
              Zamowienie #{order.id.slice(0, 8)}
            </AppText>
            <StatusPill label={statusLabel(order.status, t)} tone={statusTone(order.status)} />
          </View>
          <AppText variant="caption" muted>{new Date(order.createdAt).toLocaleString()}</AppText>
          <PriceTag amount={Number(order.totalAmount).toFixed(2)} currency={order.currency} large />
          {order.items.map((item) => (
            <AppText key={item.productId} variant="caption" muted>
              {item.quantity}x {item.productName}
            </AppText>
          ))}
        </Card>
      ))}
    </Screen>
  );
}
