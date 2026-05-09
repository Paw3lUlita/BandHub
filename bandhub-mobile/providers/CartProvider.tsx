import React, { createContext, useContext, useMemo, useState } from 'react';
import { CartItem } from '@/types/api';

type CartContextValue = {
  items: CartItem[];
  addItem: (item: Omit<CartItem, 'quantity'>, quantity?: number) => void;
  removeItem: (productId: string) => void;
  clear: () => void;
  totalAmount: number;
};

const CartContext = createContext<CartContextValue | undefined>(undefined);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([]);

  const addItem = (item: Omit<CartItem, 'quantity'>, quantity = 1) => {
    setItems((current) => {
      const existing = current.find((entry) => entry.productId === item.productId);
      if (!existing) {
        return [...current, { ...item, quantity }];
      }

      return current.map((entry) =>
        entry.productId === item.productId
          ? { ...entry, quantity: entry.quantity + quantity }
          : entry,
      );
    });
  };

  const removeItem = (productId: string) => {
    setItems((current) => current.filter((entry) => entry.productId !== productId));
  };

  const clear = () => {
    setItems([]);
  };

  const totalAmount = useMemo(
    () => items.reduce((sum, entry) => sum + entry.price * entry.quantity, 0),
    [items],
  );

  const value = useMemo<CartContextValue>(
    () => ({
      items,
      addItem,
      removeItem,
      clear,
      totalAmount,
    }),
    [items, totalAmount],
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) {
    throw new Error('useCart must be used inside CartProvider');
  }
  return ctx;
}
