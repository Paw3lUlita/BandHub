import { useLocalSearchParams } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text } from 'react-native';
import { fetchProduct } from '@/lib/api';
import { Product } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';

export default function ProductDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { addItem } = useCart();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [product, setProduct] = useState<Product | null>(null);
  const [result, setResult] = useState<string | null>(null);

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
      },
      1,
    );
    setResult('Dodano do koszyka');
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
      <Text style={styles.meta}>Stan: {product.stockQuantity}</Text>
      <Text style={styles.desc}>{product.description ?? 'Brak opisu produktu.'}</Text>

      <Pressable onPress={handleAdd} style={styles.button}>
        <Text style={styles.buttonText}>Dodaj do koszyka</Text>
      </Pressable>
      {result ? <Text style={styles.result}>{result}</Text> : null}
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
  result: {
    color: '#93c5fd',
  },
  error: {
    color: '#fda4af',
  },
});
