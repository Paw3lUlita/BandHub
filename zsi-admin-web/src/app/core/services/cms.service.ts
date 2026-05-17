import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Modele (Interface)
export interface GalleryImage {
  id: string;
  title: string;
  imageUrl: string; // Pełny URL: /api/public/uploads/...
  uploadedAt: string;
}

export interface NewsArticle {
  id: string;
  title: string;
  content: string;
  imageUrl?: string;
  publishedDate: string;
  authorId: string;
  authorUsername?: string;
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

export interface CreateNewsRequest {
  title: string;
  content: string;
  imageUrl: string;
}

export interface SiteSettings {
  bandName: string;
  tagline?: string | null;
  heroImageUrl?: string | null;
  aboutText?: string | null;
  updatedAt?: string;
  updatedBy?: string | null;
}

export interface UpdateSiteSettingsRequest {
  bandName: string;
  tagline?: string | null;
  heroImageUrl?: string | null;
  aboutText?: string | null;
}

export interface UiDictionaryEntry {
  key: string;
  value: string;
  description?: string | null;
  updatedAt?: string;
  updatedBy?: string | null;
}

export interface CreateUiDictionaryEntryRequest {
  key: string;
  value: string;
  description?: string | null;
}

export interface UpdateUiDictionaryEntryRequest {
  value: string;
  description?: string | null;
}

@Injectable({ providedIn: 'root' })
export class CmsService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/admin';

  // --- GALERIA ---
  getImages(): Observable<GalleryImage[]> {
    return this.http.get<GalleryImage[]>(`${this.apiUrl}/gallery`);
  }

  uploadImage(title: string, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('file', file);
    return this.http.post<void>(`${this.apiUrl}/gallery`, formData);
  }

  deleteImage(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/gallery/${id}`);
  }

  // --- NEWSY ---
  getAllNews(): Observable<NewsArticle[]> {
    return this.http.get<NewsArticle[]>(`${this.apiUrl}/news`);
  }

  getNewsPage(params: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
    q?: string;
  }): Observable<PageResponse<NewsArticle>> {
    const queryParams = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 10),
      sortBy: params.sortBy ?? 'publishedDate',
      sortDir: params.sortDir ?? 'desc',
      q: params.q ?? ''
    });

    return this.http.get<PageResponse<NewsArticle>>(`${this.apiUrl}/news/page?${queryParams.toString()}`);
  }

  getNews(id: string): Observable<NewsArticle> {
    return this.http.get<NewsArticle>(`${this.apiUrl}/news/${id}`);
  }

  createNews(news: CreateNewsRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/news`, news);
  }

  updateNews(id: string, news: CreateNewsRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/news/${id}`, news);
  }

  deleteNews(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/news/${id}`);
  }

  // --- USTAWIENIA STRONY (BRAND/HERO/ABOUT) ---
  getSiteSettings(): Observable<SiteSettings> {
    return this.http.get<SiteSettings>(`${this.apiUrl}/site-settings`);
  }

  updateSiteSettings(request: UpdateSiteSettingsRequest): Observable<SiteSettings> {
    return this.http.put<SiteSettings>(`${this.apiUrl}/site-settings`, request);
  }

  // --- SLOWNIK UI (mikro-copywriting) ---
  getUiDictionary(): Observable<UiDictionaryEntry[]> {
    return this.http.get<UiDictionaryEntry[]>(`${this.apiUrl}/ui-dictionary`);
  }

  createUiDictionaryEntry(request: CreateUiDictionaryEntryRequest): Observable<UiDictionaryEntry> {
    return this.http.post<UiDictionaryEntry>(`${this.apiUrl}/ui-dictionary`, request);
  }

  updateUiDictionaryEntry(key: string, request: UpdateUiDictionaryEntryRequest): Observable<UiDictionaryEntry> {
    return this.http.put<UiDictionaryEntry>(`${this.apiUrl}/ui-dictionary/${encodeURIComponent(key)}`, request);
  }

  deleteUiDictionaryEntry(key: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/ui-dictionary/${encodeURIComponent(key)}`);
  }
}
