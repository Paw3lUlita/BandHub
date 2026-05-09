import { Link } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Image, Pressable, StyleSheet, Text, View } from 'react-native';
import { fetchGallery, fetchNewsPage, fetchSetlistsPage } from '@/lib/api';
import { GalleryImage, News, Setlist } from '@/types/api';
import { absoluteApiUrl } from '@/lib/config';
import { Screen } from '@/components/ui/Screen';
import { useBranding } from '@/providers/BrandingProvider';
import { useText } from '@/providers/DictionaryProvider';

export default function HomeScreen() {
  const { settings, isLoading: brandingLoading } = useBranding();
  const t = useText();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [news, setNews] = useState<News[]>([]);
  const [gallery, setGallery] = useState<GalleryImage[]>([]);
  const [setlists, setSetlists] = useState<Setlist[]>([]);

  useEffect(() => {
    Promise.all([fetchNewsPage(), fetchGallery(), fetchSetlistsPage()])
      .then(([newsPage, galleryData, setlistPage]) => {
        setNews(newsPage.content.slice(0, 5));
        setGallery(galleryData.slice(0, 8));
        setSetlists(setlistPage.content.slice(0, 5));
      })
      .catch((err) => {
        setError(err instanceof Error ? err.message : 'Błąd pobierania danych');
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading || brandingLoading) {
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

  const heroUrl = settings?.heroImageUrl ? absoluteApiUrl(settings.heroImageUrl) : null;

  return (
    <Screen>
      {heroUrl ? <Image source={{ uri: heroUrl }} style={styles.hero} /> : null}

      <Text style={styles.header}>{settings?.bandName ?? 'BandHub'}</Text>
      {settings?.tagline ? <Text style={styles.subheader}>{settings.tagline}</Text> : null}

      {settings?.aboutText ? (
        <View style={styles.aboutCard}>
          <Text style={styles.aboutText}>{settings.aboutText}</Text>
        </View>
      ) : null}

      <Text style={styles.section}>{t('home.section.news', 'Aktualnosci')}</Text>
      {news.length === 0 ? (
        <Text style={styles.empty}>{t('home.empty.news', 'Brak aktualnosci.')}</Text>
      ) : null}
      {news.map((item) => (
        <Link key={item.id} href={{ pathname: '/news/[id]', params: { id: item.id } }} asChild>
          <Pressable style={styles.card}>
            <Text style={styles.cardTitle}>{item.title}</Text>
            <Text style={styles.cardText} numberOfLines={2}>
              {item.content}
            </Text>
          </Pressable>
        </Link>
      ))}

      <Text style={styles.section}>{t('home.section.setlists', 'Setlisty')}</Text>
      {setlists.length === 0 ? (
        <Text style={styles.empty}>{t('home.empty.setlists', 'Brak setlist.')}</Text>
      ) : null}
      {setlists.map((item) => (
        <Link key={item.id} href={{ pathname: '/setlists/[id]', params: { id: item.id } }} asChild>
          <Pressable style={styles.card}>
            <Text style={styles.cardTitle}>{item.title}</Text>
            <Text style={styles.cardText}>
              {item.concertName}
              {item.publishedAt ? ` - ${new Date(item.publishedAt).toLocaleDateString()}` : ''}
            </Text>
          </Pressable>
        </Link>
      ))}

      <Text style={styles.section}>{t('home.section.gallery', 'Galeria')}</Text>
      {gallery.length === 0 ? (
        <Text style={styles.empty}>{t('home.empty.gallery', 'Brak zdjec.')}</Text>
      ) : null}
      <View style={styles.galleryRow}>
        {gallery.map((image) => (
          <Image key={image.id} source={{ uri: absoluteApiUrl(image.imageUrl) }} style={styles.thumb} />
        ))}
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
  },
  hero: {
    width: '100%',
    height: 180,
    borderRadius: 14,
    marginBottom: 4,
    backgroundColor: '#1e293b',
  },
  header: {
    color: '#f8fafc',
    fontSize: 26,
    fontWeight: '800',
  },
  subheader: {
    color: '#94a3b8',
    fontSize: 14,
    marginBottom: 8,
  },
  aboutCard: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 14,
    borderWidth: 1,
    borderColor: '#334155',
  },
  aboutText: {
    color: '#cbd5e1',
    lineHeight: 20,
  },
  section: {
    color: '#e2e8f0',
    fontSize: 18,
    fontWeight: '600',
    marginTop: 8,
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 12,
    gap: 4,
  },
  cardTitle: {
    color: '#f8fafc',
    fontSize: 16,
    fontWeight: '600',
  },
  cardText: {
    color: '#cbd5e1',
  },
  empty: {
    color: '#64748b',
    fontStyle: 'italic',
  },
  galleryRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  thumb: {
    width: 100,
    height: 100,
    borderRadius: 8,
    backgroundColor: '#334155',
  },
  error: {
    color: '#fda4af',
  },
});
