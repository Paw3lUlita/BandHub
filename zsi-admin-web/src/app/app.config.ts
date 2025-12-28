import { ApplicationConfig } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { routes } from './app.routes';
import {HttpInterceptorFn, provideHttpClient, withInterceptors} from '@angular/common/http';
import { provideAuth, authInterceptor } from 'angular-auth-oidc-client';
import { authConfig } from './core/auth/auth.config';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),

    // WAŻNE: Tu wpinamy automatyczne dołączanie tokena do zapytań
    provideHttpClient(withInterceptors([authInterceptor() as HttpInterceptorFn])),

    provideAuth(authConfig)
  ]
};
