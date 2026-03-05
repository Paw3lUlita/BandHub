import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { adminGuard } from './core/auth/admin.guard';
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
      {
        path: 'concerts',
        loadComponent: () => import('./features/concerts/concert-list.component').then(m => m.ConcertListComponent)
      },
      {
        path: 'concerts/new',
        loadComponent: () => import('./features/concerts/concert-add.component').then(m => m.ConcertAddComponent)
      },
      {
        path: 'concerts/:id',
        loadComponent: () => import('./features/concerts/concert-detail.component').then(m => m.ConcertDetailComponent)
      },
      {
        path: 'gallery',
        loadComponent: () => import('./features/cms/gallery/gallery-list.component').then(m => m.GalleryListComponent)
      },
      {
        path: 'news',
        loadComponent: () => import('./features/cms/news/news-list.component').then(m => m.NewsListComponent)
      },
      {
        path: 'news/new',
        loadComponent: () => import('./features/cms/news/news-add.component').then(m => m.NewsAddComponent)
      },
      {
        path: 'news/:id',
        loadComponent: () => import('./features/cms/news/news-add.component').then(m => m.NewsAddComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./features/users/user-list.component').then(m => m.UserListComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'users/new',
        loadComponent: () => import('./features/users/user-add.component').then(m => m.UserAddComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'users/:id',
        loadComponent: () => import('./features/users/user-detail.component').then(m => m.UserDetailComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'roles',
        loadComponent: () => import('./features/users/role-list.component').then(m => m.RoleListComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'roles/new',
        loadComponent: () => import('./features/users/role-add.component').then(m => m.RoleAddComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'groups',
        loadComponent: () => import('./features/users/group-list.component').then(m => m.GroupListComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'groups/new',
        loadComponent: () => import('./features/users/group-add.component').then(m => m.GroupAddComponent),
        canActivate: [adminGuard]
      },
      {
        path: 'logistics',
        loadComponent: () => import('./features/logistics/tour-list.component').then(m => m.TourListComponent)
      },
      {
        path: 'logistics/new',
        loadComponent: () => import('./features/logistics/tour-add.component').then(m => m.TourAddComponent)
      },
      {
        path: 'logistics/:id',
        loadComponent: () => import('./features/logistics/tour-detail.component').then(m => m.TourDetailComponent)
      },
      { path: 'payments', loadComponent: () => import('./features/ecommerce/payments/payment-list.component').then(m => m.PaymentListComponent) },
      { path: 'payments/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'payments/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'payment-transactions', loadComponent: () => import('./features/ecommerce/payment-transactions/payment-transaction-list.component').then(m => m.PaymentTransactionListComponent) },
      { path: 'payment-transactions/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'payment-transactions/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'shipments', loadComponent: () => import('./features/ecommerce/shipments/shipment-list.component').then(m => m.ShipmentListComponent) },
      { path: 'shipments/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'shipments/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'order-status-history', loadComponent: () => import('./features/ecommerce/order-status-history/order-status-history-list.component').then(m => m.OrderStatusHistoryListComponent) },
      { path: 'order-status-history/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'order-status-history/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-orders', loadComponent: () => import('./features/ticketing/ticket-orders/ticket-order-list.component').then(m => m.TicketOrderListComponent) },
      { path: 'ticket-orders/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-orders/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-order-items', loadComponent: () => import('./features/ticketing/ticket-order-items/ticket-order-item-list.component').then(m => m.TicketOrderItemListComponent) },
      { path: 'ticket-order-items/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-order-items/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-codes', loadComponent: () => import('./features/ticketing/ticket-codes/ticket-code-list.component').then(m => m.TicketCodeListComponent) },
      { path: 'ticket-codes/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-codes/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-validations', loadComponent: () => import('./features/ticketing/ticket-validations/ticket-validation-list.component').then(m => m.TicketValidationListComponent) },
      { path: 'ticket-validations/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-validations/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-refunds', loadComponent: () => import('./features/ticketing/ticket-refunds/ticket-refund-list.component').then(m => m.TicketRefundListComponent) },
      { path: 'ticket-refunds/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'ticket-refunds/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'setlists', loadComponent: () => import('./features/fan/setlists/setlist-list.component').then(m => m.SetlistListComponent) },
      { path: 'setlists/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'setlists/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'setlist-items', loadComponent: () => import('./features/fan/setlist-items/setlist-item-list.component').then(m => m.SetlistItemListComponent) },
      { path: 'setlist-items/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'setlist-items/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-favorites', loadComponent: () => import('./features/fan/fan-favorites/fan-favorite-list.component').then(m => m.FanFavoriteListComponent) },
      { path: 'fan-favorites/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-favorites/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-notifications', loadComponent: () => import('./features/fan/fan-notifications/fan-notification-list.component').then(m => m.FanNotificationListComponent) },
      { path: 'fan-notifications/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-notifications/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-notification-reads', loadComponent: () => import('./features/fan/fan-notification-reads/fan-notification-read-list.component').then(m => m.FanNotificationReadListComponent) },
      { path: 'fan-notification-reads/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-notification-reads/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-devices', loadComponent: () => import('./features/fan/fan-devices/fan-device-list.component').then(m => m.FanDeviceListComponent) },
      { path: 'fan-devices/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'fan-devices/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'tour-legs', loadComponent: () => import('./features/logistics/tour-legs/tour-leg-list.component').then(m => m.TourLegListComponent) },
      { path: 'tour-legs/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'tour-legs/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'tour-cost-categories', loadComponent: () => import('./features/logistics/tour-cost-categories/tour-cost-category-list.component').then(m => m.TourCostCategoryListComponent) },
      { path: 'tour-cost-categories/new', loadComponent: () => import('./features/logistics/tour-cost-categories/tour-cost-category-add.component').then(m => m.TourCostCategoryAddComponent) },
      { path: 'tour-cost-categories/:id', loadComponent: () => import('./features/logistics/tour-cost-categories/tour-cost-category-add.component').then(m => m.TourCostCategoryAddComponent) },
      { path: 'tour-revenue-categories', loadComponent: () => import('./features/logistics/tour-revenue-categories/tour-revenue-category-list.component').then(m => m.TourRevenueCategoryListComponent) },
      { path: 'tour-revenue-categories/new', loadComponent: () => import('./features/logistics/tour-revenue-categories/tour-revenue-category-add.component').then(m => m.TourRevenueCategoryAddComponent) },
      { path: 'tour-revenue-categories/:id', loadComponent: () => import('./features/logistics/tour-revenue-categories/tour-revenue-category-add.component').then(m => m.TourRevenueCategoryAddComponent) },
      { path: 'tour-settlements', loadComponent: () => import('./features/logistics/tour-settlements/tour-settlement-list.component').then(m => m.TourSettlementListComponent) },
      { path: 'tour-settlements/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'tour-settlements/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'reports/merch', loadComponent: () => import('./features/reporting/merch-report.component').then(m => m.MerchReportComponent) },
      { path: 'report-runs', loadComponent: () => import('./features/reporting/report-runs/report-run-list.component').then(m => m.ReportRunListComponent) },
      { path: 'report-runs/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'report-runs/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'export-jobs', loadComponent: () => import('./features/reporting/export-jobs/export-job-list.component').then(m => m.ExportJobListComponent) },
      { path: 'export-jobs/new', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
      { path: 'export-jobs/:id', loadComponent: () => import('./features/shared/form-placeholder.component').then(m => m.FormPlaceholderComponent) },
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
