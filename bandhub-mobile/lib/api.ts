import {
  Concert,
  ConcertDetail,
  MyOrderResponse,
  MyTicketOrderResponse,
  News,
  PageResponse,
  Product,
  Setlist,
  SetlistItem,
  SiteSettings,
  TicketPurchaseResponse,
} from '@/types/api';
import { apiRequest, apiRequestRaw } from '@/lib/http';

export async function fetchSiteSettings(): Promise<SiteSettings> {
  return apiRequest<SiteSettings>('/api/public/site-settings');
}

export async function fetchUiDictionary(): Promise<Record<string, string>> {
  return apiRequest<Record<string, string>>('/api/public/ui-dictionary');
}

export async function fetchNewsPage(): Promise<PageResponse<News>> {
  return apiRequest<PageResponse<News>>('/api/public/news/page?page=0&size=20&sortBy=publishedDate&sortDir=desc');
}

export async function fetchNews(id: string): Promise<News> {
  return apiRequest<News>(`/api/public/news/${id}`);
}

export async function fetchConcertsPage(): Promise<PageResponse<Concert>> {
  return apiRequest<PageResponse<Concert>>('/api/public/concerts/page?page=0&size=30&sortBy=date&sortDir=desc');
}

export async function fetchConcert(id: string): Promise<ConcertDetail> {
  return apiRequest<ConcertDetail>(`/api/public/concerts/${id}`);
}

export async function fetchConcertSetlists(concertId: string): Promise<Setlist[]> {
  return apiRequest<Setlist[]>(`/api/public/concerts/${concertId}/setlists`);
}

export async function fetchMyOrders(token: string): Promise<MyOrderResponse[]> {
  return apiRequest<MyOrderResponse[]>('/api/public/orders/me', { token });
}

export async function fetchMyTicketOrders(token: string): Promise<MyTicketOrderResponse[]> {
  return apiRequest<MyTicketOrderResponse[]>('/api/public/ticket-orders/me', { token });
}

export async function purchaseTickets(
  concertId: string,
  items: Record<string, number>,
  token?: string | null,
): Promise<TicketPurchaseResponse> {
  return apiRequest<TicketPurchaseResponse>('/api/public/ticket-orders', {
    method: 'POST',
    token,
    body: { concertId, items },
  });
}

export async function fetchProductsPage(): Promise<PageResponse<Product>> {
  return apiRequest<PageResponse<Product>>('/api/public/products/page?page=0&size=30&sortBy=name&sortDir=asc');
}

export async function fetchProduct(id: string): Promise<Product> {
  return apiRequest<Product>(`/api/public/products/${id}`);
}

export async function placeOrder(
  items: Record<string, number>,
  deliveryAddress?: string,
  paymentProvider?: string,
  token?: string | null,
): Promise<{ location: string | null }> {
  const response = await apiRequestRaw('/api/public/orders', {
    method: 'POST',
    token,
    body: { items, deliveryAddress, paymentProvider },
  });

  return { location: response.headers.get('Location') ?? response.headers.get('location') };
}

export async function fetchSetlistsPage(): Promise<PageResponse<Setlist>> {
  return apiRequest<PageResponse<Setlist>>('/api/public/setlists/page?page=0&size=30&sortBy=publishedAt&sortDir=desc');
}

export async function fetchSetlistItems(setlistId: string): Promise<SetlistItem[]> {
  return apiRequest<SetlistItem[]>(`/api/public/setlists/${setlistId}/items`);
}
