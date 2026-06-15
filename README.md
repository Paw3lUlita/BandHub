# BandHub

Zintegrowany System Informatyczny (ZSI) wspierający zarządzanie procesami biznesowymi niezależnego zespołu muzycznego: sprzedaż merchu, bilety, logistyka tras koncertowych, treści dla fanów oraz raportowanie.

## Co zawiera projekt

| Komponent | Technologia | Rola |
|-----------|-------------|------|
| **Backend** | Java 21, Spring Boot 3.4 | API REST, logika biznesowa, baza danych |
| **Panel admina** | Angular 21, Tailwind, daisyUI | Zarządzanie dla menedżera zespołu |
| **Aplikacja mobilna** | React Native (Expo) | Kanał dla fanów: newsy, koncerty, merch, bilety |
| **Infrastruktura** | Docker Compose | PostgreSQL 16 + Keycloak 26 |

Architektura backendu to **modularny monolit** (DDD, porty i adaptery). Uwierzytelnianie przez **Keycloak** (OAuth2/OIDC, JWT).

## Wymagania

- **Docker** i **Docker Compose**
- **Java 21** (JDK)
- **Node.js 20+** i **npm**
- (Opcjonalnie) **Expo Go** na telefonie do testów mobilki

## Uruchomienie krok po kroku

### 1. Infrastruktura (baza + Keycloak)

W katalogu głównym repozytorium:

```bash
docker compose up -d
```

| Usługa | Adres | Dane dostępowe |
|--------|-------|----------------|
| PostgreSQL | `localhost:5432` | baza: `bandhub_db`, user: `bandhub_user`, hasło: `bandhub_password` |
| Keycloak | http://localhost:8081 | admin: `admin` / `admin` |

### 2. Konfiguracja Keycloak (jednorazowo)

W panelu Keycloak (http://localhost:8081):

1. Utwórz realm **`bandhub-realm`**
2. Utwórz klienta **`bandhub-public-client`** (typ: OpenID Connect, publiczny; włącz *Standard flow* i *Direct access grants*)
3. Dodaj role realmowe: **`ADMIN`**, **`FAN`**
4. Utwórz użytkownika testowego z rolą **ADMIN** (do panelu web) oraz opcjonalnie fana z rolą **FAN** (do mobilki)

### 3. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Na Windows:

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

API: http://localhost:8080  
Przy starcie **Flyway** wykonuje migracje bazy automatycznie.

### 4. Panel administracyjny (web)

```bash
cd zsi-admin-web
npm install
npm start
```

Panel: http://localhost:4200 → logowanie przez Keycloak → `/admin`

### 5. Aplikacja mobilna (fan)

```bash
cd bandhub-mobile
npm install
npx expo start
```

- **Emulator / Expo Web:** domyślnie łączy się z `http://localhost:8080`
- **Telefon fizyczny:** ustaw adres IP komputera w sieci LAN, np.:

```bash
set EXPO_PUBLIC_API_BASE_URL=http://192.168.1.10:8080
npx expo start
```

## Porty

| Usługa | Port |
|--------|------|
| Backend (Spring Boot) | 8080 |
| Keycloak | 8081 |
| PostgreSQL | 5432 |
| Panel Angular (dev) | 4200 |

## Główne funkcje

- **E-commerce** — produkty, zamówienia, płatności, wysyłki (web + mobile)
- **Ticketing** — koncerty, pule biletów, zakup i kody QR (mobile), skanowanie (web)
- **Logistyka** — trasy, koszty, przychody, rozliczenia tras
- **CMS** — aktualności, galeria, branding i słownik UI mobilki
- **Raporty** — merch, bilety, eksport PDF/Excel, szablony DOCX
- **IAM** — użytkownicy, role i grupy (Keycloak + panel admina)

## Struktura repozytorium

```text
BandHub/
├── backend/           # Spring Boot API
├── zsi-admin-web/     # Panel Angular
├── bandhub-mobile/    # Aplikacja Expo
├── docs/              # Dokumentacja projektu
├── infra/             # Dane PostgreSQL (Docker volume)
└── docker-compose.yml
```

## Dokumentacja

Szczegóły architektury, modułów i scenariuszy E2E:

- [`docs/kompendium-projektu.md`](docs/kompendium-projektu.md) — przewodnik po całym systemie
- [`docs/dokumentacja-techniczna.md`](docs/dokumentacja-techniczna.md) — dokumentacja techniczna i sprinty
- [`docs/architecture-and-patterns.md`](docs/architecture-and-patterns.md) — wzorce i decyzje architektoniczne

## Zatrzymanie środowiska

```bash
docker compose down
```

Backend i frontendy zatrzymujesz standardowo (`Ctrl+C` w terminalu).
