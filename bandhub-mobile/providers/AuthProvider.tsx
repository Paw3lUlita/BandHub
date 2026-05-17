import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { setUnauthorizedHandler } from '@/lib/authEvents';
import { clearAccessToken, getAccessToken, setAccessToken } from '@/lib/storage';
import { apiRequest } from '@/lib/http';

type AuthContextValue = {
  token: string | null;
  username: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  sessionExpired: boolean;
  clearSessionExpired: () => void;
  login: (username: string, password: string) => Promise<void>;
  register: (input: RegisterInput) => Promise<void>;
  logout: () => Promise<void>;
};

export type RegisterInput = {
  username: string;
  password: string;
  email?: string;
  firstName?: string;
  lastName?: string;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

type StoredAuth = {
  token: string;
  username: string;
};

const STORED_USERNAME_KEY = 'bandhub.mobile.username';

function decodeUsernameFromJwt(token: string): string | null {
  try {
    const payload = token.split('.')[1];
    if (!payload) {
      return null;
    }

    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const decoded =
      typeof atob === 'function' ? atob(normalized) : Buffer.from(normalized, 'base64').toString('binary');
    const json = JSON.parse(decoded);
    return json.preferred_username ?? json.username ?? json.sub ?? null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [sessionExpired, setSessionExpired] = useState(false);

  const clearSession = useCallback(async () => {
    await clearAccessToken();
    setToken(null);
    setUsername(null);
  }, []);

  useEffect(() => {
    getAccessToken()
      .then((value) => {
        if (!value) {
          return;
        }
        setToken(value);
        setUsername(decodeUsernameFromJwt(value));
      })
      .finally(() => setIsLoading(false));
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(() => {
      clearSession();
      setSessionExpired(true);
    });
    return () => setUnauthorizedHandler(null);
  }, [clearSession]);

  const clearSessionExpired = useCallback(() => {
    setSessionExpired(false);
  }, []);

  const persist = async (next: StoredAuth) => {
    await setAccessToken(next.token);
    setToken(next.token);
    setUsername(next.username);
    setSessionExpired(false);
  };

  const login = async (loginUsername: string, password: string) => {
    // Mobilka rozmawia tylko z naszym backendem, ktory robi password grant po stronie serwera.
    // Dzieki temu omijamy CORS / Web Origins na Keycloaku i mamy jeden entry-point dla appki.
    const response = await apiRequest<{ accessToken: string; refreshToken?: string | null }>(
      '/api/public/auth/login',
      {
        method: 'POST',
        body: { username: loginUsername, password },
      },
    );

    if (!response.accessToken) {
      throw new Error('Brak tokenu w odpowiedzi z backendu');
    }

    await persist({
      token: response.accessToken,
      username: decodeUsernameFromJwt(response.accessToken) ?? loginUsername,
    });
  };

  const register = async (input: RegisterInput) => {
    await apiRequest<{ userId: string; username: string }>('/api/public/register', {
      method: 'POST',
      body: input,
    });
    await login(input.username, input.password);
  };

  const logout = async () => {
    await clearSession();
    setSessionExpired(false);
  };

  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      username,
      isAuthenticated: Boolean(token),
      isLoading,
      sessionExpired,
      clearSessionExpired,
      login,
      register,
      logout,
    }),
    [token, username, isLoading, sessionExpired, clearSessionExpired],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return ctx;
}

export { STORED_USERNAME_KEY };
