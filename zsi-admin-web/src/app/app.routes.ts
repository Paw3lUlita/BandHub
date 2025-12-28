import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { AdminLayoutComponent } from './layout/admin-layout.component';

export const routes: Routes = [
  // 1. Domyślna ścieżka: Przekieruj do /admin
  {
    path: '',
    redirectTo: 'admin',
    pathMatch: 'full'
  },

  // 2. Ścieżka chroniona /admin
  {
    path: 'admin',
    component: AdminLayoutComponent, // Załaduj Layout (Sidebar + Topbar)
    canActivate: [authGuard],        // SPRAWDŹ CZY ZALOGOWANY
    children: [
      // Tutaj będą podstrony wczytywane wewnątrz <router-outlet> layoutu
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'products',
        loadComponent: () => import('./features/products/product-list.component').then(m => m.ProductListComponent)
      },
      {
        path: 'products/new',
        loadComponent: () => import('./features/products/product-add.component').then(m => m.ProductAddComponent)
      },
      {
        path: 'products/:id',
        loadComponent: () => import('./features/products/product-add.component').then(m => m.ProductAddComponent)
      },
      // Domyślna podstrona dla /admin
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // 3. Fallback (jak ktoś wpisze głupoty)
  {
    path: '**',
    redirectTo: 'admin'
  }
];
