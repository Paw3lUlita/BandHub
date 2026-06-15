import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface ModuleTile {
  title: string;
  description: string;
  link: string;
  section: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="bh-page max-w-6xl">
      <section class="bh-hero mb-8">
        <h1 class="bh-hero-title">Witaj w BandHub</h1>
        <p class="bh-hero-subtitle">
          Panel operacyjny zespołu — merch, bilety, trasy, treści i raporty w jednym miejscu.
        </p>
      </section>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        @for (tile of tiles; track tile.link) {
          <a [routerLink]="tile.link" class="bh-module-tile no-underline">
            <span class="text-xs font-semibold uppercase tracking-wider text-primary/70">{{ tile.section }}</span>
            <h3 class="bh-module-tile-title mt-1">{{ tile.title }}</h3>
            <p class="bh-module-tile-desc">{{ tile.description }}</p>
          </a>
        }
      </div>
    </div>
  `,
})
export class DashboardComponent {
  tiles: ModuleTile[] = [
    { section: 'E-commerce', title: 'Produkty', description: 'Katalog merchu, ceny i stany magazynowe.', link: '/admin/products' },
    { section: 'E-commerce', title: 'Zamówienia', description: 'Obsługa zamówień fanów — statusy, płatności, wysyłki.', link: '/admin/orders' },
    { section: 'Ticketing', title: 'Koncerty', description: 'Planowanie wydarzeń, pule biletów i ceny.', link: '/admin/concerts' },
    { section: 'Ticketing', title: 'Skan biletu', description: 'Kontrola wejścia na koncert przez kod QR.', link: '/admin/ticketing/scan' },
    { section: 'CMS', title: 'Aktualności', description: 'Treści widoczne fanom na ekranie głównym mobilki.', link: '/admin/news' },
    { section: 'CMS', title: 'Ustawienia strony', description: 'Branding zespołu — nazwa, logo, kolory, hero.', link: '/admin/site-settings' },
    { section: 'Fan', title: 'Setlisty', description: 'Setlisty koncertów publikowane w aplikacji mobilnej.', link: '/admin/setlists' },
    { section: 'Logistyka', title: 'Trasy koncertowe', description: 'Planowanie trasy, koszty, przychody i rozliczenia.', link: '/admin/logistics' },
    { section: 'Raporty', title: 'Generator raportów', description: 'Eksporty PDF, Excel i dokumenty z szablonów.', link: '/admin/reports/generator' },
  ];
}
