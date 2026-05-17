import { Link, useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { fetchProduct } from '@/lib/api';
import { Product } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';
import { useText } from '@/providers/DictionaryProvider';

export default function ProductDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { addItem } = useCart();
  const t = useText();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [product, setProduct] = useState<Product | null>(null);
  const [added, setAdded] = useState(false);

  useEffect(() => {
    if (!id) {
      return;
    }

    fetchProduct(id)
      .then(setProduct)
      .catch((err) => setError(err instanceof Error ? err.message : 'Blad pobierania produktu'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleAdd = () => {
    if (!product) {
      return;
    }

    addItem(
      {
        productId: product.id,
        name: product.name,
        price: product.price,
        currency: product.currency,
        stockQuantity: product.stockQuantity,
      },
      1,
    );
    setAdded(true);
  };

  if (loading) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <ActivityIndicator color="#38bdf8" />
      </Screen>
    );
  }

  if (!product || error) {
    return (
      <Screen scroll={false} contentContainerStyle={styles.center}>
        <Text style={styles.error}>{error ?? 'Produkt nie istnieje'}</Text>
      </Screen>
    );
  }

  return (
    <Screen>
      <Text style={styles.title}>{product.name}</Text>
      <Text style={styles.price}>
        {product.price} {product.currency}
      </Text>
      <Text style={styles.meta}>Kategoria: {product.categoryName ?? 'brak'}</Text>
      <Text style={styles.meta}>
        {t('merch.label.stock', 'Stan')}: {product.stockQuantity}
      </Text>
      <Text style={styles.desc}>{product.description ?? 'Brak opisu produktu.'}</Text>

      {!added ? (
        <Pressable onPress={handleAdd} style={styles.button}>
          <Text style={styles.buttonText}>Dodaj do koszyka</Text>
        </Pressable>
      ) : (
        <View style={styles.addedBanner}>
          <Text style={styles.addedTitle}>
            {t('product.feedback.added', 'Dodano do koszyka')}
          </Text>
          <Link href="/cart" asChild>
            <Pressable style={styles.ctaPrimary}>
              <Text style={styles.ctaPrimaryText}>
                {t('product.cta.viewCart', 'Przejdz do koszyka')}
              </Text>
            </Pressable>
          </Link>
          <Pressable onPress={() => setAdded(false)} style={styles.ctaSecondary}>
            <Text style={styles.ctaSecondaryText}>
              {t('product.cta.continue', 'Kontynuuj zakupy')}
            </Text>
          </Pressable>
        </View>
      )}
    </Screen>
  );
}

const styles = StyleSheet.create({
  center: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
  },
  title: {
    color: '#f8fafc',
    fontSize: 24,
    fontWeight: '700',
  },
  price: {
    color: '#22d3ee',
    fontWeight: '700',
    fontSize: 20,
  },
  meta: {
    color: '#94a3b8',
  },
  desc: {
    color: '#e2e8f0',
    marginTop: 6,
  },
  button: {
    backgroundColor: '#0ea5e9',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  buttonText: {
    color: '#082f49',
    fontWeight: '700',
  },
  addedBanner: {
    backgroundColor: '#0f172a',
    borderRadius: 12,
    padding: 14,
    gap: 10,
    borderWidth: 1,
    borderColor: '#334155',
  },
  addedTitle: {
    color: '#86efac',
    fontWeight: '600',
  },
  ctaPrimary: {
    backgroundColor: '#22c55e',
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
  },
  ctaPrimaryText: {
    color: '#052e16',
    fontWeight: '700',
  },
  ctaSecondary: {
    backgroundColor: '#334155',
    borderRadius: 10,
    paddingVertical: 10,
    alignItems: 'center',
  },
  ctaSecondaryText: {
    color: '#e2e8f0',
    fontWeight: '600',
  },
  error: {
    color: '#fda4af',
  },
});
