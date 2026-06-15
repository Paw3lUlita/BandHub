import { Link, useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { Pressable } from 'react-native';
import { Screen } from '@/components/ui/Screen';
import { RequireAuth } from '@/components/ui/RequireAuth';
import { fetchMyTicketOrders } from '@/lib/api';
import { MyTicketOrderResponse } from '@/types/api';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { LoadingView } from '@/components/ui/LoadingView';
import { SectionHeader } from '@/components/ui/SectionHeader';

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
      <SectionHeader
        title={t('tickets.title', 'Moje bilety')}
        subtitle={t('tickets.subtitle', 'Historia zakupow z Twojego konta fana.')}
      />
      <RequireAuth
        message={t('tickets.gate.message', 'Zaloguj sie, aby zobaczyc swoje bilety i kody wstepu.')}>
        {loading ? <LoadingView /> : null}
        {!loading && tickets.length === 0 ? (
          <EmptyState message={t('tickets.empty', 'Brak biletow. Kup pierwszy bilet z zakladki Koncerty.')} />
        ) : null}

        {tickets.map((ticket) => (
          <Link
            key={ticket.orderId}
            href={{ pathname: '/tickets/[id]', params: { id: ticket.orderId } }}
            asChild>
            <Pressable>
              <Card accent>
                <AppText variant="h3">{ticket.concertName}</AppText>
                <AppText variant="caption" muted>
                  {t('tickets.label.purchasedAt', 'Kupiono')}: {new Date(ticket.createdAt).toLocaleString()}
                </AppText>
                <AppText variant="caption" muted>
                  {t('tickets.label.codes', 'Kody')}: {ticket.ticketCodes.length}
                </AppText>
              </Card>
            </Pressable>
          </Link>
        ))}
      </RequireAuth>
    </Screen>
  );
}
