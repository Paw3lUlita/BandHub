import { Link, useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { RequireAuth } from '@/components/ui/RequireAuth';
import { fetchMyTicketOrders } from '@/lib/api';
import { MyTicketOrderResponse } from '@/types/api';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';

export default function TicketsScreen() {
  const { isAuthenticated, token } = useAuth();
  const [tickets, setTickets] = useState<MyTicketOrderResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const t = useText();

  useFocusEffect(
    useCallback(() => {
      if (!isAuthenticated || !token) {
        setTickets([]);
        return;
      }
      setLoading(true);
      fetchMyTicketOrders(token)
        .then(setTickets)
        .catch(() => setTickets([]))
        .finally(() => setLoading(false));
    }, [isAuthenticated, token]),
  );

  return (
    <Screen>
      <Text style={styles.header}>{t('tickets.title', 'Moje bilety')}</Text>
      <RequireAuth
        message={t(
          'tickets.gate.message',
          'Zaloguj sie, aby zobaczyc swoje bilety i kody wstepu.',
        )}>
        <Text style={styles.subheader}>
          {t(
            'tickets.subtitle',
            'Historia zakupow z Twojego konta fana.',
          )}
        </Text>

        {loading ? <ActivityIndicator color="#38bdf8" style={{ marginVertical: 12 }} /> : null}

        {!loading && tickets.length === 0 ? (
          <Text style={styles.empty}>
            {t('tickets.empty', 'Brak biletow. Kup pierwszy bilet z zakladki Koncerty.')}
          </Text>
        ) : null}

        {tickets.map((ticket) => (
          <Link
            key={ticket.orderId}
            href={{ pathname: '/tickets/[id]', params: { id: ticket.orderId } }}
            asChild>
            <Pressable style={styles.card}>
              <Text style={styles.title}>{ticket.concertName}</Text>
              <Text style={styles.meta}>
                {t('tickets.label.purchasedAt', 'Kupiono')}:{' '}
                {new Date(ticket.createdAt).toLocaleString()}
              </Text>
              <Text style={styles.meta}>
                {t('tickets.label.codes', 'Kody')}: {ticket.ticketCodes.length}
              </Text>
            </Pressable>
          </Link>
        ))}
      </RequireAuth>
    </Screen>
  );
}

const styles = StyleSheet.create({
  header: {
    color: '#f8fafc',
    fontSize: 22,
    fontWeight: '700',
  },
  subheader: {
    color: '#94a3b8',
    fontSize: 13,
  },
  empty: {
    color: '#cbd5e1',
    marginTop: 12,
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 12,
    gap: 4,
  },
  title: {
    color: '#f8fafc',
    fontSize: 16,
    fontWeight: '600',
  },
  meta: {
    color: '#cbd5e1',
  },
});
