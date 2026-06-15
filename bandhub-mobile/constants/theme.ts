export const colors = {
  background: '#0b1120',
  backgroundElevated: '#111827',
  surface: '#1e293b',
  surfaceHover: '#273449',
  border: '#334155',
  borderLight: '#475569',
  text: '#f8fafc',
  textSecondary: '#e2e8f0',
  textMuted: '#94a3b8',
  textDim: '#64748b',
  primary: '#6366f1',
  primaryLight: '#818cf8',
  accent: '#06b6d4',
  accentGreen: '#22c55e',
  accentBlue: '#0ea5e9',
  error: '#fda4af',
  errorDark: '#f43f5e',
  success: '#4ade80',
  warning: '#fbbf24',
  gradientStart: '#6366f1',
  gradientMid: '#8b5cf6',
  gradientEnd: '#06b6d4',
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 24,
};

export const radius = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  full: 999,
};

export const typography = {
  hero: { fontSize: 28, fontWeight: '800' as const, color: colors.text },
  h1: { fontSize: 24, fontWeight: '700' as const, color: colors.text },
  h2: { fontSize: 20, fontWeight: '700' as const, color: colors.text },
  h3: { fontSize: 18, fontWeight: '600' as const, color: colors.textSecondary },
  body: { fontSize: 15, fontWeight: '400' as const, color: colors.textSecondary, lineHeight: 22 },
  caption: { fontSize: 13, fontWeight: '400' as const, color: colors.textMuted },
  label: { fontSize: 12, fontWeight: '600' as const, color: colors.textMuted, textTransform: 'uppercase' as const, letterSpacing: 0.5 },
  mono: { fontFamily: 'monospace' as const },
};

export const shadows = {
  card: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 12,
    elevation: 4,
  },
  glow: {
    shadowColor: colors.primary,
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.35,
    shadowRadius: 16,
    elevation: 6,
  },
};

const theme = { colors, spacing, radius, typography, shadows };
export default theme;
