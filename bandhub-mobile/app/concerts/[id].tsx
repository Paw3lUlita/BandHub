import { Link, useRouter } from 'expo-router';
import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { fetchConcert, fetchConcertSetlists, purchaseTickets } from '@/lib/api';
import { ConcertDetail, Setlist } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';
import { ApiError } from '@/lib/http';

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
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <ActivityIndicator color="#38bdf8" />
      </Screen>
    );
  }

  if (!detail || error) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <Text style={styles.error}>{error ?? 'Brak koncertu'}</Text>
      </Screen>
    );
  }

  return (
    <Screen>
      <Text style={styles.title}>{detail.name}</Text>
      <Text style={styles.meta}>{new Date(detail.date).toLocaleString()}</Text>
      <Text style={styles.meta}>
        {detail.venueName} - {detail.venueCity}
      </Text>
      <Text style={styles.description}>{detail.description ?? 'Brak opisu koncertu.'}</Text>

      <Text style={styles.section}>{t('concert.section.setlist', 'Setlista koncertu')}</Text>
      {setlists.length === 0 ? (
        <Text style={styles.emptySetlist}>
          {t('concert.empty.setlist', 'Setlista jeszcze nie zostala opublikowana.')}
        </Text>
      ) : (
        setlists.map((setlist) => (
          <Link
            key={setlist.id}
            href={{ pathname: '/setlists/[id]', params: { id: setlist.id } }}
            asChild>
            <Pressable style={styles.setlistCard}>
              <Text style={styles.setlistTitle}>{setlist.title}</Text>
              {setlist.publishedAt ? (
                <Text style={styles.setlistMeta}>
                  {new Date(setlist.publishedAt).toLocaleDateString()}
                </Text>
              ) : null}
            </Pressable>
          </Link>
        ))
      )}

      <Text style={styles.section}>Wybierz pule</Text>
      {detail.ticketPools.map((pool) => (
        <Pressable
          key={pool.id}
          onPress={() => setPoolId(pool.id)}
          style={[styles.poolCard, poolId === pool.id ? styles.poolCardActive : undefined]}>
          <Text style={styles.poolName}>{pool.name}</Text>
          <Text style={styles.poolMeta}>
            {pool.price} {pool.currency}, wolne: {pool.remainingQuantity}/{pool.totalQuantity}
          </Text>
        </Pressable>
      ))}

      {isAuthenticated ? (
        <>
          <View style={styles.row}>
            <Pressable onPress={() => setQuantity((v) => Math.max(1, v - 1))} style={styles.qtyButton}>
              <Text style={styles.qtyText}>-</Text>
            </Pressable>
            <Text style={styles.qtyValue}>Ilosc: {quantity}</Text>
            <Pressable
              onPress={() =>
                setQuantity((v) =>
                  selectedPool ? Math.min(selectedPool.remainingQuantity || 1, v + 1) : v + 1,
                )
              }
              style={styles.qtyButton}>
              <Text style={styles.qtyText}>+</Text>
            </Pressable>
          </View>

          <Pressable onPress={handlePurchase} style={styles.buyButton}>
            <Text style={styles.buyText}>Kup bilet</Text>
          </Pressable>
        </>
      ) : (
        <Text style={styles.loginHint}>Zaloguj sie w zakladce Konto, aby kupic bilet.</Text>
      )}

      {result ? <Text style={purchaseSuccess ? styles.resultOk : styles.result}>{result}</Text> : null}

      {purchaseSuccess ? (
        <Pressable
          onPress={() => router.push('/(tabs)/tickets')}
          style={styles.viewTicketsButton}>
          <Text style={styles.viewTicketsText}>
            {t('concert.cta.viewMyTickets', 'Zobacz moje bilety')}
          </Text>
        </Pressable>
      ) : null}
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
  description: {
    color: '#e2e8f0',
    marginTop: 6,
  },
  section: {
    color: '#e2e8f0',
    fontSize: 18,
    fontWeight: '600',
    marginTop: 12,
  },
  emptySetlist: {
    color: '#64748b',
    fontStyle: 'italic',
  },
  setlistCard: {
    backgroundColor: '#1e293b',
    borderRadius: 10,
    padding: 10,
    gap: 2,
    borderWidth: 1,
    borderColor: '#334155',
  },
  setlistTitle: {
    color: '#f8fafc',
    fontWeight: '600',
  },
  setlistMeta: {
    color: '#94a3b8',
    fontSize: 12,
  },
  poolCard: {
    backgroundColor: '#1e293b',
    borderRadius: 10,
    padding: 10,
    gap: 3,
    borderWidth: 1,
    borderColor: '#1e293b',
  },
  poolCardActive: {
    borderColor: '#38bdf8',
  },
  poolName: {
    color: '#f8fafc',
    fontWeight: '600',
  },
  poolMeta: {
    color: '#cbd5e1',
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  qtyButton: {
    backgroundColor: '#334155',
    borderRadius: 8,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  qtyText: {
    color: '#e2e8f0',
    fontSize: 20,
    fontWeight: '700',
  },
  qtyValue: {
    color: '#e2e8f0',
  },
  buyButton: {
    backgroundColor: '#22c55e',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  buyText: {
    color: '#052e16',
    fontWeight: '700',
  },
  loginHint: {
    color: '#94a3b8',
    fontStyle: 'italic',
  },
  result: {
    color: '#fda4af',
  },
  resultOk: {
    color: '#86efac',
  },
  viewTicketsButton: {
    backgroundColor: '#0ea5e9',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  viewTicketsText: {
    color: '#082f49',
    fontWeight: '700',
  },
  error: {
    color: '#fda4af',
  },
});
