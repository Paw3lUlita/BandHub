import { Link } from 'expo-router';
import { useEffect, useState } from 'react';
import { Pressable } from 'react-native';
import { fetchConcertsPage } from '@/lib/api';
import { Concert } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { LoadingView } from '@/components/ui/LoadingView';
import { SectionHeader } from '@/components/ui/SectionHeader';

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
      <Screen scroll={false}>
        <LoadingView />
      </Screen>
    );
  }

  if (error) {
    return (
      <Screen scroll={false}>
        <AppText variant="error">{error}</AppText>
      </Screen>
    );
  }

  return (
    <Screen>
      <SectionHeader title="Nadchodzace koncerty" subtitle="Wybierz wydarzenie i kup bilet" />
      {concerts.length === 0 ? <EmptyState message="Brak zaplanowanych koncertow." /> : null}
      {concerts.map((concert) => (
        <Link
          key={concert.id}
          href={{ pathname: '/concerts/[id]', params: { id: concert.id, name: concert.name } }}
          asChild>
          <Pressable>
            <Card accent>
              <AppText variant="h3">{concert.name}</AppText>
              <AppText variant="caption" muted>
                {new Date(concert.date).toLocaleString()}
              </AppText>
              <AppText variant="caption" muted>
                {concert.venueName} · {concert.city}
              </AppText>
            </Card>
          </Pressable>
        </Link>
      ))}
    </Screen>
  );
}
