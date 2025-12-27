import { ApplicationConfig } from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { provideAuth } from 'angular-auth-oidc-client';
import { authConfig } from './core/auth/auth.config';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(), // Niezbędne do komunikacji z Backendem i Keycloakiem
    provideAuth(authConfig) // Rejestracja Auth
  ]
};
