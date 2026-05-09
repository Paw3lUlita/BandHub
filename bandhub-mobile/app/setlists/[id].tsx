import { useLocalSearchParams } from 'expo-router';
import { useEffect, useMemo, useState } from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import { fetchSetlistItems, fetchSetlistsPage } from '@/lib/api';
import { Setlist, SetlistItem } from '@/types/api';
import { Screen } from '@/components/ui/Screen';

export default function SetlistDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const [loading, setLoading] = useState(true);
  const [setlists, setSetlists] = useState<Setlist[]>([]);
  const [items, setItems] = useState<SetlistItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      return;
    }

    Promise.all([fetchSetlistsPage(), fetchSetlistItems(id)])
      .then(([setlistPage, itemsData]) => {
        setSetlists(setlistPage.content);
        setItems(itemsData);
      })
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania setlisty'))
      .finally(() => setLoading(false));
  }, [id]);

  const setlist = useMemo(() => setlists.find((item) => item.id === id), [id, setlists]);
  const orderedItems = useMemo(
    () => [...items].sort((a, b) => a.songOrder - b.songOrder),
    [items],
  );

  if (loading) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <ActivityIndicator color="#38bdf8" />
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
      <Text style={styles.title}>{setlist?.title ?? 'Setlista'}</Text>
      <Text style={styles.meta}>{setlist?.concertName}</Text>
      {orderedItems.map((item) => (
        <View key={item.id} style={styles.itemRow}>
          <Text style={styles.order}>{item.songOrder}.</Text>
          <View style={styles.songColumn}>
            <Text style={styles.song}>{item.songTitle}</Text>
            <Text style={styles.duration}>
              {item.durationSeconds ? `${Math.floor(item.durationSeconds / 60)}:${String(item.durationSeconds % 60).padStart(2, '0')}` : 'czas nieznany'}
            </Text>
          </View>
        </View>
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
  title: {
    color: '#f8fafc',
    fontSize: 24,
    fontWeight: '700',
  },
  meta: {
    color: '#94a3b8',
    marginBottom: 8,
  },
  itemRow: {
    backgroundColor: '#1e293b',
    borderRadius: 10,
    padding: 10,
    flexDirection: 'row',
    gap: 10,
    alignItems: 'center',
  },
  order: {
    color: '#38bdf8',
    width: 28,
    fontWeight: '700',
  },
  songColumn: {
    flex: 1,
  },
  song: {
    color: '#f8fafc',
  },
  duration: {
    color: '#94a3b8',
    fontSize: 12,
  },
  error: {
    color: '#fda4af',
  },
});
