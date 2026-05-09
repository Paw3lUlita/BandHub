import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { fetchUiDictionary } from '@/lib/api';

/**
 * UI dictionary provider - mikro-copywriting sterowany z bazy.
 * Mobilka pobiera plaska mape z /api/public/ui-dictionary i udostepnia helper `t(key, fallback)`.
 *
 * Zalozenia:
 * - kazda widoczna w UI etykieta MUSI miec klucz w slowniku (zarzadzanym z admin web).
 * - jezeli backend nie odpowie / klucz nie istnieje, uzywamy fallbacku z kodu (zeby
 *   appka byla uzywalna nawet w czasie awarii backendu i zeby mozna bylo spokojnie
 *   developowac nowe widoki bez ciaglego dodawania kluczy do migracji).
 */
type DictionaryContextValue = {
  entries: Record<string, string>;
  isLoading: boolean;
  error: string | null;
  reload: () => Promise<void>;
  t: (key: string, fallback?: string) => string;
};

const DictionaryContext = createContext<DictionaryContextValue | undefined>(undefined);

export function DictionaryProvider({ children }: { children: React.ReactNode }) {
  const [entries, setEntries] = useState<Record<string, string>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async () => {
    setError(null);
    try {
      const next = await fetchUiDictionary();
      setEntries(next ?? {});
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Nie udalo sie pobrac slownika UI');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    reload();
  }, [reload]);

  const t = useCallback(
    (key: string, fallback?: string) => {
      const value = entries[key];
      if (value && value.length > 0) {
        return value;
      }
      return fallback ?? key;
    },
    [entries],
  );

  const value = useMemo<DictionaryContextValue>(
    () => ({ entries, isLoading, error, reload, t }),
    [entries, isLoading, error, reload, t],
  );

  return <DictionaryContext.Provider value={value}>{children}</DictionaryContext.Provider>;
}

export function useDictionary() {
  const ctx = useContext(DictionaryContext);
  if (!ctx) {
    throw new Error('useDictionary must be used inside DictionaryProvider');
  }
  return ctx;
}

export function useText() {
  const { t } = useDictionary();
  return t;
}
