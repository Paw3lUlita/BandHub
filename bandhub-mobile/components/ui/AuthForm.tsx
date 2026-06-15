import { useMemo, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { RegisterInput, useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';
import { colors, radius, spacing } from '@/constants/theme';

type Mode = 'login' | 'register';

// Regex zgodny z domyslnym User Profile Keycloaka oraz @Pattern w FanRegistrationRequest
// na backendzie. Trzymamy go w jednym miejscu jako stala, zeby latwo zmienic gdy ktos
// poluzuje policy w realmie.
const USERNAME_REGEX = /^[A-Za-z0-9._@-]+$/;
const PASSWORD_MIN_LENGTH = 8;

export function AuthForm() {
  const { login, register, sessionExpired, clearSessionExpired } = useAuth();
  const t = useText();
  const [mode, setMode] = useState<Mode>('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isRegister = mode === 'register';

  // Walidacja w UI - zlapanie spacji / niedozwolonego znaku zanim w ogole uderzymy
  // w backend (dzieki temu nie zalogujemy sie do Keycloaka tylko po to zeby dostac 400).
  const usernameValidationError = useMemo(() => {
    if (!isRegister || username.length === 0) {
      return null;
    }
    if (username.length < 3) {
      return t('auth.error.username.tooShort', 'Nazwa uzytkownika musi miec minimum 3 znaki.');
    }
    if (!USERNAME_REGEX.test(username)) {
      return t(
        'auth.error.username.invalidCharacter',
        'Nazwa uzytkownika moze zawierac tylko litery, cyfry oraz . _ - @ (bez spacji).',
      );
    }
    return null;
  }, [isRegister, username, t]);

  const passwordValidationError = useMemo(() => {
    if (!isRegister || password.length === 0) {
      return null;
    }
    if (password.length < PASSWORD_MIN_LENGTH) {
      return t(
        'auth.error.password.tooShort',
        `Haslo musi miec minimum ${PASSWORD_MIN_LENGTH} znakow.`,
      );
    }
    return null;
  }, [isRegister, password, t]);

  const submitDisabled = (() => {
    if (submitting || !username || !password) {
      return true;
    }
    if (isRegister && (usernameValidationError !== null || passwordValidationError !== null)) {
      return true;
    }
    return false;
  })();

  const submit = async () => {
    setError(null);
    setSubmitting(true);

    try {
      if (isRegister) {
        if (usernameValidationError) {
          throw new Error(usernameValidationError);
        }
        if (passwordValidationError) {
          throw new Error(passwordValidationError);
        }
        const payload: RegisterInput = {
          username: username.trim(),
          password,
          email: email.trim() || undefined,
          firstName: firstName.trim() || undefined,
          lastName: lastName.trim() || undefined,
        };
        await register(payload);
      } else {
        await login(username.trim(), password);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : t('auth.error.generic', 'Operacja nieudana'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <View style={styles.card}>
      {sessionExpired ? (
        <Pressable onPress={clearSessionExpired} style={styles.sessionBanner}>
          <Text style={styles.sessionBannerText}>
            {t('auth.session.expired', 'Twoja sesja wygasla. Zaloguj sie ponownie.')}
          </Text>
        </Pressable>
      ) : null}

      <View style={styles.tabs}>
        <Pressable
          onPress={() => setMode('login')}
          style={[styles.tab, !isRegister && styles.tabActive]}>
          <Text style={[styles.tabText, !isRegister && styles.tabTextActive]}>
            {t('auth.tab.login', 'Logowanie')}
          </Text>
        </Pressable>
        <Pressable
          onPress={() => setMode('register')}
          style={[styles.tab, isRegister && styles.tabActive]}>
          <Text style={[styles.tabText, isRegister && styles.tabTextActive]}>
            {t('auth.tab.register', 'Rejestracja')}
          </Text>
        </Pressable>
      </View>

      <Text style={styles.subtitle}>
        {isRegister
          ? t(
              'auth.subtitle.register',
              'Utworz konto fana w BandHub. Otrzymasz role FAN i wpadniesz prosto do appki.',
            )
          : t(
              'auth.subtitle.login',
              'Zaloguj sie danymi z konta fana, aby kupowac bilety i merch.',
            )}
      </Text>

      <View style={styles.field}>
        <Text style={styles.label}>{t('auth.label.username', 'Nazwa uzytkownika')}</Text>
        <TextInput
          value={username}
          onChangeText={setUsername}
          autoCapitalize="none"
          autoCorrect={false}
          placeholder={t('auth.placeholder.username', 'np. fan123')}
          placeholderTextColor="#64748b"
          style={[styles.input, usernameValidationError ? styles.inputError : null]}
        />
        {isRegister ? (
          <Text style={usernameValidationError ? styles.fieldError : styles.fieldHint}>
            {usernameValidationError ??
              t(
                'auth.hint.username',
                'Litery, cyfry oraz . _ - @ (bez spacji), 3-64 znakow.',
              )}
          </Text>
        ) : null}
      </View>

      <View style={styles.field}>
        <Text style={styles.label}>{t('auth.label.password', 'Haslo')}</Text>
        <TextInput
          value={password}
          onChangeText={setPassword}
          secureTextEntry
          placeholder={t('auth.placeholder.password', 'minimum 8 znakow')}
          placeholderTextColor="#64748b"
          style={[styles.input, passwordValidationError ? styles.inputError : null]}
        />
        {isRegister && passwordValidationError ? (
          <Text style={styles.fieldError}>{passwordValidationError}</Text>
        ) : null}
      </View>

      {isRegister ? (
        <>
          <View style={styles.field}>
            <Text style={styles.label}>{t('auth.label.email', 'Email (opcjonalnie)')}</Text>
            <TextInput
              value={email}
              onChangeText={setEmail}
              keyboardType="email-address"
              autoCapitalize="none"
              placeholder={t('auth.placeholder.email', 'fan@bandhub.pl')}
              placeholderTextColor="#64748b"
              style={styles.input}
            />
          </View>

          <View style={styles.row}>
            <View style={[styles.field, styles.flex]}>
              <Text style={styles.label}>{t('auth.label.firstName', 'Imie')}</Text>
              <TextInput
                value={firstName}
                onChangeText={setFirstName}
                placeholder="Jan"
                placeholderTextColor="#64748b"
                style={styles.input}
              />
            </View>
            <View style={[styles.field, styles.flex]}>
              <Text style={styles.label}>{t('auth.label.lastName', 'Nazwisko')}</Text>
              <TextInput
                value={lastName}
                onChangeText={setLastName}
                placeholder="Kowalski"
                placeholderTextColor="#64748b"
                style={styles.input}
              />
            </View>
          </View>
        </>
      ) : null}

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <Pressable
        onPress={submit}
        disabled={submitDisabled}
        style={({ pressed }) => [
          styles.submit,
          submitDisabled && styles.submitDisabled,
          pressed && styles.submitPressed,
        ]}>
        {submitting ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.submitText}>
            {isRegister
              ? t('auth.button.register', 'Zarejestruj sie')
              : t('auth.button.login', 'Zaloguj sie')}
          </Text>
        )}
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: radius.xl,
    padding: spacing.lg,
    gap: spacing.md,
    borderWidth: 1,
    borderColor: colors.border,
  },
  sessionBanner: {
    backgroundColor: '#422006',
    borderRadius: radius.md,
    padding: spacing.md,
    borderWidth: 1,
    borderColor: colors.warning,
  },
  sessionBannerText: {
    color: '#fde68a',
    fontSize: 13,
    lineHeight: 18,
  },
  tabs: {
    flexDirection: 'row',
    backgroundColor: colors.background,
    borderRadius: radius.md,
    padding: 4,
    gap: 4,
  },
  tab: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: radius.sm,
    alignItems: 'center',
  },
  tabActive: {
    backgroundColor: colors.primary,
  },
  tabText: {
    color: colors.textMuted,
    fontWeight: '600',
  },
  tabTextActive: {
    color: '#fff',
  },
  subtitle: {
    color: colors.textMuted,
    fontSize: 13,
    lineHeight: 18,
  },
  field: {
    gap: 6,
  },
  flex: {
    flex: 1,
  },
  label: {
    color: colors.textSecondary,
    fontSize: 13,
    fontWeight: '500',
  },
  input: {
    backgroundColor: colors.background,
    borderRadius: radius.md,
    borderWidth: 1,
    borderColor: colors.border,
    color: colors.text,
    paddingHorizontal: spacing.md,
    paddingVertical: 10,
  },
  inputError: {
    borderColor: colors.errorDark,
  },
  fieldHint: {
    color: colors.textDim,
    fontSize: 12,
    marginTop: 4,
  },
  fieldError: {
    color: colors.error,
    fontSize: 12,
    marginTop: 4,
  },
  row: {
    flexDirection: 'row',
    gap: 10,
  },
  error: {
    color: colors.error,
    fontSize: 13,
  },
  submit: {
    backgroundColor: colors.primary,
    borderRadius: radius.md,
    paddingVertical: spacing.md,
    alignItems: 'center',
  },
  submitPressed: {
    opacity: 0.85,
  },
  submitDisabled: {
    opacity: 0.5,
  },
  submitText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 15,
  },
});
