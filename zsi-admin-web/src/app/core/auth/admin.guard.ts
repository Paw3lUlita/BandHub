import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { map, of, switchMap, take } from 'rxjs';

function hasAdminRole(payload: unknown): boolean {
  if (!payload || typeof payload !== 'object') return false;
  const realmAccess = (payload as Record<string, unknown>)['realm_access'];
  if (!realmAccess || typeof realmAccess !== 'object') return false;
  const roles = (realmAccess as Record<string, unknown>)['roles'];
  if (!Array.isArray(roles)) return false;
  return roles.some((r: unknown) =>
    String(r).toUpperCase() === 'ADMIN' || String(r) === 'ROLE_ADMIN'
  );
}

export const adminGuard: CanActivateFn = (route, state) => {
  const oidcSecurityService = inject(OidcSecurityService);
  const router = inject(Router);

  return oidcSecurityService.checkAuth().pipe(
    take(1),
    switchMap(({ isAuthenticated }) => {
      if (!isAuthenticated) {
        oidcSecurityService.authorize();
        return of(false);
      }
      return oidcSecurityService.getPayloadFromAccessToken().pipe(
        take(1),
        map((payload) => {
          if (hasAdminRole(payload)) return true;
          router.navigate(['/admin/dashboard']);
          return false;
        })
      );
    })
  );
};
