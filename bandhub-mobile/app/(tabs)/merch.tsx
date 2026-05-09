import { Link } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { fetchProductsPage } from '@/lib/api';
import { Product } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { RequireAuth } from '@/components/ui/RequireAuth';
import { useCart } from '@/providers/CartProvider';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';

export default function MerchScreen() {
  const { isAuthenticated } = useAuth();
  const { items } = useCart();
  const t = useText();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    if (!isAuthenticated) {
      setProducts([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    fetchProductsPage()
      .then((page) => setProducts(page.content))
      .catch((err) => setError(err instanceof Error ? err.message : 'Błąd pobierania produktów'))
      .finally(() => setLoading(false));
  }, [isAuthenticated]);

  return (
    <Screen>
      <Text style={styles.header}>{t('merch.title', 'Sklep merch')}</Text>

      <RequireAuth
        message={t(
          'merch.gate.message',
          'Zaloguj sie, aby przegladac i kupowac merch oficjalny.',
        )}>
        <View style={styles.headerRow}>
          <Text style={styles.subheader}>{t('merch.subtitle', 'Oficjalny merch zespolu')}</Text>
          <Link href="/cart" asChild>
            <Pressable style={styles.cartButton}>
              <Text style={styles.cartButtonText}>
                {t('merch.button.cart', 'Koszyk')} ({items.length})
              </Text>
            </Pressable>
          </Link>
        </View>

        {loading ? (
          <View style={styles.center}>
            <ActivityIndicator size="large" color="#38bdf8" />
          </View>
        ) : null}

        {error ? <Text style={styles.error}>{error}</Text> : null}

        {!loading && !error && products.length === 0 ? (
          <Text style={styles.meta}>{t('merch.empty', 'Brak produktow w sklepie.')}</Text>
        ) : null}

        {products.map((product) => (
          <Link
            key={product.id}
            href={{ pathname: '/products/[id]', params: { id: product.id } }}
            asChild>
            <Pressable style={styles.card}>
              <Text style={styles.title}>{product.name}</Text>
              <Text style={styles.meta}>
                {product.price} {product.currency}
              </Text>
              <Text style={styles.meta}>
                {t('merch.label.stock', 'Stan')}: {product.stockQuantity}
              </Text>
            </Pressable>
          </Link>
        ))}
      </RequireAuth>
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 24,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  header: {
    color: '#f8fafc',
    fontSize: 22,
    fontWeight: '700',
  },
  subheader: {
    color: '#94a3b8',
    fontSize: 14,
  },
  cartButton: {
    backgroundColor: '#0ea5e9',
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  cartButtonText: {
    color: '#082f49',
    fontWeight: '600',
  },
  card: {
    backgroundColor: '#1e293b',
    borderRadius: 12,
    padding: 12,
    gap: 3,
  },
  title: {
    color: '#f8fafc',
    fontSize: 16,
    fontWeight: '600',
  },
  meta: {
    color: '#cbd5e1',
  },
  error: {
    color: '#fda4af',
  },
});
