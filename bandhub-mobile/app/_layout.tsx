import { DarkTheme, ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { AuthProvider } from '@/providers/AuthProvider';
import { BrandingProvider } from '@/providers/BrandingProvider';
import { CartProvider } from '@/providers/CartProvider';
import { DictionaryProvider } from '@/providers/DictionaryProvider';
import { colors } from '@/constants/theme';
import 'react-native-reanimated';

const BandHubTheme = {
  ...DarkTheme,
  colors: {
    ...DarkTheme.colors,
    primary: colors.primary,
    background: colors.background,
    card: colors.backgroundElevated,
    text: colors.text,
    border: colors.border,
    notification: colors.accent,
  },
};

export {
  ErrorBoundary,
} from 'expo-router';

export const unstable_settings = {
  initialRouteName: '(tabs)',
};

export default function RootLayout() {
  return (
    <AuthProvider>
      <DictionaryProvider>
        <BrandingProvider>
          <CartProvider>
            <RootLayoutNav />
          </CartProvider>
        </BrandingProvider>
      </DictionaryProvider>
    </AuthProvider>
  );
}

function RootLayoutNav() {
  return (
    <ThemeProvider value={BandHubTheme}>
      <Stack
        screenOptions={{
          headerStyle: { backgroundColor: colors.backgroundElevated },
          headerTintColor: colors.text,
          headerTitleStyle: { fontWeight: '700' },
          contentStyle: { backgroundColor: colors.background },
        }}>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="news/[id]" options={{ title: 'Aktualnosc' }} />
        <Stack.Screen name="setlists/[id]" options={{ title: 'Setlista' }} />
        <Stack.Screen name="concerts/[id]" options={{ title: 'Koncert' }} />
        <Stack.Screen name="products/[id]" options={{ title: 'Produkt' }} />
        <Stack.Screen name="cart" options={{ title: 'Koszyk' }} />
        <Stack.Screen name="checkout" options={{ title: 'Checkout' }} />
        <Stack.Screen name="tickets/[id]" options={{ title: 'Bilet' }} />
      </Stack>
    </ThemeProvider>
  );
}
