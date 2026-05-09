import { useMemo, useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { RegisterInput, useAuth } from '@/providers/AuthProvider';
import { useText } from '@/providers/DictionaryProvider';

type Mode = 'login' | 'register';

// Regex zgodny z domyslnym User Profile Keycloaka oraz @Pattern w FanRegistrationRequest
// na backendzie. Trzymamy go w jednym miejscu jako stala, zeby latwo zmienic gdy ktos
// poluzuje policy w realmie.
const USERNAME_REGEX = /^[A-Za-z0-9._@-]+$/;
const PASSWORD_MIN_LENGTH = 8;

export function AuthForm() {
  const { login, register } = useAuth();
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
          <ActivityIndicator color="#0f172a" />
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
    backgroundColor: '#1e293b',
    borderRadius: 16,
    padding: 18,
    gap: 14,
    borderWidth: 1,
    borderColor: '#334155',
  },
  tabs: {
    flexDirection: 'row',
    backgroundColor: '#0f172a',
    borderRadius: 12,
    padding: 4,
    gap: 4,
  },
  tab: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: 10,
    alignItems: 'center',
  },
  tabActive: {
    backgroundColor: '#38bdf8',
  },
  tabText: {
    color: '#94a3b8',
    fontWeight: '600',
  },
  tabTextActive: {
    color: '#0f172a',
  },
  subtitle: {
    color: '#94a3b8',
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
    color: '#cbd5e1',
    fontSize: 13,
    fontWeight: '500',
  },
  input: {
    backgroundColor: '#0f172a',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#334155',
    color: '#f8fafc',
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
  inputError: {
    borderColor: '#f43f5e',
  },
  fieldHint: {
    color: '#64748b',
    fontSize: 12,
    marginTop: 4,
  },
  fieldError: {
    color: '#fda4af',
    fontSize: 12,
    marginTop: 4,
  },
  row: {
    flexDirection: 'row',
    gap: 10,
  },
  error: {
    color: '#fda4af',
    fontSize: 13,
  },
  submit: {
    backgroundColor: '#38bdf8',
    borderRadius: 12,
    paddingVertical: 12,
    alignItems: 'center',
  },
  submitPressed: {
    opacity: 0.85,
  },
  submitDisabled: {
    opacity: 0.5,
  },
  submitText: {
    color: '#0f172a',
    fontWeight: '700',
    fontSize: 15,
  },
});
