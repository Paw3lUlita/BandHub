import { Link, useRouter } from 'expo-router';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import { fetchConcert, fetchConcertSetlists, purchaseTickets } from '@/lib/api';
import { ConcertDetail, Setlist } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';
import { ApiError } from '@/lib/http';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { LoadingView } from '@/components/ui/LoadingView';
import { PriceTag } from '@/components/ui/PriceTag';
import { PrimaryButton } from '@/components/ui/PrimaryButton';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { colors, radius, spacing } from '@/constants/theme';

export default function ConcertDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { token, isAuthenticated } = useAuth();
  const t = useText();
  const [loading, setLoading] = useState(true);
  const [detail, setDetail] = useState<ConcertDetail | null>(null);
  const [setlists, setSetlists] = useState<Setlist[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [poolId, setPoolId] = useState<string | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [result, setResult] = useState<string | null>(null);
  const [purchaseSuccess, setPurchaseSuccess] = useState(false);

  useEffect(() => {
    if (!id) {
      return;
    }

    Promise.all([fetchConcert(id), fetchConcertSetlists(id)])
      .then(([concert, concertSetlists]) => {
        setDetail(concert);
        setSetlists(concertSetlists);
        setPoolId(concert.ticketPools[0]?.id ?? null);
      })
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania koncertu'))
      .finally(() => setLoading(false));
  }, [id]);

  const selectedPool = useMemo(
    () => detail?.ticketPools.find((pool) => pool.id === poolId) ?? null,
    [detail, poolId],
  );

  const handlePurchase = async () => {
    if (!id || !detail || !poolId || !token) {
      setResult('Zaloguj sie, aby kupic bilet.');
      return;
    }

    setResult(null);
    setPurchaseSuccess(false);
    try {
      const response = await purchaseTickets(id, { [poolId]: quantity }, token);
      setPurchaseSuccess(true);
      setResult(`Zakup zakonczony. Kody: ${response.ticketCodes.join(', ')}`);
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setResult(t('auth.session.expired', 'Twoja sesja wygasla. Zaloguj sie ponownie.'));
      } else {
        setResult(err instanceof Error ? err.message : 'Nie udalo sie kupic biletu');
      }
    }
  };

  if (loading) {
    return (
      <Screen scroll={false}>
        <LoadingView />
      </Screen>
    );
  }

  if (!detail || error) {
    return (
      <Screen scroll={false}>
        <AppText variant="error">{error ?? 'Brak koncertu'}</AppText>
      </Screen>
    );
  }

  return (
    <Screen>
      <AppText variant="h1">{detail.name}</AppText>
      <AppText variant="caption" muted>{new Date(detail.date).toLocaleString()}</AppText>
      <AppText variant="caption" muted>
        {detail.venueName} · {detail.venueCity}
      </AppText>
      <AppText variant="body">{detail.description ?? 'Brak opisu koncertu.'}</AppText>

      <SectionHeader title={t('concert.section.setlist', 'Setlista koncertu')} />
      {setlists.length === 0 ? (
        <EmptyState message={t('concert.empty.setlist', 'Setlista jeszcze nie zostala opublikowana.')} />
      ) : (
        setlists.map((setlist) => (
          <Link
            key={setlist.id}
            href={{ pathname: '/setlists/[id]', params: { id: setlist.id } }}
            asChild>
            <Pressable>
              <Card>
                <AppText variant="h3">{setlist.title}</AppText>
                {setlist.publishedAt ? (
                  <AppText variant="caption" muted>
                    {new Date(setlist.publishedAt).toLocaleDateString()}
                  </AppText>
                ) : null}
              </Card>
            </Pressable>
          </Link>
        ))
      )}

      <SectionHeader title="Wybierz pule" />
      {detail.ticketPools.map((pool) => (
        <Pressable
          key={pool.id}
          onPress={() => setPoolId(pool.id)}
          style={[styles.poolCard, poolId === pool.id && styles.poolCardActive]}>
          <AppText variant="h3">{pool.name}</AppText>
          <PriceTag amount={pool.price} currency={pool.currency} />
          <AppText variant="caption" muted>
            Wolne: {pool.remainingQuantity}/{pool.totalQuantity}
          </AppText>
        </Pressable>
      ))}

      {isAuthenticated ? (
        <>
          <View style={styles.row}>
            <Pressable onPress={() => setQuantity((v) => Math.max(1, v - 1))} style={styles.qtyButton}>
              <AppText variant="h3">-</AppText>
            </Pressable>
            <AppText variant="body">Ilosc: {quantity}</AppText>
            <Pressable
              onPress={() =>
                setQuantity((v) =>
                  selectedPool ? Math.min(selectedPool.remainingQuantity || 1, v + 1) : v + 1,
                )
              }
              style={styles.qtyButton}>
              <AppText variant="h3">+</AppText>
            </Pressable>
          </View>

          <PrimaryButton label="Kup bilet" onPress={handlePurchase} />
        </>
      ) : (
        <AppText variant="caption" muted style={{ fontStyle: 'italic' }}>
          Zaloguj sie w zakladce Konto, aby kupic bilet.
        </AppText>
      )}

      {result ? (
        <AppText variant={purchaseSuccess ? 'success' : 'error'}>{result}</AppText>
      ) : null}

      {purchaseSuccess ? (
        <PrimaryButton
          label={t('concert.cta.viewMyTickets', 'Zobacz moje bilety')}
          onPress={() => router.push('/(tabs)/tickets')}
          variant="secondary"
        />
      ) : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  poolCard: {
    backgroundColor: colors.surface,
    borderRadius: radius.lg,
    padding: spacing.md,
    gap: spacing.xs,
    borderWidth: 1,
    borderColor: colors.border,
  },
  poolCardActive: {
    borderColor: colors.accent,
    backgroundColor: colors.surfaceHover,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: spacing.md,
  },
  qtyButton: {
    backgroundColor: colors.surfaceHover,
    borderRadius: radius.sm,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: colors.border,
  },
});
