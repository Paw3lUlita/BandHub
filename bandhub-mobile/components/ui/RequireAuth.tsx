import { useRouter } from 'expo-router';
import { ReactNode } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';

type Props = {
  title?: string;
  message?: string;
  children: ReactNode;
};

/**
 * Pokazuje child elementy gdy fan jest zalogowany. Inaczej wyswietla
 * informacje wymuszajaca logowanie z przyciskiem do zakladki Konto.
 * Domyslne teksty (title/message/cta) trzymane sa w slowniku UI - mozna je
 * zmienic z panelu admina bez ruszania kodu.
 */
export function RequireAuth({ title, message, children }: Props) {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();
  const t = useText();

  if (isLoading) {
    return null;
  }

  if (isAuthenticated) {
    return <>{children}</>;
  }

  const resolvedTitle = title ?? t('require_auth.title', 'Wymagane logowanie');
  const resolvedMessage =
    message ??
    t(
      'require_auth.message',
      'Aby korzystac z tej zakladki, musisz byc zalogowanym fanem BandHub.',
    );
  const ctaLabel = t('require_auth.cta', 'Przejdz do logowania');

  return (
    <View style={styles.card}>
      <Text style={styles.title}>{resolvedTitle}</Text>
      <Text style={styles.message}>{resolvedMessage}</Text>
      <Pressable
        onPress={() => router.push('/(tabs)/account')}
        style={({ pressed }) => [styles.cta, pressed && styles.ctaPressed]}>
        <Text style={styles.ctaText}>{ctaLabel}</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 16,
    padding: 18,
    gap: 10,
    borderWidth: 1,
    borderColor: '#334155',
  },
  title: {
    color: '#f8fafc',
    fontSize: 18,
    fontWeight: '700',
  },
  message: {
    color: '#cbd5e1',
    fontSize: 14,
    lineHeight: 20,
  },
  cta: {
    marginTop: 6,
    backgroundColor: '#38bdf8',
    paddingVertical: 11,
    borderRadius: 12,
    alignItems: 'center',
  },
  ctaPressed: {
    opacity: 0.85,
  },
  ctaText: {
    color: '#0f172a',
    fontWeight: '700',
  },
});
