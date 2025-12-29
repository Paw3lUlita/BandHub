import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Odpowiedź z GET (Lista)
export interface Product {
  id: string;
  name: string;
  description?: string;
  price: number;
  currency: string;
  stockQuantity: number;
  categoryName: string;
  categoryId: string;
}

// Żądanie POST (Dodawanie)
export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  currency: string;
  stockQuantity: number; // Było: stock
  categoryId: string;    // Było: category (String/Enum) -> Teraz UUID
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  // URL zgodny z kontraktem
  private apiUrl = 'http://localhost:8080/api/admin/products';

  // A. Lista Produktów (GET)
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  // B. Dodawanie Produktu (POST)
  createProduct(product: CreateProductRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, product);
  }

  // C. Usuwanie Produktu (DELETE)
  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // D. Pobranie pojedynczego produktu (do edycji)
  getProduct(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  // E. Aktualizacja produktu (PUT)
  updateProduct(id: string, product: CreateProductRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, product);
  }
}
