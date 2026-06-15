import { Link, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { Pressable, StyleSheet, View } from 'react-native';
import { fetchProductsPage } from '@/lib/api';
import { Product } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { RequireAuth } from '@/components/ui/RequireAuth';
import { useCart } from '@/providers/CartProvider';
import { useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { EmptyState } from '@/components/ui/EmptyState';
import { LoadingView } from '@/components/ui/LoadingView';
import { PriceTag } from '@/components/ui/PriceTag';
import { SectionHeader } from '@/components/ui/SectionHeader';
import { colors, radius, spacing } from '@/constants/theme';

export default function MerchScreen() {
  const router = useRouter();
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
      <View style={styles.headerRow}>
        <SectionHeader
          title={t('merch.title', 'Sklep merch')}
          subtitle={t('merch.subtitle', 'Oficjalny merch zespolu')}
        />
        <Pressable style={styles.cartBtn} onPress={() => router.push('/cart')}>
          <AppText variant="caption" style={styles.cartBtnText}>
            {t('merch.button.cart', 'Koszyk')} ({items.length})
          </AppText>
        </Pressable>
      </View>

      <RequireAuth
        message={t('merch.gate.message', 'Zaloguj sie, aby przegladac i kupowac merch oficjalny.')}>
        {loading ? <LoadingView /> : null}
        {error ? <AppText variant="error">{error}</AppText> : null}
        {!loading && !error && products.length === 0 ? (
          <EmptyState message={t('merch.empty', 'Brak produktow w sklepie.')} />
        ) : null}

        {products.map((product) => (
          <Link
            key={product.id}
            href={{ pathname: '/products/[id]', params: { id: product.id } }}
            asChild>
            <Pressable>
              <Card>
                <AppText variant="h3">{product.name}</AppText>
                <View style={styles.metaRow}>
                  <PriceTag amount={product.price} currency={product.currency} />
                  <AppText variant="caption" muted>
                    {t('merch.label.stock', 'Stan')}: {product.stockQuantity}
                  </AppText>
                </View>
              </Card>
            </Pressable>
          </Link>
        ))}
      </RequireAuth>
    </Screen>
  );
}

const styles = StyleSheet.create({
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    gap: spacing.sm,
  },
  cartBtn: {
    backgroundColor: colors.accent,
    borderRadius: radius.md,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    marginTop: spacing.md,
    alignSelf: 'flex-start',
  },
  cartBtnText: {
    color: colors.background,
    fontWeight: '700',
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: spacing.xs,
  },
});
