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
      {
        path: 'categories',
        loadComponent: () => import('./features/categories/category-list.component').then(m => m.CategoryListComponent)
      },
      {
        path: 'categories/new',
        loadComponent: () => import('./features/categories/category-add.component').then(m => m.CategoryAddComponent)
      },
      {
        path: 'categories/:id',
        loadComponent: () => import('./features/categories/category-add.component').then(m => m.CategoryAddComponent)
      },
      {
        path: 'orders',
        loadComponent: () => import('./features/orders/order-list.component')
          .then(m => m.OrderListComponent)
      },
      {
        path: 'orders/:id', // Parametr :id
        loadComponent: () => import('./features/orders/order-detail.component').then(m => m.OrderDetailComponent)
      },
      {
        path: 'venues',
        loadComponent: () => import('./features/venues/venue-list.component').then(m => m.VenueListComponent)
      },
      {
        path: 'venues/new',
        loadComponent: () => import('./features/venues/venue-add.component').then(m => m.VenueAddComponent)
      },

      // 2. Koncerty (Concerts) - to zrobimy za chwilę w Task 5.3, ale możesz już dodać
      {
        path: 'concerts',
        loadComponent: () => import('./features/concerts/concert-list.component').then(m => m.ConcertListComponent)
      },
      {
        path: 'concerts/new',
        loadComponent: () => import('./features/concerts/concert-add.component').then(m => m.ConcertAddComponent)
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
