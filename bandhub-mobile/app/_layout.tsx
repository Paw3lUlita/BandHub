import { DarkTheme, DefaultTheme, ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { AuthProvider } from '@/providers/AuthProvider';
import { BrandingProvider } from '@/providers/BrandingProvider';
import { CartProvider } from '@/providers/CartProvider';
import { DictionaryProvider } from '@/providers/DictionaryProvider';
import 'react-native-reanimated';
import { useColorScheme } from '@/components/useColorScheme';

export {
  // Catch any errors thrown by the Layout component.
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
  const colorScheme = useColorScheme();

  return (
    <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
      <Stack>
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
