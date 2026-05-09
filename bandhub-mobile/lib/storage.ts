import AsyncStorage from '@react-native-async-storage/async-storage';
import { LocalMerchOrder, LocalTicketPurchase } from '@/types/api';

const keys = {
  accessToken: 'bandhub.mobile.accessToken',
  ticketPurchases: 'bandhub.mobile.ticketPurchases',
  merchOrders: 'bandhub.mobile.merchOrders',
};

export async function setAccessToken(token: string): Promise<void> {
  await AsyncStorage.setItem(keys.accessToken, token);
}

export async function getAccessToken(): Promise<string | null> {
  return AsyncStorage.getItem(keys.accessToken);
}

export async function clearAccessToken(): Promise<void> {
  await AsyncStorage.removeItem(keys.accessToken);
}

export async function getTicketPurchases(): Promise<LocalTicketPurchase[]> {
  const raw = await AsyncStorage.getItem(keys.ticketPurchases);
  if (!raw) {
    return [];
  }

  try {
    return JSON.parse(raw) as LocalTicketPurchase[];
  } catch {
    return [];
  }
}

export async function saveTicketPurchase(purchase: LocalTicketPurchase): Promise<void> {
  const current = await getTicketPurchases();
  const next = [purchase, ...current];
  await AsyncStorage.setItem(keys.ticketPurchases, JSON.stringify(next));
}

export async function getMerchOrders(): Promise<LocalMerchOrder[]> {
  const raw = await AsyncStorage.getItem(keys.merchOrders);
  if (!raw) {
    return [];
  }

  try {
    return JSON.parse(raw) as LocalMerchOrder[];
  } catch {
    return [];
  }
}

export async function saveMerchOrder(order: LocalMerchOrder): Promise<void> {
  const current = await getMerchOrders();
  const next = [order, ...current];
  await AsyncStorage.setItem(keys.merchOrders, JSON.stringify(next));
}
