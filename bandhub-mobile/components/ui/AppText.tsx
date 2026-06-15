import { colors, radius, spacing, typography } from '@/constants/theme';
import { StyleSheet, Text, TextProps } from 'react-native';

type Variant = 'hero' | 'h1' | 'h2' | 'h3' | 'body' | 'caption' | 'label' | 'error' | 'success';

type AppTextProps = TextProps & {
  variant?: Variant;
  muted?: boolean;
};

const variantStyles: Record<Variant, object> = {
  hero: typography.hero,
  h1: typography.h1,
  h2: typography.h2,
  h3: typography.h3,
  body: typography.body,
  caption: typography.caption,
  label: typography.label,
  error: { ...typography.body, color: colors.error },
  success: { ...typography.body, color: colors.success },
};

export function AppText({ variant = 'body', muted, style, ...props }: AppTextProps) {
  return (
    <Text
      style={[variantStyles[variant], muted && { color: colors.textMuted }, style]}
      {...props}
    />
  );
}

export const textStyles = StyleSheet.create({
  hero: typography.hero,
  h1: typography.h1,
  h2: typography.h2,
  h3: typography.h3,
  body: typography.body,
  caption: typography.caption,
  label: typography.label,
});
