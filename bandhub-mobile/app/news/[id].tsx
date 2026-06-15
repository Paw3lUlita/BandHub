import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import { Screen } from '@/components/ui/Screen';
import { fetchNews } from '@/lib/api';
import { News } from '@/types/api';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { LoadingView } from '@/components/ui/LoadingView';

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
      <Screen scroll={false}>
        <LoadingView />
      </Screen>
    );
  }

  if (!news || error) {
    return (
      <Screen scroll={false}>
        <AppText variant="error">{error ?? 'Nie znaleziono newsa'}</AppText>
      </Screen>
    );
  }

  return (
    <Screen>
      <AppText variant="h1">{news.title}</AppText>
      <AppText variant="caption" muted>{new Date(news.publishedDate).toLocaleString()}</AppText>
      <Card>
        <AppText variant="body">{news.content}</AppText>
      </Card>
    </Screen>
  );
}
