import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// DTO - definicje typów zgodne z Backendem
export enum OrderStatus {
  NEW = 'NEW',
  PAID = 'PAID',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface OrderSummary {
  id: string;
  createdAt: string; // Backend zwraca datę jako string ISO
  status: OrderStatus;
  totalAmount: number;
  currency: string;
  userId: string;
}

export interface UpdateStatusCommand {
  newStatus: OrderStatus;
}

export interface OrderItem {
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface PaymentSummary {
  id: string;
  orderId: string;
  provider: string | null;
  providerPaymentId: string | null;
  status: string;
  amount: number;
  currency: string;
  paidAt: string | null;
  createdAt: string;
}

export interface ShipmentSummary {
  id: string;
  orderId: string;
  carrier: string | null;
  trackingNumber: string | null;
  status: string;
  shippedAt: string | null;
  deliveredAt: string | null;
  createdAt: string;
  deliveryAddress: string | null;
}

export interface OrderStatusHistoryItem {
  id: string;
  orderId: string;
  oldStatus: string | null;
  newStatus: string;
  changedBy: string | null;
  changedAt: string;
  note: string | null;
}

export interface OrderDetails {
  id: string;
  createdAt: string;
  status: OrderStatus;
  totalAmount: number;
  currency: string;
  userId: string;
  items: OrderItem[];
  payment: PaymentSummary | null;
  shipment: ShipmentSummary | null;
  statusHistory: OrderStatusHistoryItem[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  sortBy: string;
  sortDir: string;
  query: string;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/admin/orders';

  // A. Pobranie listy zamówień
  getOrders(): Observable<OrderSummary[]> {
    return this.http.get<OrderSummary[]>(this.apiUrl);
  }

  getOrdersPage(params: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
    q?: string;
    status?: OrderStatus;
  }): Observable<PageResponse<OrderSummary>> {
    const queryParams = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'createdAt',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });

    if (params.status) {
      queryParams.set('status', params.status);
    }

    return this.http.get<PageResponse<OrderSummary>>(`${this.apiUrl}/page?${queryParams.toString()}`);
  }

  // B. Zmiana statusu (PATCH)
  updateStatus(id: string, newStatus: OrderStatus): Observable<void> {
    const command: UpdateStatusCommand = { newStatus };
    return this.http.patch<void>(`${this.apiUrl}/${id}/status`, command);
  }

  // C. Pobranie szczegółów
  getOrder(id: string): Observable<OrderDetails> {
    return this.http.get<OrderDetails>(`${this.apiUrl}/${id}`);
  }
}
