import { Link } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text } from 'react-native';
import { fetchConcertsPage } from '@/lib/api';
import { Concert } from '@/types/api';
import { Screen } from '@/components/ui/Screen';

export default function ConcertsScreen() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [concerts, setConcerts] = useState<Concert[]>([]);

  useEffect(() => {
    fetchConcertsPage()
      .then((page) => setConcerts(page.content))
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania koncertow'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <ActivityIndicator size="large" color="#38bdf8" />
      </Screen>
    );
  }

  if (error) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <Text style={styles.error}>{error}</Text>
      </Screen>
    );
  }

  return (
    <Screen>
      <Text style={styles.header}>Nadchodzace koncerty</Text>
      {concerts.map((concert) => (
        <Link
          key={concert.id}
          href={{ pathname: '/concerts/[id]', params: { id: concert.id, name: concert.name } }}
          asChild>
          <Pressable style={styles.card}>
            <Text style={styles.title}>{concert.name}</Text>
            <Text style={styles.meta}>{new Date(concert.date).toLocaleString()}</Text>
            <Text style={styles.meta}>
              {concert.venueName} - {concert.city}
            </Text>
          </Pressable>
        </Link>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
  },
  header: {
    color: '#f8fafc',
    fontSize: 22,
    fontWeight: '700',
    marginBottom: 4,
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 12,
    gap: 3,
  },
  title: {
    color: '#f8fafc',
    fontSize: 16,
    fontWeight: '600',
  },
  meta: {
    color: '#cbd5e1',
  },
  error: {
    color: '#fda4af',
  },
});
