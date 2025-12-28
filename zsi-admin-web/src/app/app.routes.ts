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
