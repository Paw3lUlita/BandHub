import { Link } from 'expo-router';
import { useEffect, useState } from 'react';
import { Image, Pressable, StyleSheet, View } from 'react-native';
import { fetchNewsPage } from '@/lib/api';
import { News } from '@/types/api';
import { absoluteApiUrl } from '@/lib/config';
import { Screen } from '@/components/ui/Screen';
import { useBranding } from '@/providers/BrandingProvider';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { LoadingView } from '@/components/ui/LoadingView';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { colors, radius, spacing } from '@/constants/theme';

export default function HomeScreen() {
  const { settings, isLoading: brandingLoading } = useBranding();
  const t = useText();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [news, setNews] = useState<News[]>([]);

  useEffect(() => {
    fetchNewsPage()
      .then((newsPage) => setNews(newsPage.content.slice(0, 5)))
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania danych'))
      .finally(() => setLoading(false));
  }, []);

  if (loading || brandingLoading) {
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

  const heroUrl = settings?.heroImageUrl ? absoluteApiUrl(settings.heroImageUrl) : null;

  return (
    <Screen>
      <View style={styles.heroWrap}>
        {heroUrl ? (
          <Image source={{ uri: heroUrl }} style={styles.heroImage} />
        ) : (
          <View style={[styles.heroImage, styles.heroPlaceholder]} />
        )}
        <View style={styles.heroOverlay}>
          <AppText variant="hero">{settings?.bandName ?? 'BandHub'}</AppText>
          {settings?.tagline ? <AppText variant="caption" style={styles.tagline}>{settings.tagline}</AppText> : null}
        </View>
      </View>

      {settings?.aboutText ? (
        <Card>
          <AppText variant="body">{settings.aboutText}</AppText>
        </Card>
      ) : null}

      <SectionHeader title={t('home.section.news', 'Aktualnosci')} />
      {news.length === 0 ? (
        <EmptyState message={t('home.empty.news', 'Brak aktualnosci.')} />
      ) : null}
      {news.map((item) => (
        <Link key={item.id} href={{ pathname: '/news/[id]', params: { id: item.id } }} asChild>
          <Pressable>
            <Card accent>
              <AppText variant="h3">{item.title}</AppText>
              <AppText variant="caption" muted numberOfLines={2}>
                {item.content}
              </AppText>
            </Card>
          </Pressable>
        </Link>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  heroWrap: {
    borderRadius: radius.xl,
    overflow: 'hidden',
    marginBottom: spacing.xs,
    ...{
      shadowColor: colors.primary,
      shadowOffset: { width: 0, height: 8 },
      shadowOpacity: 0.25,
      shadowRadius: 16,
      elevation: 8,
    },
  },
  heroImage: {
    width: '100%',
    height: 200,
    backgroundColor: colors.surface,
  },
  heroPlaceholder: {
    backgroundColor: colors.primary + '33',
  },
  heroOverlay: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    padding: spacing.lg,
    backgroundColor: 'rgba(11,17,32,0.75)',
    gap: 4,
  },
  tagline: {
    color: colors.textMuted,
  },
});
