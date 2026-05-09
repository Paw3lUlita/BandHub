import { absoluteApiUrl } from '@/lib/config';

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  token?: string | null;
};

/**
 * Bledy z backendu: trzymamy klase wyjatku w jednym miejscu, zeby ekrany
 * mogly czytac status i validationErrors zamiast parsowac string-a.
 * Backend zwraca ApiErrorResponse { message, validationErrors, status, ... }.
 */
export class ApiError extends Error {
  status: number;
  validationErrors: Record<string, string>;

  constructor(message: string, status: number, validationErrors: Record<string, string> = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.validationErrors = validationErrors;
  }
}

function parseErrorPayload(text: string, status: number): ApiError {
  if (!text) {
    return new ApiError(`Request failed (${status})`, status);
  }

  try {
    const payload = JSON.parse(text) as {
      message?: string;
      error?: string;
      validationErrors?: Record<string, string>;
    };

    const validationErrors = payload.validationErrors ?? {};
    const validationKeys = Object.keys(validationErrors);
    const validationLine =
      validationKeys.length > 0
        ? validationKeys.map((key) => `${key}: ${validationErrors[key]}`).join('\n')
        : null;

    const message =
      validationLine ??
      payload.message ??
      payload.error ??
      `Request failed (${status})`;

    return new ApiError(message, status, validationErrors);
  } catch {
    return new ApiError(text, status);
  }
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {
    Accept: 'application/json',
  };

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  if (options.token) {
    headers.Authorization = `Bearer ${options.token}`;
  }

  const response = await fetch(absoluteApiUrl(path), {
    method: options.method ?? 'GET',
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  });

  if (!response.ok) {
    const text = await response.text();
    throw parseErrorPayload(text, response.status);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export async function apiRequestRaw(path: string, options: RequestOptions = {}): Promise<Response> {
  const headers: Record<string, string> = {
    Accept: 'application/json',
  };

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  if (options.token) {
    headers.Authorization = `Bearer ${options.token}`;
  }

  const response = await fetch(absoluteApiUrl(path), {
    method: options.method ?? 'GET',
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  });

  if (!response.ok) {
    const text = await response.text();
    throw parseErrorPayload(text, response.status);
  }

  return response;
}
