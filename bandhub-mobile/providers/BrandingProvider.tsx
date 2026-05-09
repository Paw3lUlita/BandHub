import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { fetchSiteSettings } from '@/lib/api';
import { SiteSettings } from '@/types/api';

type BrandingContextValue = {
  settings: SiteSettings | null;
  isLoading: boolean;
  error: string | null;
  reload: () => Promise<void>;
};

const BrandingContext = createContext<BrandingContextValue | undefined>(undefined);

export function BrandingProvider({ children }: { children: React.ReactNode }) {
  const [settings, setSettings] = useState<SiteSettings | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async () => {
    setError(null);
    try {
      const next = await fetchSiteSettings();
      setSettings(next);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Nie udało się pobrać ustawień strony');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    reload();
  }, [reload]);

  const value = useMemo<BrandingContextValue>(
    () => ({ settings, isLoading, error, reload }),
    [settings, isLoading, error, reload],
  );

  return <BrandingContext.Provider value={value}>{children}</BrandingContext.Provider>;
}

export function useBranding() {
  const ctx = useContext(BrandingContext);
  if (!ctx) {
    throw new Error('useBranding must be used inside BrandingProvider');
  }
  return ctx;
}
