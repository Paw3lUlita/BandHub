import { useRouter } from 'expo-router';
import { ReactNode } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { colors, radius, spacing } from '@/constants/theme';

type Props = {
  title?: string;
  message?: string;
  children: ReactNode;
};

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
    t('require_auth.message', 'Aby korzystac z tej zakladki, musisz byc zalogowanym fanem BandHub.');
  const ctaLabel = t('require_auth.cta', 'Przejdz do logowania');

  return (
    <View style={styles.card}>
      <AppText variant="h3">{resolvedTitle}</AppText>
      <AppText variant="body" muted>
        {resolvedMessage}
      </AppText>
      <Pressable
        onPress={() => router.push('/(tabs)/account')}
        style={({ pressed }) => [styles.cta, pressed && styles.ctaPressed]}>
        <AppText variant="caption" style={styles.ctaText}>
          {ctaLabel}
        </AppText>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: radius.xl,
    padding: spacing.lg,
    gap: spacing.sm,
    borderWidth: 1,
    borderColor: colors.border,
  },
  cta: {
    marginTop: spacing.xs,
    backgroundColor: colors.primary,
    paddingVertical: spacing.md,
    borderRadius: radius.md,
    alignItems: 'center',
  },
  ctaPressed: {
    opacity: 0.85,
  },
  ctaText: {
    color: '#fff',
    fontWeight: '700',
  },
});
