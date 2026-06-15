import { colors, radius, spacing, typography } from '@/constants/theme';
import { StyleSheet, Text, View } from 'react-native';

type StatusPillProps = {
  label: string;
  tone?: 'default' | 'success' | 'warning' | 'error' | 'info';
};

const toneMap = {
  default: { bg: colors.surface, fg: colors.textMuted, border: colors.border },
  success: { bg: '#14532d44', fg: colors.success, border: '#22c55e55' },
  warning: { bg: '#78350f44', fg: colors.warning, border: '#fbbf2455' },
  error: { bg: '#88133744', fg: colors.error, border: '#f43f5e55' },
  info: { bg: '#0c4a6e44', fg: colors.accent, border: '#06b6d455' },
};

export function StatusPill({ label, tone = 'default' }: StatusPillProps) {
  const t = toneMap[tone];
  return (
    <View style={[styles.pill, { backgroundColor: t.bg, borderColor: t.border }]}>
      <Text style={[styles.text, { color: t.fg }]}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  pill: {
    alignSelf: 'flex-start',
    paddingHorizontal: spacing.sm,
    paddingVertical: 4,
    borderRadius: radius.full,
    borderWidth: 1,
  },
  text: {
    ...typography.caption,
    fontWeight: '700',
    fontSize: 11,
    textTransform: 'uppercase',
    letterSpacing: 0.6,
  },
});
