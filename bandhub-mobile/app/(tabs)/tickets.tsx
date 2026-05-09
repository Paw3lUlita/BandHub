import { Link, useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { Pressable, StyleSheet, Text } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { RequireAuth } from '@/components/ui/RequireAuth';
import { getTicketPurchases } from '@/lib/storage';
import { LocalTicketPurchase } from '@/types/api';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';

export default function TicketsScreen() {
  const { isAuthenticated } = useAuth();
  const [tickets, setTickets] = useState<LocalTicketPurchase[]>([]);
  const t = useText();

  useFocusEffect(
    useCallback(() => {
      if (!isAuthenticated) {
        setTickets([]);
        return;
      }
      getTicketPurchases().then(setTickets);
    }, [isAuthenticated]),
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
            'Lokalna historia zakupow z endpointu /api/public/ticket-orders',
          )}
        </Text>

        {tickets.length === 0 ? (
          <Text style={styles.empty}>
            {t('tickets.empty', 'Brak biletow. Kup pierwszy bilet z zakladki Koncerty.')}
          </Text>
        ) : null}

        {tickets.map((ticket) => (
          <Link
            key={ticket.id}
            href={{ pathname: '/tickets/[id]', params: { id: ticket.id } }}
            asChild>
            <Pressable style={styles.card}>
              <Text style={styles.title}>{ticket.concertName}</Text>
              <Text style={styles.meta}>
                {t('tickets.label.purchasedAt', 'Kupiono')}: {new Date(ticket.purchasedAt).toLocaleString()}
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
