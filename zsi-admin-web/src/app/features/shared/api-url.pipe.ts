import { Pipe, PipeTransform } from '@angular/core';

const BACKEND_BASE_URL = 'http://localhost:8080';

/**
 * Skleja wzgledne sciezki API (np. zwrocone przez backend pola imageUrl) z hostem backendu.
 * Zapobiega 404 typu http://localhost:4200/api/public/uploads/... gdy admin web hostuje
 * sie pod 4200, a uploads sa pod 8080.
 */
@Pipe({
  name: 'apiUrl',
  standalone: true
})
export class ApiUrlPipe implements PipeTransform {
  transform(value: string | null | undefined): string | null {
    if (!value) {
      return null;
    }
    if (/^https?:\/\//i.test(value)) {
      return value;
    }
    return `${BACKEND_BASE_URL}${value.startsWith('/') ? '' : '/'}${value}`;
  }
}
