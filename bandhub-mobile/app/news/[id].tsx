import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, Text } from 'react-native';
import { fetchNews } from '@/lib/api';
import { News } from '@/types/api';
import { Screen } from '@/components/ui/Screen';

export default function NewsDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [loading, setLoading] = useState(true);
  const [news, setNews] = useState<News | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      return;
    }

    fetchNews(id)
      .then(setNews)
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania newsa'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <ActivityIndicator color="#38bdf8" />
      </Screen>
    );
  }

  if (!news || error) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <Text style={styles.error}>{error ?? 'Nie znaleziono newsa'}</Text>
      </Screen>
    );
  }

  return (
    <Screen>
      <Text style={styles.title}>{news.title}</Text>
      <Text style={styles.meta}>{new Date(news.publishedDate).toLocaleString()}</Text>
      <Text style={styles.content}>{news.content}</Text>
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
    marginBottom: 8,
  },
  content: {
    color: '#e2e8f0',
    lineHeight: 22,
  },
  error: {
    color: '#fda4af',
  },
});
