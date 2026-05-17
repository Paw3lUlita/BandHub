import React, { createContext, useContext, useMemo, useState } from 'react';
import { CartItem } from '@/types/api';

type CartContextValue = {
  items: CartItem[];
  addItem: (item: Omit<CartItem, 'quantity'>, quantity?: number) => void;
  increment: (productId: string) => void;
  decrement: (productId: string) => void;
  setQuantity: (productId: string, quantity: number) => void;
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
        const capped = item.stockQuantity != null ? Math.min(quantity, item.stockQuantity) : quantity;
        return [...current, { ...item, quantity: Math.max(1, capped) }];
      }

      return current.map((entry) => {
        if (entry.productId !== item.productId) {
          return entry;
        }
        const nextQty = entry.quantity + quantity;
        const max = entry.stockQuantity ?? nextQty;
        return { ...entry, quantity: Math.min(nextQty, max) };
      });
    });
  };

  const setQuantity = (productId: string, quantity: number) => {
    if (quantity <= 0) {
      setItems((current) => current.filter((entry) => entry.productId !== productId));
      return;
    }
    setItems((current) =>
      current.map((entry) => {
        if (entry.productId !== productId) {
          return entry;
        }
        const max = entry.stockQuantity ?? quantity;
        return { ...entry, quantity: Math.min(quantity, max) };
      }),
    );
  };

  const increment = (productId: string) => {
    setItems((current) =>
      current.map((entry) => {
        if (entry.productId !== productId) {
          return entry;
        }
        const max = entry.stockQuantity ?? entry.quantity + 1;
        return { ...entry, quantity: Math.min(entry.quantity + 1, max) };
      }),
    );
  };

  const decrement = (productId: string) => {
    setItems((current) =>
      current
        .map((entry) => {
          if (entry.productId !== productId) {
            return entry;
          }
          return { ...entry, quantity: entry.quantity - 1 };
        })
        .filter((entry) => entry.quantity > 0),
    );
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
      increment,
      decrement,
      setQuantity,
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
