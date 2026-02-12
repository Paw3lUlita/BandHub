import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// DTO - definicje typów zgodne z Backendem
export enum OrderStatus {
  NEW = 'NEW',
  SHIPPED = 'SHIPPED',
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

export interface OrderDetails {
  id: string;
  createdAt: string;
  status: OrderStatus;
  totalAmount: number;
  currency: string;
  userId: string;
  items: OrderItem[];
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/admin/orders';

  // A. Pobranie listy zamówień
  getOrders(): Observable<OrderSummary[]> {
    return this.http.get<OrderSummary[]>(this.apiUrl);
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
