import { useLocalSearchParams, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { fetchProduct } from '@/lib/api';
import { Product } from '@/types/api';
import { Screen } from '@/components/ui/Screen';
import { useCart } from '@/providers/CartProvider';
import { useText } from '@/providers/DictionaryProvider';
import { AppText } from '@/components/ui/AppText';
import { Card } from '@/components/ui/Card';
import { LoadingView } from '@/components/ui/LoadingView';
import { PriceTag } from '@/components/ui/PriceTag';
import { PrimaryButton } from '@/components/ui/PrimaryButton';
import { spacing } from '@/constants/theme';

export default function ProductDetailScreen() {
  const router = useRouter();
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
      <Screen scroll={false}>
        <LoadingView />
      </Screen>
    );
  }

  if (!product || error) {
    return (
      <Screen scroll={false}>
        <AppText variant="error">{error ?? 'Produkt nie istnieje'}</AppText>
      </Screen>
    );
  }

  return (
    <Screen>
      <AppText variant="h1">{product.name}</AppText>
      <PriceTag amount={product.price} currency={product.currency} large />
      <AppText variant="caption" muted>Kategoria: {product.categoryName ?? 'brak'}</AppText>
      <AppText variant="caption" muted>
        {t('merch.label.stock', 'Stan')}: {product.stockQuantity}
      </AppText>
      <AppText variant="body">{product.description ?? 'Brak opisu produktu.'}</AppText>

      {!added ? (
        <PrimaryButton label="Dodaj do koszyka" onPress={handleAdd} />
      ) : (
        <Card accent style={{ gap: spacing.sm }}>
          <AppText variant="success">{t('product.feedback.added', 'Dodano do koszyka')}</AppText>
          <PrimaryButton
            label={t('product.cta.viewCart', 'Przejdz do koszyka')}
            onPress={() => router.push('/cart')}
          />
          <PrimaryButton
            label={t('product.cta.continue', 'Kontynuuj zakupy')}
            onPress={() => setAdded(false)}
            variant="ghost"
          />
        </Card>
      )}
    </Screen>
  );
}
