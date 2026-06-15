import { AppText } from '@/components/ui/AppText';
import { spacing } from '@/constants/theme';
import { StyleSheet, View } from 'react-native';

type SectionHeaderProps = {
  title: string;
  subtitle?: string;
};

export function SectionHeader({ title, subtitle }: SectionHeaderProps) {
  return (
    <View style={styles.wrap}>
      <AppText variant="h3">{title}</AppText>
      {subtitle ? <AppText variant="caption" muted>{subtitle}</AppText> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    marginTop: spacing.md,
    marginBottom: spacing.xs,
    gap: 2,
  },
});
