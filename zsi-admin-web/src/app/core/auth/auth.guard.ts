import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { map, take } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const oidcSecurityService = inject(OidcSecurityService);
  const router = inject(Router);

  return oidcSecurityService.checkAuth().pipe(
    take(1),
    map(({ isAuthenticated }) => {
      // Jeśli zalogowany -> Wpuszczamy (return true)
      if (isAuthenticated) {
        return true;
      }

      // Jeśli niezalogowany -> Przekieruj do Keycloak
      oidcSecurityService.authorize();
      return false;
    })
  );
};
