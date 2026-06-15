import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="drawer lg:drawer-open">
      <input id="my-drawer-2" type="checkbox" class="drawer-toggle" />

      <div class="drawer-content flex flex-col min-h-screen bg-bh-surface">
        <header class="navbar sticky top-0 z-30 bg-base-100/90 backdrop-blur-md border-b border-base-300/60 shadow-sm px-2">
          <div class="flex-none lg:hidden">
            <label for="my-drawer-2" aria-label="open sidebar" class="btn btn-square btn-ghost">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="w-6 h-6 stroke-current">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
              </svg>
            </label>
          </div>
          <div class="flex-1 px-2">
            <span class="text-lg font-bold bg-clip-text text-transparent bg-bh-gradient">BandHub Panel</span>
          </div>
          <div class="flex-none">
            <button class="btn btn-sm btn-ghost text-error hover:bg-error/10" (click)="logout()">Wyloguj</button>
          </div>
        </header>

        <main class="flex-1 p-4 md:p-6 lg:p-8">
          <router-outlet></router-outlet>
        </main>
      </div>

      <div class="drawer-side z-40">
        <label for="my-drawer-2" aria-label="close sidebar" class="drawer-overlay"></label>
        <aside class="min-h-full w-72 bg-neutral text-neutral-content flex flex-col border-r border-white/5">
          <div class="p-5 border-b border-white/10">
            <div class="text-2xl font-black flex items-center gap-2">
              <span class="w-9 h-9 rounded-xl bg-bh-gradient flex items-center justify-center text-sm shadow-glow">BH</span>
              <span><span class="text-accent">Band</span>Hub</span>
            </div>
            <p class="text-xs text-neutral-content/50 mt-2">Panel zarządzania zespołem</p>
          </div>

          <ul class="menu p-3 flex-1 overflow-y-auto text-sm gap-0.5">
            <li>
              <a routerLink="/admin/dashboard" routerLinkActive="active" class="bh-sidebar-link">
                Dashboard
              </a>
            </li>

            <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">E-commerce</li>
            <li><a routerLink="/admin/products" routerLinkActive="active" class="bh-sidebar-link">Merch (Produkty)</a></li>
            <li><a routerLink="/admin/categories" routerLinkActive="active" class="bh-sidebar-link">Kategorie</a></li>
            <li><a routerLink="/admin/orders" routerLinkActive="active" class="bh-sidebar-link">Zamówienia</a></li>
            <li><a routerLink="/admin/payments" routerLinkActive="active" class="bh-sidebar-link">Płatności</a></li>
            <li><a routerLink="/admin/payment-transactions" routerLinkActive="active" class="bh-sidebar-link">Transakcje płatności</a></li>
            <li><a routerLink="/admin/shipments" routerLinkActive="active" class="bh-sidebar-link">Wysyłki</a></li>
            <li><a routerLink="/admin/order-status-history" routerLinkActive="active" class="bh-sidebar-link">Historia statusów</a></li>

            <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">Ticketing & Trasa</li>
            <li><a routerLink="/admin/concerts" routerLinkActive="active" class="bh-sidebar-link">Koncerty</a></li>
            <li><a routerLink="/admin/venues" routerLinkActive="active" class="bh-sidebar-link">Miejsca</a></li>
            <li><a routerLink="/admin/ticket-orders" routerLinkActive="active" class="bh-sidebar-link">Zamówienia biletów</a></li>
            <li><a routerLink="/admin/ticket-order-items" routerLinkActive="active" class="bh-sidebar-link">Pozycje zamówień</a></li>
            <li><a routerLink="/admin/ticket-codes" routerLinkActive="active" class="bh-sidebar-link">Kody biletów</a></li>
            <li><a routerLink="/admin/ticket-validations" routerLinkActive="active" class="bh-sidebar-link">Walidacje</a></li>
            <li><a routerLink="/admin/ticket-refunds" routerLinkActive="active" class="bh-sidebar-link">Zwroty biletów</a></li>
            <li><a routerLink="/admin/ticketing/scan" routerLinkActive="active" class="bh-sidebar-link">Skan biletu</a></li>

            <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">CMS / Treści</li>
            <li><a routerLink="/admin/site-settings" routerLinkActive="active" class="bh-sidebar-link">Ustawienia strony</a></li>
            <li><a routerLink="/admin/ui-dictionary" routerLinkActive="active" class="bh-sidebar-link">Słownik UI</a></li>
            <li><a routerLink="/admin/news" routerLinkActive="active" class="bh-sidebar-link">Aktualności</a></li>
            <li><a routerLink="/admin/gallery" routerLinkActive="active" class="bh-sidebar-link">Galeria Zdjęć</a></li>

            <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">Fan / Mobile</li>
            <li><a routerLink="/admin/setlists" routerLinkActive="active" class="bh-sidebar-link">Setlisty</a></li>
            <li><a routerLink="/admin/setlist-items" routerLinkActive="active" class="bh-sidebar-link">Pozycje setlist</a></li>

            @if (isAdmin()) {
              <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">IAM</li>
              <li><a routerLink="/admin/users" routerLinkActive="active" class="bh-sidebar-link">Użytkownicy</a></li>
              <li><a routerLink="/admin/roles" routerLinkActive="active" class="bh-sidebar-link">Role</a></li>
              <li><a routerLink="/admin/groups" routerLinkActive="active" class="bh-sidebar-link">Grupy</a></li>
            }

            <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">Logistyka & Finanse</li>
            <li><a routerLink="/admin/logistics" routerLinkActive="active" class="bh-sidebar-link">Trasy Koncertowe</a></li>
            <li><a routerLink="/admin/tour-legs" routerLinkActive="active" class="bh-sidebar-link">Odcinki trasy</a></li>
            <li><a routerLink="/admin/tour-cost-categories" routerLinkActive="active" class="bh-sidebar-link">Kategorie kosztów</a></li>
            <li><a routerLink="/admin/tour-revenue-categories" routerLinkActive="active" class="bh-sidebar-link">Kategorie przychodów</a></li>
            <li><a routerLink="/admin/tour-settlements" routerLinkActive="active" class="bh-sidebar-link">Rozliczenia tras</a></li>

            <li class="menu-title mt-3 text-accent/80 text-xs uppercase tracking-wider">Raportowanie</li>
            <li><a routerLink="/admin/reports/generator" routerLinkActive="active" class="bh-sidebar-link">Generator raportów</a></li>
            <li><a routerLink="/admin/reports/docx-templates" routerLinkActive="active" class="bh-sidebar-link">Szablony DOCX</a></li>
            <li><a routerLink="/admin/reports/merch" routerLinkActive="active" class="bh-sidebar-link">Raport merchu</a></li>
            <li><a routerLink="/admin/reports/ticketing-event" routerLinkActive="active" class="bh-sidebar-link">Raport wydarzenia (bilety)</a></li>
            <li><a routerLink="/admin/report-runs" routerLinkActive="active" class="bh-sidebar-link">Uruchomienia raportów</a></li>
            <li><a routerLink="/admin/export-jobs" routerLinkActive="active" class="bh-sidebar-link">Zadania eksportu</a></li>
          </ul>
        </aside>
      </div>
    </div>
  `,
})
export class AdminLayoutComponent implements OnInit {
  private oidcSecurityService = inject(OidcSecurityService);

  isAdmin = signal(false);

  ngOnInit() {
    this.oidcSecurityService.getPayloadFromAccessToken().subscribe((payload) => {
      const realmAccess = payload?.['realm_access'] as { roles?: unknown[] } | undefined;
      const roles = realmAccess?.roles ?? [];
      this.isAdmin.set(
        roles.some((r: unknown) => String(r).toUpperCase() === 'ADMIN' || String(r) === 'ROLE_ADMIN'),
      );
    });
  }

  logout() {
    this.oidcSecurityService.logoff().subscribe();
  }
}
