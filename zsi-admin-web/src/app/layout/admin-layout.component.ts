import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="drawer lg:drawer-open">
      <input id="my-drawer-2" type="checkbox" class="drawer-toggle" />

      <div class="drawer-content flex flex-col bg-base-200 min-h-screen">

        <div class="navbar bg-base-100 shadow-sm w-full">
          <div class="flex-none lg:hidden">
            <label for="my-drawer-2" aria-label="open sidebar" class="btn btn-square btn-ghost">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="inline-block w-6 h-6 stroke-current"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path></svg>
            </label>
          </div>
          <div class="flex-1 px-4 text-xl font-bold text-primary">BandHub Panel</div>
          <div class="flex-none">
            <button class="btn btn-sm btn-ghost text-error" (click)="logout()">Wyloguj</button>
          </div>
        </div>

        <main class="p-6">
          <router-outlet></router-outlet>
        </main>

      </div>

      <div class="drawer-side z-20">
        <label for="my-drawer-2" aria-label="close sidebar" class="drawer-overlay"></label>
        <ul class="menu p-4 w-64 min-h-full bg-base-100 text-base-content border-r border-base-300">
          <li class="mb-4">
            <div class="text-2xl font-black px-4 py-2 flex items-center gap-2">
              <span class="text-primary">Band</span>Hub
            </div>
          </li>

          <li>
            <a routerLink="/admin/dashboard" routerLinkActive="active" class="font-medium">
              Dashboard
            </a>
          </li>

          <li class="menu-title mt-4">E-commerce</li>
          <li>
            <a routerLink="/admin/products" routerLinkActive="active">
              Produkty (Merch)
            </a>
          </li>
          <li>
            <a routerLink="/admin/categories" routerLinkActive="active">Kategorie</a>
          </li>
          <li>
            <a routerLink="/admin/orders" routerLinkActive="active">Zamówienia</a>
          </li>
          <li><a routerLink="/admin/venues" routerLinkActive="active">Miejsca</a></li>
          <li><a routerLink="/admin/concerts" routerLinkActive="active">Koncerty</a></li>
        </ul>

      </div>
    </div>
  `
})
export class AdminLayoutComponent {
  private oidcSecurityService = inject(OidcSecurityService);

  logout() {
    this.oidcSecurityService.logoff().subscribe();
  }
}
