import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { fetchConcert, purchaseTickets } from '@/lib/api';
import { ConcertDetail, LocalTicketPurchase } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { useAuth } from '@/providers/AuthProvider';
import { saveTicketPurchase } from '@/lib/storage';

export default function ConcertDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { token } = useAuth();
  const [loading, setLoading] = useState(true);
  const [detail, setDetail] = useState<ConcertDetail | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [poolId, setPoolId] = useState<string | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [result, setResult] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      return;
    }

    fetchConcert(id)
      .then((response) => {
        setDetail(response);
        setPoolId(response.ticketPools[0]?.id ?? null);
      })
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania koncertu'))
      .finally(() => setLoading(false));
  }, [id]);

  const selectedPool = useMemo(
    () => detail?.ticketPools.find((pool) => pool.id === poolId) ?? null,
    [detail, poolId],
  );

  const handlePurchase = async () => {
    if (!id || !detail || !poolId) {
      return;
    }

    try {
      const response = await purchaseTickets(id, { [poolId]: quantity }, token);
      const purchase: LocalTicketPurchase = {
        id: `${response.orderId}-${Date.now()}`,
        orderId: response.orderId,
        concertId: detail.id,
        concertName: detail.name,
        purchasedAt: new Date().toISOString(),
        ticketCodes: response.ticketCodes,
      };
      await saveTicketPurchase(purchase);
      setResult(`Zakup zakonczony. Kody: ${response.ticketCodes.join(', ')}`);
    } catch (err) {
      setResult(err instanceof Error ? err.message : 'Nie udalo sie kupic biletu');
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

      {result ? <Text style={styles.result}>{result}</Text> : null}
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
    marginTop: 8,
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
  result: {
    color: '#93c5fd',
  },
  error: {
    color: '#fda4af',
  },
});
