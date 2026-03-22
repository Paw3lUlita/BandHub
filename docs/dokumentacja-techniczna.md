# BandHub - Dokumentacja Techniczna

## 1. Cel dokumentu

Ten dokument jest centralnym miejscem opisu technicznego projektu `BandHub`.
Zawiera:

- aktualny stan architektury i komponentow,
- opis sposobu dzialania systemu,
- zbiorcze podsumowania zrealizowanych sprintow,
- miejsce na regularne dopisywanie kolejnych postepow.

Dokument jest zrodlowym artefaktem technicznym do przegladow, raportowania
postepu i przygotowania materialow do obrony.

---

## 2. Zakres systemu

Projekt: `Zintegrowany System Informatyczny (BandHub)`.

Aktualnie system sklada sie z:

- backendu `Spring Boot` (modularny monolit),
- panelu administracyjnego web `Angular` (`zsi-admin-web`),
- infrastruktury lokalnej uruchamianej przez `Docker Compose`
  (`PostgreSQL + Keycloak`).

Docelowo system bedzie rozszerzony o aplikacje mobilna (fanowska), przy
zachowaniu wspolnego backendu i wspolnej bazy danych.

---

## 3. Architektura techniczna

### 3.1. Styl architektoniczny

- `Modularny Monolit` (jeden backend, izolowane moduly biznesowe).
- `Domain-Driven Design (DDD)`.
- `Ports and Adapters (Hexagonal Architecture)`.

### 3.2. Warstwy backendu

- `Domain` - encje/agregaty i logika biznesowa.
- `Application/Service` - orkiestracja przypadkow uzycia i transakcji.
- `Infrastructure` - adaptery JPA, integracje zewnetrzne.
- `API` - kontrolery REST.

### 3.3. Podejscie do danych

- `Code First` po stronie modelu Java (`@Entity`).
- `Flyway` jako jedyne zrodlo zmian schematu SQL.
- `spring.jpa.hibernate.ddl-auto=validate`.

### 3.4. Bezpieczenstwo

- `Keycloak` jako serwer autoryzacji i uwierzytelniania.
- Backend jako `OAuth2 Resource Server` (JWT).
- Frontend z OIDC (`angular-auth-oidc-client`), guardy i tokeny JWT.

---

## 4. Stack technologiczny

### 4.1. Backend

- Java 21
- Spring Boot 3.4+
- Spring Data JPA
- Flyway
- Spring Security (OAuth2 Resource Server)

### 4.2. Frontend web

- Angular (Standalone Components)
- Reactive Forms
- Tailwind CSS + DaisyUI
- angular-auth-oidc-client

### 4.3. Dane i infrastruktura

- PostgreSQL 16
- Keycloak 26
- Docker / Docker Compose

---

## 5. Struktura repozytorium

Przyblizony uklad:

```text
BandHub/
├── .gitignore
├── docker-compose.yml
├── infra/
├── docs/
│   ├── architecture-and-patterns.md
│   ├── wymagania-projektu.md
│   ├── rozpisane-sprinty.md
│   └── dokumentacja-techniczna.md
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/bandhub/zsi/
│       └── resources/
│           ├── application.properties
│           └── db/migration/
└── zsi-admin-web/
```

---

## 6. Jak dziala system (przeplyw)

1. Uzytkownik (admin) loguje sie przez Keycloak.
2. Frontend web pobiera token i wywoluje backend REST.
3. Backend waliduje JWT i role.
4. Warstwa serwisowa uruchamia logike domenowa i repozytoria.
5. Dane sa zapisywane/odczytywane z PostgreSQL przez JPA.
6. Zmiany schematu sa wdrazane przez migracje Flyway.

---

## 7. Podsumowanie sprintow 0-6

Ponizej znajduje sie zbiorcza dokumentacja wykonanych prac na podstawie notatek
projektowych.

## Sprint 0 - Infrastruktura i Architektura

Status: `Done`  
Cel: konfiguracja srodowiska, Docker, auth oraz szkielet backendu.

### Zrealizowane

- Ustalono zasady architektury: modularny monolit, DDD, Ports and Adapters.
- Skonfigurowano infrastrukture `docker-compose.yml`:
  - `zsi-database` (`postgres:16-alpine`) na porcie `5432`,
  - `zsi-keycloak` (`quay.io/keycloak/keycloak:26.0`) na porcie `8081`.
- Skonfigurowano Realm `bandhub-realm`:
  - klient `bandhub-public-client`,
  - role `ROLE_ADMIN`, `ROLE_FAN`,
  - testowy user `manager`.
- Backend podlaczony do PostgreSQL i Keycloak.
- Hibernate ustawiony na `validate`, Flyway aktywny.

## Sprint 1 - Baza danych i modul uzytkownikow

Status: `Done`  
Cel: bazowy model danych, migracje i integracja z API Keycloak.

### Zrealizowane

- Utworzono migracje SQL i model poczatkowy:
  - 9 tabel biznesowych + tabela techniczna Flyway.
- Zakres tabel:
  - E-commerce: `product_categories`, `products`, `orders`, `order_items`
  - Ticketing: `venues`, `concerts`, `ticket_pools`, `tickets`
  - CMS: `news_articles`
- W warstwie domeny wdrozono:
  - encje `Product`, `ProductCategory`,
  - value object `Money` (`@Embeddable`).
- Integracja z Keycloak przez `UserAdminController`:
  - `GET /api/admin/users`
  - `GET /api/admin/users/{id}/roles`

## Sprint 2 - Backend E-commerce (produkty)

Status: `Done`  
Cel: logika biznesowa i API administracyjne dla produktow.

### Zrealizowane

- Dodawanie, pobieranie i usuwanie produktow.
- Obsluga ceny przez value object `Money`.
- Walidacja kategorii i stanow magazynowych.
- Struktura zgodna z Ports and Adapters:
  - agregaty domenowe,
  - porty repozytoriow,
  - serwisy transakcyjne,
  - adaptery JPA i kontrolery REST.
- Kluczowy kontrakt API:
  - `GET /api/admin/products`
  - `POST /api/admin/products`

## Sprint 3 - Admin Panel, CRUD i Security

Status: `Done`  
Cel: panel web admina i bezpieczny CRUD produktow/kategorii.

### Zrealizowane

- Frontend auth:
  - OIDC (`angular-auth-oidc-client`),
  - interceptor tokena JWT,
  - `auth.guard.ts` dla tras `/admin`.
- Backend security:
  - konfiguracja resource server i walidacja tokena.
- DDD:
  - rich domain model (zmiany stanu przez metody biznesowe),
  - DTO jako Java records.
- Frontend:
  - CRUD produktow,
  - CRUD kategorii,
  - Reactive Forms i dynamiczne formularze.
- UI:
  - Tailwind + DaisyUI,
  - spojny layout, walidacje i semantyczne klasy stylow.

## Sprint 4 - Modul Zamowien (Order Management)

Status: `Done`  
Cel: proces skladania zamowien, aktualizacja magazynu, transakcyjnosc.

### Zrealizowane

- Wdrozono domene:
  - `Order` (aggregate root),
  - `OrderItem`,
  - `OrderStatus`.
- Zastosowano snapshot pattern:
  - `OrderItem` przechowuje nazwe/cene z chwili zakupu.
- Zapewniono transakcyjnosc:
  - zapis zamowienia + redukcja stanu magazynowego w jednej transakcji,
  - rollback przy bledach (np. brak towaru).
- Naprawiono generowanie ID w `order_items` po stronie bazy (`gen_random_uuid()`).
- Frontend:
  - reaktywne odswiezanie list (BehaviorSubject),
  - statusy i akcje warunkowe w UI.

## Sprint 5 - Modul Ticketing (wydarzenia)

Status: `Done`  
Cel: zarzadzanie koncertami, miejscami i pulami biletow.

### Zrealizowane

- Rozszerzono model bazy i migracje ticketing.
- Wdrozono relacje:
  - `venues` -> `concerts` -> `ticket_pools`.
- Logika biznesowa agregatu `Concert`:
  - konfiguracja pul biletow tylko w kontekscie koncertu.
- Transakcje i kaskady:
  - zapis koncertu razem z pulami.
- Security:
  - poprawione mapowanie rol z Keycloak (eliminacja problemu 403).
- Frontend:
  - dynamiczny `FormArray` dla wielu pul biletow,
  - widok master-detail koncertu.

## Sprint 6 - Modul CMS (tresci i pliki)

Status: `Done`  
Cel: dynamiczne zarzadzanie tresciami i plikami bez hardcodowania.

### Zrealizowane

- Utworzono modul `com.bandhub.zsi.cms`:
  - `NewsArticle` (agregat),
  - `GalleryImage` (encja metadanych pliku),
  - porty i adaptery repozytoriow.
- Dodano kontrolery:
  - `GalleryAdminController` (upload/usuwanie),
  - `NewsAdminController` (CRUD newsow),
  - `GalleryPublicController` (serwowanie plikow publicznych).
- Security:
  - dostep anonimowy do `/api/public/**` dla zasobow publicznych.
- Migracja `V5__Add_CMS_Module.sql`:
  - tabela `gallery_images`,
  - rozszerzenie `news_articles` o `image_url`.
- Obsluga plikow:
  - fizyczny zapis na dysku,
  - konfiguracja `app.upload.dir=./uploads`,
  - unikalne nazwy plikow.
- Frontend CMS:
  - galeria (upload, grid, usuwanie),
  - lista aktualnosci,
  - formularz newsa z wyborem obrazka i podgladem.
- Routing i layout:
  - dodane trasy `/admin/gallery`, `/admin/news`,
  - sekcja menu CMS w panelu admin.

---

## 8. Status biezacy po sprintach 0-6

Zrealizowano fundament systemu:

- dzialajaca infrastruktura lokalna (DB + Auth),
- backend z glownymi modulami biznesowymi,
- panel administracyjny web z kluczowymi widokami i CRUD,
- obsluga tresci dynamicznych (CMS) i plikow.

Obszary do dalszej realizacji sa planowane i opisywane w
`docs/rozpisane-sprinty.md`.

---

## 9. Sprint 7 - Modul Logistyki Trasy (Tour Logistics)

Status: `Done (MVP)`  
Cel: wdrozenie modulu planowania tras i rejestracji kosztow w panelu admina.

### Zrealizowane

- Dodano migracje `V6__Add_Logistics.sql`:
  - utworzenie tabel `tours` i `tour_costs`,
  - powiazanie koncertow z trasa przez `concerts.tour_id`,
  - usuniecie starego pola `concerts.tour_name`.
- W backendzie utworzono modul logistyczny:
  - domena: `Tour`, `TourCost`,
  - port: `TourRepository`,
  - adapter JPA: `SqlTourRepository`,
  - serwis aplikacyjny: `LogisticsAdminService`,
  - API admina: `LogisticsAdminController`.
- Zaimplementowane endpointy:
  - `POST /api/admin/logistics/tours` - planowanie trasy,
  - `POST /api/admin/logistics/tours/{tourId}/costs` - dodanie kosztu do trasy,
  - `GET /api/admin/logistics/tours` - lista tras,
  - `GET /api/admin/logistics/tours/{id}` - szczegoly trasy z kosztami.
- Zaimplementowano widoki Angular:
  - `TourListComponent` (lista tras),
  - `TourAddComponent` (formularz planowania trasy),
  - `TourDetailComponent` (panel logistyki + rejestr kosztow + suma kosztow).
- Dodano serwis frontendowy `LogisticsService` i routing:
  - `/admin/logistics`,
  - `/admin/logistics/new`,
  - `/admin/logistics/:id`.

### Decyzje techniczne

- Model kosztu oparty o wspolny value object `Money` (`amount + currency`).
- Koszty utrzymywane jako relacja `@OneToMany` w agregacie `Tour`
  (`cascade = ALL`, `orphanRemoval = true`).
- Odczyty szczegolow trasy zwracane jako gotowe DTO, aby uniknac problemow z
  lazy loading.
- Dostep do API ograniczony do roli administratora (`@PreAuthorize("hasRole('ADMIN')")`).

### Ryzyka / otwarte tematy

- Zakres MVP nie obejmuje jeszcze pelnego CRUD dla tras i kosztow
  (brak aktualizacji/usuwania).
- Brak osobnych encji etapow trasy (`tour_legs`) i bardziej granularnego modelu
  kosztow (`cost_items`) z pierwotnej rozpiski.
- Brak automatycznego raportu rentownosci trasy (przychody vs koszty) - obecnie
  panel prezentuje sume kosztow.
- Brak eksportu raportu logistycznego do PDF/Excel.

### Wplyw na wymagania projektu

- Wzmacnia realizacje wymagania o obsludze rozbudowanych procesow biznesowych
  (modul logistyki trasy).
- Poszerza zakres dynamicznych widokow biznesowych w panelu administracyjnym.
- Rozwija model danych i logike domenowa pod przyszle raportowanie finansowe.

---

## 10. Sprint 7.2 - Manualne przychody i bilans rentownosci

Status: `Done`  
Cel: rozszerzenie modulu logistyki o reczne przychody oraz pelny bilans:
`przychody z biletow + przychody reczne - koszty`.

### Zrealizowane

- Dodano nowa migracje `V7__Add_Tour_Revenues.sql`:
  - utworzenie tabeli `tour_revenues`.
- Rozszerzono domene logistyki:
  - nowa encja `TourRevenue`,
  - agregat `Tour` posiada teraz kolekcje `revenues` i metody:
    `logRevenue`, `getRevenue`, `removeRevenue`.
- Rozszerzono API backendu o obsluge recznych przychodow:
  - `POST /api/admin/logistics/tours/{tourId}/revenues`,
  - `PUT /api/admin/logistics/tours/{tourId}/revenues/{revenueId}`,
  - `DELETE /api/admin/logistics/tours/{tourId}/revenues/{revenueId}`.
- Rozszerzono odpowiedzi logistyczne:
  - `TourDetailResponse` zawiera `costs` i `revenues`,
  - `TourProfitabilityResponse` zwraca:
    `ticketRevenue`, `manualRevenue`, `totalRevenue`, `totalCosts`, `balance`.
- W serwisie logistycznym wdrozono nowe liczenie rentownosci:
  - przychody z ticketingu (automatyczne),
  - przychody reczne z logistyki (manualne),
  - bilans koncowy po odjeciu kosztow.
- Frontend (`TourDetailComponent`) rozszerzono o:
  - rejestr przychodow recznych (lista, edycja, usuwanie),
  - formularz dodawania i edycji przychodu,
  - panel rentownosci z rozbiciem na:
    - przychod z biletow,
    - przychody reczne,
    - przychod laczny,
    - koszty,
    - bilans.

### Decyzje techniczne

- Zachowano spojny model pieniadza przez value object `Money`
  dla kosztow i przychodow.
- Ruch finansowy trasy zostal rozdzielony na:
  - przychody automatyczne (`ticket_pools`),
  - przychody reczne (`tour_revenues`),
  co upraszcza audyt i raportowanie.
- API i UI pozostaly zgodne z podejsciem DDD + Ports and Adapters.

### Ryzyka / otwarte tematy

- Nadal brak osobnych encji etapow trasy (`tour_legs`) i szczegolowych kategorii
  kosztow/przychodow (`cost_items`, typy przychodow).
- Brak eksportu raportu logistycznego do PDF/Excel.
- Brak automatycznego wydruku rozliczenia trasy na szablonie `.docx`.

### Wplyw na wymagania projektu

- Istotnie wzmacnia domkniecie procesu biznesowego logistyki trasy.
- Poprawia wiarygodnosc finansowa procesu (pelniejszy model przychodow i kosztow).
- Przygotowuje dane pod raporty biznesowe i rozliczenia koncowe.

---

## 11. Sprint 8 - Security, walidacja i standard listowania API

Status: `Done (backend) / In Progress (frontend UI)`  
Cel: podniesienie jakosci warstwy API przez hardening security, walidacje wejscia,
standaryzacje bledow oraz wdrozenie paginacji/sortowania/filtrowania na endpointach list.

### Zrealizowane

- Security:
  - wlaczono `@EnableMethodSecurity`,
  - wdrozono mapowanie rol z JWT Keycloak (`realm_access.roles`) do
    springowych `ROLE_*`,
  - utrzymano kompatybilnosc z `@PreAuthorize("hasRole('ADMIN')")`.
- Obsluga bledow:
  - dodano globalny handler wyjatkow (`GlobalExceptionHandler`),
  - ujednolicono format odpowiedzi bledu (`ApiErrorResponse`),
  - obsluga m.in. `400`, `403`, `404`, `500` + mapy bledow walidacji.
- Walidacja requestow:
  - dodano adnotacje `jakarta.validation` w DTO (`@NotBlank`, `@NotNull`,
    `@Positive`, `@Pattern`, `@Size`),
  - wlaczono `@Valid` w kontrolerach admin/public.
- Standaryzacja listowania:
  - dodano wspolny kontrakt `PageResponse<T>`,
  - dodano paginowane endpointy listujace:
    - `GET /api/admin/products/page`
    - `GET /api/admin/orders/page`
    - `GET /api/admin/concerts/page`
    - `GET /api/admin/logistics/tours/page`
    - `GET /api/admin/news/page`
  - wspierane parametry: `page`, `size`, `sortBy`, `sortDir`, `q`
    (+ `status` dla zamowien).
- Frontend (warstwa serwisow):
  - dopisano metody paginowane w serwisach Angular:
    `ProductService`, `OrderService`, `ConcertService`,
    `LogisticsService`, `CmsService`.

### Decyzje techniczne

- Paginowane endpointy zostaly dodane jako nowe trasy `.../page`,
  aby nie psuc aktualnych widokow korzystajacych ze starych endpointow `GET`.
- Walidacja oparta o `jakarta.validation` (standard API), z Hibernate Validator
  jako implementacja dostarczana przez Spring Boot.
- Utrzymano podejscie modularne i kontrakty DTO zgodne z dotychczasowa architektura.

### Ryzyka / otwarte tematy

- Do domkniecia w tym sprincie zostalo:
  - podpiecie paginacji, sortowania i filtrowania w komponentach UI
    (listy w panelu admina),
  - dodanie kontrolek UI (zmiana strony, rozmiar strony, sort po kolumnie,
    pole wyszukiwania) i integracja z nowymi endpointami `.../page`.

### Wplyw na wymagania projektu

- Wzmacnia wymagania dot. bezpieczenstwa i poprawnosci technicznej API.
- Przygotowuje fundament pod wymog operacji listowania/filtrowania/sortowania
  w warstwie interfejsu.
- Redukuje ryzyko bledow danych wejsciowych i niejednolitych odpowiedzi backendu.

---

## 12. Sprint 9 - Rozbudowa warstwy danych (lean)

Status: `Done (data layer + mappings)`  
Cel: domkniecie wymagan formalnych bazy danych (30+ tabel, widoki, funkcje, indeksy)
przy zachowaniu podejscia lean pod realia niezaleznej kapeli.

### Zrealizowane

- Dodano migracje:
  - `V8__Lean_Ecommerce_And_Fan_Mobile.sql`
  - `V9__Ticketing_Expansion.sql`
  - `V10__Logistics_Reporting_Views_Functions.sql`
- Rozszerzono model danych o tabele e-commerce essentials:
  - `payments`, `payment_transactions`, `shipments`, `order_status_history`.
- Rozszerzono ticketing (priorytet sprintu):
  - `ticket_orders`, `ticket_order_items`, `ticket_codes`,
    `ticket_validations`, `ticket_refunds`.
- Rozszerzono logistyke i raportowanie:
  - `tour_legs`, `tour_cost_categories`, `tour_revenue_categories`,
    `tour_settlements`, `report_runs`, `export_jobs`.
- Dodano warstwe fan/mobile:
  - `setlists`, `setlist_items`, `fan_favorites`, `fan_notifications`,
    `fan_notification_reads`, `fan_devices`.
- Dodano obiekty SQL wymagane formalnie:
  - widoki: `vw_tour_profitability`, `vw_top_concerts`, `vw_fan_activity`,
  - funkcje: `fn_close_tour_settlement`, `fn_fan_activity_summary`.
- Dodano indeksy pod filtry/listowania dla nowych tabel.
- Dodano mapowanie JPA encji dla nowych tabel (moduly:
  `ecommerce`, `ticketing`, `logistics`, `fan`, `reporting`).

### Decyzje techniczne

- Przyjeto podejscie lean: ograniczono e-commerce do tabel realnie potrzebnych
  w projekcie studenckim (bez multi-magazynu i nadmiarowej logistyki magazynowej).
- Dodatkowe tabele skierowano w obszary o wysokiej wartosci biznesowej:
  ticketing (kody, walidacje, refundy) oraz funkcje fanowskie mobile
  (setlisty, ulubione, notyfikacje, urzadzenia).
- Rozszerzenia SQL projektowano pod dalsze sprinty (CRUD, raporty, mobile),
  aby minimalizowac refaktoryzacje w kolejnych etapach.

### Uzasadnienie biznesowe nowych tabel (dla nowych osob w projekcie)

Ponizsza sekcja tlumaczy, jaki konkretny problem biznesowy rozwiazuje kazda
grupa tabel i dlaczego zostala dodana w Sprincie 9.

- `payments`:
  - oddziela zamowienie od procesu oplacenia (zamowienie moze byc zlozone,
    ale jeszcze nieoplacone),
  - umozliwia obsluge statusow platnosci (`PENDING`, `PAID`, `FAILED`,
    `REFUNDED`) bez nadpisywania danych zamowienia.
- `payment_transactions`:
  - przechowuje historie zdarzen od operatora platnosci (callbacki, ponowienia),
  - daje audyt i podstawe do debugowania problemow z platnosciami.
- `shipments`:
  - modeluje etap dostawy osobno od zamowienia (przewoznik, numer trackingu,
    status dostawy),
  - wspiera domkniecie procesu e-commerce "od zakupu do doreczenia".
- `order_status_history`:
  - przechowuje historie zmian statusu zamowienia (kto i kiedy zmienil status),
  - jest kluczowa do audytu i wiarygodnego raportowania operacyjnego.

- `ticket_orders`:
  - porzadkuje zakup biletow jako osobny proces biznesowy (fan -> koncert),
  - pozwala raportowac przychody i wolumen ticketingu niezaleznie od merchu.
- `ticket_order_items`:
  - zapisuje szczegoly zakupu per pula biletowa (VIP/Normal itd.),
  - pozwala analizowac oblozenie i skutecznosc cen per pula.
- `ticket_codes`:
  - przechowuje faktyczny kod biletu (np. QR) i jego lifecycle
    (`ACTIVE`, `USED`, `CANCELLED`),
  - to podstawa do kontroli wejscia i ochrony przed duplikatami.
- `ticket_validations`:
  - loguje kazda probe walidacji biletu (bramka, czas, rezultat),
  - umozliwia obsluge wejscia "na zywo" oraz analizy frekwencji po wydarzeniu.
- `ticket_refunds`:
  - obsluguje zwroty i reklamacje biletow, bez psucia historii sprzedazy,
  - spina ticketing z finansami i rozliczeniami.

- `tour_legs`:
  - rozbija trase na etapy (miasto, kolejnosc, data, budzet etapu),
  - pozwala planowac i rozliczac trase granularnie, nie tylko globalnie.
- `tour_cost_categories` i `tour_revenue_categories`:
  - wprowadzaja slowniki kategorii (np. paliwo, hotel, sponsoring),
  - standaryzuja raporty i pozniejsza analityke kosztow/przychodow.
- `tour_settlements`:
  - przechowuje finalne rozliczenie trasy (przychody, koszty, bilans),
  - jest punktem koncowym procesu logistyczno-finansowego.

- `setlists` i `setlist_items`:
  - realizuja realna potrzebe fana mobile: podglad setlisty koncertu,
  - wspieraja CMS bez hardcodowania tresci w aplikacji.
- `fan_favorites`:
  - przechowuje ulubione obiekty fana (np. koncerty, newsy),
  - poprawia personalizacje i retencje uzytkownika.
- `fan_notifications`:
  - trzyma tresc i metadane powiadomien (globalnych i per fan),
  - daje centralny mechanizm komunikacji z fanami.
- `fan_notification_reads`:
  - rejestruje przeczytanie powiadomienia przez fana,
  - umozliwia mierzenie skutecznosci komunikacji.
- `fan_devices`:
  - przechowuje urzadzenia/tokeny push fana (Android/iOS, wersja appki),
  - jest niezbedna tabela operacyjna do wysylki powiadomien mobilnych;
    bez niej push notifications nie da sie realizowac poprawnie.

- `report_runs`:
  - przechowuje historie uruchamiania raportow (kto, kiedy, z jakimi parametrami),
  - wspiera transparentnosc procesu raportowego.
- `export_jobs`:
  - modeluje asynchroniczne eksporty (PDF/Excel), ich status i wynikowy plik,
  - przygotowuje backend pod modul raportowy z wymagania projektu.

Dlaczego NIE dodano `warehouses` i `inventory_movements` w tym sprincie:
- dla niezaleznej kapeli bylaby to nadmiarowa zlozonosc operacyjna,
- Sprint 9 ma domknac wymagania formalne i jednoczesnie zachowac realizm domeny,
- priorytetowo rozbudowano ticketing + mobile fan features, bo daja wieksza
  wartosc biznesowa i lepiej wspieraja scenariusz demo na obrone.

### Ryzyka / otwarte tematy

- Integracja pelnego CRUD i UI dla nowych tabel jest planowana na kolejne sprinty
  (Sprint 10+), aby nie przeciazac Sprintu 9.
- Funkcje SQL operuja na statusach tekstowych; docelowo warto utrzymac
  slownik/status machine po stronie backendu i testy integralnosci.

### Wplyw na wymagania projektu

- Istotnie przybliza projekt do wymogu minimum 30 tabel.
- Dostarcza wymagane obiekty SQL: widoki, funkcje i indeksowanie.
- Rozszerza baze pod procesy fanowskie aplikacji mobilnej
  (np. setlisty koncertowe), zgodnie z profilem biznesowym BandHub.

---

## 13. Sprint 10 - CRUD + filtrowanie/sortowanie (backend-first)

Status: `Done`  
Cel: uzupelnienie brakujacych CRUD oraz standaryzacja endpointow listujacych.

### Zrealizowane

- Dodano macierz zakresu Sprintu 10:
  - `docs/sprint-10-macierz-crud.md`
- Opracowano i utrzymano aktualna macierz brakow:
  - `docs/sprint-10-macierz-crud.md`
- Potwierdzono kierunek realizacji: backend-first i wdrazanie
  endpointow zgodnie z dotychczasowym stylem repozytorium.
- Wdrozono od nowa pierwsze dwa obszary CRUD zgodnie ze stylem repo:
  - `ticket_codes` (`/api/admin/ticket-codes`)
  - `setlists` (`/api/admin/setlists`)
  - z zachowaniem podejscia: osobne pliki
    `Controller + Service + Repository + SqlRepository + DTO`.
- Rozszerzono backend CRUD (ten sam wzorzec plikow) o:
  - `payments` (`/api/admin/payments`)
  - `payment_transactions` (`/api/admin/payment-transactions`)
  - `shipments` (`/api/admin/shipments`)
  - `order_status_history` (`/api/admin/order-status-history`)
- Rozszerzono backend CRUD w module ticketing o:
  - `ticket_orders` (`/api/admin/ticket-orders`)
  - `ticket_order_items` (`/api/admin/ticket-order-items`)
  - `ticket_validations` (`/api/admin/ticket-validations`)
  - `ticket_refunds` (`/api/admin/ticket-refunds`)
- Rozszerzono backend CRUD w module fan/mobile o:
  - `setlist_items` (`/api/admin/setlist-items`)
  - `fan_favorites` (`/api/admin/fan-favorites`)
  - `fan_notifications` (`/api/admin/fan-notifications`)
  - `fan_notification_reads` (`/api/admin/fan-notification-reads`)
  - `fan_devices` (`/api/admin/fan-devices`)
- Domknieto ostatni pakiet backend CRUD (ten sam wzorzec plikow) o:
  - `tour_legs` (`/api/admin/tour-legs`)
  - `tour_cost_categories` (`/api/admin/tour-cost-categories`)
  - `tour_revenue_categories` (`/api/admin/tour-revenue-categories`)
  - `tour_settlements` (`/api/admin/tour-settlements`)
  - `report_runs` (`/api/admin/report-runs`)
  - `export_jobs` (`/api/admin/export-jobs`)

- Dodano widoki Angular dla wszystkich nowych encji:
  - Serwisy, komponenty list, routing, linki w sidebarze.
  - Pelne formularze add/edit dla tour-cost-categories i tour-revenue-categories.
  - Placeholder formularza dla pozostalych encji (linki Edytuj/Dodaj dzialaja).

### Domknięcie paginacji i sortowania (Sprint 10)

- **Backend**: Wszystkie endpointy `/page` korzystają z paginacji/sortowania/filtrowania po stronie DB
  (usunięto logikę in-memory stream; rozszerzono adaptery JPA o `findPage` z `PageRequest` i filtrem `q`).
- **Frontend**: Wszystkie listy admin przełączone na `getPage(...)`:
  - komponent współdzielony `ListPageControlsComponent` (Sortuj po, Kierunek, Na stronie, Szukaj),
  - widoczne domyślne sortowanie (np. data malejąco, nazwa rosnąco),
  - nawigacja stron (← Strona X / Y →) gdy totalPages > 1.
- **Listy objęte zmianami**: payment, payment-transaction, shipment, order-status-history,
  ticket-order, ticket-order-item, ticket-code, ticket-validation, ticket-refund,
  setlist, setlist-item, fan-favorite, fan-notification, fan-notification-read, fan-device,
  tour-leg, tour-cost-category, tour-revenue-category, tour-settlement,
  report-run, export-job, product, order, concert, news, tour.

### Jak działa paginacja (szczegóły techniczne na obronę)

Poniższy opis pozwala wytłumaczyć na obronie architekturę paginacji i uzasadnienie
wyboru klas `PagedResult` oraz `PageResponse`.

#### 1. Dwa poziomy abstrakcji

- **`PagedResult<T>`** (`shared.api.PagedResult`) – rekord wewnętrzny warstwy
  repozytorium. Zawiera `content` (lista elementów) i `totalElements` (liczba całkowita).
  Został wprowadzony, aby **nie uzależniać portu repozytorium od Spring Data**.
  Port (`ProductRepository`) definiuje `PagedResult<Product> findPage(...)` – adapter
  JPA (`SqlProductRepository`) zwraca ten wynik, mapując `Page<T>` z Spring Data
  na `PagedResult<T>`. Dzięki temu warstwa domeny i serwisu nie zna `org.springframework.data.domain.Page`.

- **`PageResponse<T>`** (`shared.api.PageResponse`) – kontrakt API REST zwracany
  do frontendu. Zawiera: `content`, `page`, `size`, `totalElements`, `totalPages`,
  `sortBy`, `sortDir`, `query`. Serwis mapuje `PagedResult` na `PageResponse` i
  dodaje metadane paginacji (np. `totalPages` z `Math.ceil(totalElements / size)`).

#### 2. Przepływ: od żądania HTTP do odpowiedzi

1. **Kontroler** (`ProductAdminController`) – odbiera `GET /api/admin/products/page`
   z parametrami: `page`, `size`, `sortBy`, `sortDir`, `q`. Wywołuje serwis.

2. **Serwis** (`ProductAdminService`) – wywołuje `productRepository.findPage(...)`,
   otrzymuje `PagedResult<Product>`. Mapuje encje na DTO i buduje `PageResponse.of(...)`.

3. **Port repozytorium** (`ProductRepository`) – interfejs z metodą
   `PagedResult<Product> findPage(int page, int size, String sortBy, String sortDir, String q)`.

4. **Adapter JPA** (`SqlProductRepository`) – implementacja:
   - buduje `PageRequest` z `PageRequest.of(page, size, Sort.by(dir, prop))`,
   - buduje filtr `q` (np. `LIKE %wartość%`) na whiteliscie pól (np. `name`, `description`, `category.name`),
   - wywołuje `JpaProductRepository.findAllFiltered(pattern, pageable)` – zapytanie JPQL
     z `@Query` i `Pageable`,
   - mapuje `Page<Product>` na `PagedResult<>(result.getContent(), result.getTotalElements())`.

#### 3. Dlaczego paginacja po stronie DB, a nie in-memory?

Wcześniej część serwisów używała `findAll().stream()` do filtrowania i sortowania.
Pobieranie wszystkich rekordów do pamięci i filtrowanie w Javie jest:
- nieefektywne przy dużej liczbie rekordów,
- obciążające dla serwera i pamięci.

Obecnie **sortowanie, filtrowanie i wybór strony** są wykonywane w zapytaniu SQL
(przez JPA `Pageable` i `@Query` z filtrem `LIKE`). Baza zwraca tylko żądaną stronę
(np. 10 rekordów), co skaluje się dużo lepiej.

#### 4. Kontrolki frontendowe

- **`ListPageControlsComponent`** – współdzielony komponent nad tabelą:
  `Sortuj po`, `Kierunek`, `Na stronie`, `Szukaj`, nawigacja stron.
- **Parametry domyślne** – każda lista ma ustawione domyślne `sortBy` i `sortDir`
  (np. data malejąco), zgodne z backendem.
- **Serwis Angular** – `getPage({ page, size, sortBy, sortDir, q })` wywołuje
  `GET .../page?page=0&size=10&sortBy=createdAt&sortDir=desc&q=`.

---

## 14. Sprint 11 - Zarządzanie użytkownikami i uprawnieniami (IAM)

Status: `Done`  
Cel: pełny moduł zarządzania użytkownikami, rolami i grupami przez UI admina, oparty o Keycloak Admin API.

### Uzasadnienie biznesowe (na obronę)

Wymagania projektu inżynierskiego obejmują **zarządzanie użytkownikami** – admin musi móc tworzyć konta, resetować hasła, aktywować/dezaktywować użytkowników oraz przypisywać im role i grupy. W BandHub użytkownicy nie są przechowywani w PostgreSQL – system korzysta z **Keycloak** jako serwera tożsamości (SSO, logowanie OIDC). Dlatego moduł IAM nie ma własnych encji JPA ani repozytoriów – zamiast tego **integruje się z Keycloak Admin API**, aby wykonywać operacje na użytkownikach, rolach i grupach w realmie Keycloak. Dzięki temu dane użytkowników pozostają w jednym miejscu (Keycloak), a backend Spring Boot pełni rolę **warstwy orkiestracyjnej** między panelem admina a Keycloak.

### Architektura modułu IAM (zgodność z architecture-and-patterns)

Moduł `com.bandhub.zsi.user` zachowuje strukturę pionowych plastrów:

```
com.bandhub.zsi.user/
├── UserAdminService.java      # [Primary Port] Serwis – orkiestracja, mapowanie błędów
├── adapter/                    # [Primary Adapter] REST API
│   ├── UserAdminController.java
│   ├── RoleAdminController.java
│   └── GroupAdminController.java
└── dto/                        # DTO jako Java records
    ├── CreateUserRequest.java, UpdateUserRequest.java, ResetPasswordRequest.java
    ├── UserResponse.java
    ├── RoleResponse.java, CreateRoleRequest.java, AssignRoleRequest.java
    └── GroupResponse.java, CreateGroupRequest.java
```

**Różnica względem modułów e-commerce/ticketing**: brak warstwy `domain` i `infrastructure` z repozytoriami JPA – dane użytkowników są w Keycloak, nie w PostgreSQL. `UserAdminService` pełni rolę zarówno serwisu, jak i **adaptera integracyjnego** do Keycloak (Secondary Adapter w sensie Hexagonal Architecture – wywołuje zewnętrzny system).

### Przepływ end-to-end (na obronę – krok po kroku)

1. **Admin otwiera panel** → Angular ładuje `/admin/users`. `authGuard` sprawdza, czy użytkownik jest zalogowany (OIDC). `adminGuard` sprawdza, czy w tokenie JWT jest rola `ADMIN` w `realm_access.roles`. Jeśli nie – przekierowanie na `/admin/dashboard`.

2. **Lista użytkowników** → `UserListComponent` wywołuje `UserService.getAllUsers()`, który wysyła `GET /api/admin/users` z nagłówkiem `Authorization: Bearer <token>`. Backend waliduje JWT (Spring Security OAuth2 Resource Server), `@PreAuthorize("hasRole('ADMIN')")` sprawdza rolę. `UserAdminController` deleguje do `UserAdminService.getAllUsers()`.

3. **UserAdminService → Keycloak** → `UserAdminService` używa `Keycloak` (Keycloak Admin Client) do wywołania `realm().users().list()`. Keycloak zwraca listę `UserRepresentation`. Serwis mapuje je na `UserResponse` (DTO) i zwraca do kontrolera.

4. **Tworzenie użytkownika** → Admin wypełnia formularz w `UserAddComponent` i klika „Utwórz”. `UserService.createUser(request)` wysyła `POST /api/admin/users` z `CreateUserRequest`. Backend: `UserAdminService.createUser()` – tworzy `UserRepresentation`, wywołuje `realm().users().create(user)`. Jeśli status 201 – pobiera ustawia hasło przez `resetPassword()`. Jeśli 409 – zwraca `IllegalArgumentException` (duplikat użytkownika).

5. **Błędy Keycloak** → `UserAdminService` łapie `NotFoundException` (JAX-RS) i rzuca `EntityNotFoundException` – backend `GlobalExceptionHandler` mapuje to na HTTP 404. `ClientErrorException` (409) → `IllegalArgumentException` → HTTP 400. Dzięki temu frontend otrzymuje spójny format błędów (`ApiErrorResponse`).

### Backend – warstwa serwisowa (UserAdminService)

`UserAdminService` jest jedynym punktem wejścia do operacji Keycloak. Zawiera:

- **Lifecycle użytkownika**: `createUser`, `resetPassword`, `setEnabled`, `updateUser`, `deleteUser`
- **Role**: `getRealmRoles`, `getUserRoles`, `assignRole`, `removeRole`, `createRole`
- **Grupy**: `getGroups`, `getUserGroups`, `createGroup`, `assignGroup`, `removeGroup`

**Realm**: stała `bandhub-realm` – wszystkie operacje dotyczą tego realmu Keycloak.

**Mapowanie błędów** – każda operacja Keycloak może rzucić:
- `NotFoundException` (JAX-RS) → `EntityNotFoundException` → HTTP 404 (np. użytkownik nie istnieje)
- `ClientErrorException` z statusem 409 → `IllegalArgumentException` → HTTP 400 (np. „Użytkownik o podanej nazwie już istnieje”)

### Backend – warstwa API (kontrolery)

- **UserAdminController** – `GET/POST /api/admin/users`, `GET/PUT/POST/DELETE /api/admin/users/{id}/...`, `GET/POST/DELETE /api/admin/users/{id}/roles`, `GET/POST/DELETE /api/admin/users/{id}/groups`
- **RoleAdminController** – `GET/POST /api/admin/roles`
- **GroupAdminController** – `GET/POST /api/admin/groups`

Wszystkie kontrolery mają `@PreAuthorize("hasRole('ADMIN')")` na poziomie klasy. Cienkie kontrolery – delegują do serwisu, mapują DTO.

### Bezpieczeństwo (na obronę)

**Backend**: `SecurityConfig` – `JwtAuthenticationConverter` wyciąga `realm_access.roles` z JWT i mapuje na `GrantedAuthority` z prefiksem `ROLE_` (np. `admin` → `ROLE_admin`. `hasRole('ADMIN')` sprawdza `ROLE_ADMIN`). Endpointy `/api/admin/**` wymagają `authenticated()` – `@PreAuthorize` dodatkowo wymaga `ADMIN`.

**Frontend**: `adminGuard` – przed wejściem na trasę `/admin/users`, `/admin/roles`, `/admin/groups` sprawdza `getPayloadFromAccessToken()` i szuka w `realm_access.roles` wartości `ADMIN` lub `ROLE_ADMIN`. Jeśli brak – przekierowanie na dashboard. Sekcja IAM w sidebarze (`AdminLayoutComponent`) jest widoczna tylko gdy `isAdmin()` jest true – odczyt z tokena.

### Frontend – moduł IAM

- **UserService** – serwis Angular z metodami HTTP do wszystkich endpointów IAM
- **UserListComponent** – tabela użytkowników, przyciski Szczegóły/Usuń
- **UserAddComponent** – formularz tworzenia (login, hasło, imię, nazwisko, email, aktywność)
- **UserDetailComponent** – szczegóły użytkownika, edycja danych, reset hasła, aktywacja/dezaktywacja, sekcje Role i Grupy z selectami do przypisywania i odbierania
- **RoleListComponent**, **RoleAddComponent** – lista ról i formularz tworzenia
- **GroupListComponent**, **GroupAddComponent** – lista grup i formularz tworzenia

Routing: `/admin/users`, `/admin/users/new`, `/admin/users/:id`, `/admin/roles`, `/admin/roles/new`, `/admin/groups`, `/admin/groups/new`. Trasy users/roles/groups mają `canActivate: [adminGuard]`.

### Zrealizowane (podsumowanie)

- Audyt modułu users, identyfikacja luk.
- Backend: pełny lifecycle użytkownika, role, grupy, mapowanie błędów Keycloak.
- Frontend: listy, formularze, szczegóły, przypisywanie ról i grup.
- Bezpieczeństwo: `@PreAuthorize`, `adminGuard`, warunkowe menu IAM.

### Decyzje techniczne

- Keycloak Admin Client (`org.keycloak:keycloak-admin-client`) – bean `Keycloak` z `KeycloakConfig` (`infrastructure.keycloak`) – łączy się z Keycloak (localhost:8081, realm `master`, client `admin-cli`) w celu uzyskania tokena do wywołań Admin API. Operacje na użytkownikach wykonywane są w realmie `bandhub-realm`.
- DTO jako records – `CreateUserRequest`, `UserResponse` itd.
- Brak encji JPA – użytkownicy w Keycloak.

### Ryzyka / otwarte tematy

- Brak paginacji na listach users/roles/groups.
- Brak edycji/usuwania ról i grup (tylko tworzenie i przypisywanie do użytkowników).

### Wpływ na wymagania projektu

- Domknięcie wymogu zarządzania użytkownikami.
- Pełny lifecycle użytkownika i zarządzanie uprawnieniami z panelu admina.
- Zgodność z architekturą: cienkie kontrolery, logika w serwisach, DTO jako records.

---

## 15. Sprint 12 - E-commerce Lean pod Mobile

Status: `Done`  
Cel: Domknięcie E-commerce tak, aby backend i panel admin były gotowe do późniejszej integracji z aplikacją mobilną fana, przy minimalnej złożoności (jeden magazyn, proste statusy, brak checkoutu po stronie web-admin).

### Zrealizowane

- **Public API produktów** – `ProductPublicController` i `ProductPublicService`: `GET /api/public/products`, `GET /api/public/products/page`, `GET /api/public/products/{id}`. Kontrakt pod przyszłą aplikację mobilną.
- **Checkout rozszerzony** – `PlaceOrderCommand` rozszerzony o `deliveryAddress` i `paymentProvider`. W jednej transakcji: zamówienie + redukcja stocku + utworzenie `Payment` (PENDING) i `Shipment` (PENDING) z adresem dostawy.
- **Migracja V11** – kolumna `delivery_address` w tabeli `shipments`.
- **Cykl statusów zamówienia** – enum `OrderStatus`: NEW, PAID, SHIPPED, DELIVERED, CANCELLED. Dozwolone przejścia: NEW→PAID/CANCELLED, PAID→SHIPPED/CANCELLED, SHIPPED→DELIVERED.
- **Automatyczna historia statusów** – przy każdej zmianie statusu zapis do `order_status_history` (kto, kiedy, stary/nowy status).
- **Kompensacja stocku przy anulacji** – `Product.restoreStock()` i wywołanie w `OrderAdminService.updateOrderStatus` przy przejściu na CANCELLED.
- **Admin UI zamówień** – filtr statusu, przyciski przejść zgodne z cyklem, widok szczegółów z sekcjami: płatność, wysyłka, historia statusów.
- **Raport merchu** – `GET /api/admin/reports/merch/sales-snapshot?from=&to=` zwraca `orderCount`, `totalRevenue`, `totalUnits`. Widok Angular „Raport merchu” z filtrem dat.

### Decyzje techniczne

- **Lean magazyn** – brak osobnej tabeli ruchów magazynowych; stan na produkcie + walidacja przy checkout + `restoreStock` przy anulacji.
- **Brak checkoutu w web-admin** – zamówienia składa wyłącznie fan przez API (docelowo aplikacja mobilna).
- **DTO** – `ProductResponse` współdzielony między admin i public; `OrderDetailsResponse` rozszerzony o `payment`, `shipment`, `statusHistory`.

### Ryzyka / otwarte tematy

- Brak paginacji po stronie DB w `MerchReportService` – przy dużej liczbie zamówień filtrowanie in-memory może być wolne.
- Frontend: formularze add/edit dla payments/shipments nadal prowadzą do placeholdera (nie w scope Sprintu 12).

### Wpływ na wymagania projektu

- Domknięcie procesu E-commerce end-to-end (od konfiguracji produktów do raportu sprzedaży).
- Gotowość kontraktów API pod przyszłą aplikację mobilną fana.
- Zgodność z architekturą DDD + Ports&Adapters.

### Kontrakty API pod aplikację mobilną (fan)

| Endpoint | Metoda | Opis |
|----------|--------|------|
| `/api/public/products` | GET | Lista wszystkich produktów |
| `/api/public/products/page` | GET | Lista paginowana (page, size, sortBy, sortDir, q) |
| `/api/public/products/{id}` | GET | Szczegóły produktu |
| `/api/public/orders` | POST | Złożenie zamówienia (PlaceOrderCommand: items, deliveryAddress?, paymentProvider?) |

Odpowiedzi używają `ProductResponse` i `PageResponse<T>`. Błędy: 400 (walidacja), 404 (nie znaleziono), 409 (np. brak stocku).

### Szczegółowe flow E2E (dla implementacji mobilnej, obrony i testerów)

Poniższe opisy pozwalają zrozumieć pełny przebieg procesu e-commerce, zaimplementować aplikację mobilną, wytłumaczyć system na obronie oraz przeprowadzić testy manualne.

---

#### Flow 1: Fan kupuje merch (scenariusz sukcesu)

**Aktor:** Fan (aplikacja mobilna / przyszły klient API)

| Krok | Akcja | Endpoint / Źródło | Oczekiwany rezultat |
|------|-------|-------------------|---------------------|
| 1 | Fan otwiera katalog produktów | `GET /api/public/products/page?page=0&size=10&sortBy=name&sortDir=asc&q=` | 200 OK, `PageResponse<ProductResponse>` z listą produktów |
| 2 | Fan wybiera produkt i otwiera szczegóły | `GET /api/public/products/{id}` | 200 OK, `ProductResponse` (nazwa, opis, cena, stockQuantity, kategoria) |
| 3 | Fan dodaje produkty do koszyka (po stronie klienta) | — | Koszyk trzymany lokalnie w aplikacji |
| 4 | Fan przechodzi do checkoutu, podaje adres dostawy i wybiera metodę płatności | — | Formularz w aplikacji |
| 5 | Fan wysyła zamówienie | `POST /api/public/orders` z `PlaceOrderCommand` | 201 Created, nagłówek `Location: /api/public/orders/{orderId}` |

**Uwaga:** Endpoint `/api/public/orders` jest pod `permitAll`, ale `userId` zamówienia pochodzi z `Authentication.getName()` (JWT). Aplikacja mobilna powinna wysyłać request z nagłówkiem `Authorization: Bearer <token>` po zalogowaniu fana (Keycloak). Bez tokena zamówienie może być zapisane z `userId = "anonymousUser"`.

**Przykładowy request checkout (POST /api/public/orders):**

```json
{
  "items": {
    "550e8400-e29b-41d4-a716-446655440001": 2,
    "550e8400-e29b-41d4-a716-446655440002": 1
  },
  "deliveryAddress": "ul. Koncertowa 15, 00-001 Warszawa",
  "paymentProvider": "stripe"
}
```

- `items`: mapa `productId → quantity` (UUID produktu jako klucz string)
- `deliveryAddress`: opcjonalny, max 500 znaków
- `paymentProvider`: opcjonalny (np. "stripe", "bank_transfer"); domyślnie "pending"

**Co dzieje się w backendzie przy checkout (w jednej transakcji):**

1. Walidacja: każdy produkt musi istnieć, quantity > 0
2. Dla każdego produktu: `product.reduceStock(quantity)` – rzuca wyjątek, jeśli brak stocku
3. Utworzenie zamówienia (`Order`) ze statusem NEW
4. Utworzenie rekordu `Payment` (status PENDING, amount = suma zamówienia)
5. Utworzenie rekordu `Shipment` (status PENDING, deliveryAddress z requestu)
6. Zapis do bazy – wszystko w jednej transakcji (rollback przy błędzie)

---

#### Flow 2: Admin obsługuje zamówienie (od listy do dostarczenia)

**Aktor:** Admin (panel web Angular)

| Krok | Akcja | Gdzie | Oczekiwany rezultat |
|------|-------|-------|---------------------|
| 1 | Admin loguje się i wchodzi w Zamówienia | `/admin/orders` | Lista zamówień z paginacją, sortowaniem, filtrem statusu |
| 2 | Admin filtruje po statusie (np. "Nowe") | Filtr statusu | Tylko zamówienia w statusie NEW |
| 3 | Admin klika "Podgląd" przy zamówieniu | `/admin/orders/{id}` | Widok szczegółów: pozycje, suma, płatność (PENDING), wysyłka (PENDING, adres), historia statusów |
| 4 | Admin potwierdza wpłatę i zmienia status na Opłacone | Przycisk "Opłacone" | PATCH `/api/admin/orders/{id}/status` z `{"newStatus":"PAID"}` → wpis w `order_status_history` |
| 5 | Admin pakuje i wysyła, zmienia status na Wysłane | Przycisk "Wysłano" | Status SHIPPED, wpis w historii |
| 6 | Admin uzupełnia dane wysyłki (opcjonalnie) | Lista Wysyłki → Edytuj | Carrier, numer śledzenia – osobny CRUD `shipments` |
| 7 | Po dostarczeniu admin zmienia status na Dostarczone | Przycisk "Dostarczono" | Status DELIVERED (terminalny) |

**Dozwolone przejścia statusów:**

```
NEW → PAID | CANCELLED
PAID → SHIPPED | CANCELLED
SHIPPED → DELIVERED
DELIVERED, CANCELLED → (brak – stany terminalne)
```

Próba niedozwolonego przejścia (np. NEW → SHIPPED) zwraca 400 z komunikatem `Invalid transition from NEW to SHIPPED`.

---

#### Flow 3: Anulowanie zamówienia i kompensacja stocku

**Aktor:** Admin

| Krok | Akcja | Rezultat |
|------|-------|----------|
| 1 | Zamówienie w statusie NEW lub PAID | Przycisk "Anuluj" dostępny |
| 2 | Admin klika "Anuluj" i potwierdza | PATCH status → CANCELLED |
| 3 | Backend przed zapisem historii | Dla każdej pozycji zamówienia: `product.restoreStock(quantity)` |
| 4 | Stock produktów | Przywrócony do stanu sprzed zamówienia |
| 5 | Zamówienie | Status CANCELLED (terminalny), wpis w historii |

**Uwaga:** Anulowanie z statusu SHIPPED lub DELIVERED jest zablokowane (stany terminalne).

---

#### Flow 4: Błąd – brak stocku przy checkout

| Krok | Akcja | Rezultat |
|------|-------|----------|
| 1 | Fan wysyła zamówienie z ilością większą niż dostępny stock | `POST /api/public/orders` |
| 2 | Backend w `product.reduceStock(quantity)` | `IllegalStateException`: "Not enough stock for product: {id}" |
| 3 | Transakcja | Rollback – żadne dane nie są zapisane |
| 4 | Odpowiedź HTTP | 400 lub 500 (zależnie od mapowania w `GlobalExceptionHandler`) |

---

#### Scenariusze testowe dla manual testerów

**Scenariusz 1: Pełny flow zakupu (happy path)**

1. Upewnij się, że są produkty z stockQuantity > 0.
2. Wywołaj `GET /api/public/products/page` – sprawdź, że zwraca listę.
3. Wywołaj `GET /api/public/products/{id}` dla jednego produktu – sprawdź szczegóły.
4. Wywołaj `POST /api/public/orders` z poprawnym `items` (istniejące ID, quantity ≤ stock).
5. Sprawdź 201 Created i `Location`.
6. Zaloguj się jako admin, wejdź w Zamówienia – zamówienie na liście ze statusem NEW.
7. Otwórz szczegóły – sprawdź sekcje: Płatność (PENDING), Wysyłka (PENDING, adres), Historia (brak wpisów lub jeden).
8. Zmień status na PAID – sprawdź wpis w historii.
9. Zmień na SHIPPED, potem DELIVERED – sprawdź historię.
10. Wejdź w Raport merchu – sprawdź, że zamówienie i przychód są uwzględnione.

**Scenariusz 2: Brak stocku**

1. Ustaw produkt na stockQuantity = 2.
2. Wyślij zamówienie z quantity = 3 dla tego produktu.
3. Oczekuj błędu, brak nowego zamówienia w bazie.
4. Sprawdź, że stock produktu nadal = 2.

**Scenariusz 3: Anulowanie**

1. Złóż zamówienie (np. 2 szt. produktu X, stock był 10).
2. Jako admin zmień status na CANCELLED.
3. Sprawdź, że stock produktu X = 10 (przywrócone 2 szt.).

**Scenariusz 4: Niedozwolone przejście statusu**

1. Zamówienie w statusie NEW.
2. Wywołaj PATCH z `{"newStatus":"SHIPPED"}` (pomijając PAID).
3. Oczekuj błędu 400, status zamówienia bez zmian.

---

#### Diagram sekwencji: Checkout (fan → backend)

```
Fan (Mobile)          ProductPublicAPI       OrderPublicService       ProductRepository
     |                        |                        |                        |
     | GET /products/page     |                        |                        |
     |----------------------->|                        |                        |
     |<-----------------------|  PageResponse          |                        |
     |                        |                        |                        |
     | GET /products/{id}     |                        |                        |
     |----------------------->|                        |                        |
     |<-----------------------|  ProductResponse      |                        |
     |                        |                        |                        |
     | POST /orders           |                        |                        |
     | PlaceOrderCommand      |                        |                        |
     |----------------------->| placeOrder()           |                        |
     |                        |----------------------->| findById, reduceStock   |
     |                        |                        |----------------------->|
     |                        |                        |<-----------------------|
     |                        |                        | save Order, Payment,   |
     |                        |                        | Shipment               |
     |                        |<-----------------------|                        |
     | 201 Created            |                        |                        |
     | Location: /orders/{id} |                        |                        |
     |<-----------------------|                        |                        |
```

---

#### Diagram sekwencji: Zmiana statusu (admin)

```
Admin (Web)           OrderAdminController    OrderAdminService       OrderStatusHistoryRepo
     |                        |                        |                        |
     | PATCH /orders/{id}/status                      |                        |
     | {"newStatus":"PAID"}   |                        |                        |
     |----------------------->| updateOrderStatus()    |                        |
     |                        |----------------------->|                        |
     |                        |                        | (jeśli CANCELLED:       |
     |                        |                        |  restoreStock)         |
     |                        |                        | save(OrderStatusHistory)|
     |                        |                        |----------------------->|
     |                        |                        | order.changeStatus()   |
     |                        |<-----------------------|                        |
     | 204 No Content         |                        |                        |
     |<-----------------------|                        |                        |
```

---

## 16. Sprint 13 - Ticketing end-to-end

Status: `Done`  
Cel: Domknięcie procesu biletowego: publiczne API fana, zakup z rezerwacją puli i emisją kodów, walidacja wejścia, monitoring i raport w panelu admina.

### Zrealizowane

- **Public API koncertów** – `ConcertPublicController` / `ConcertPublicService`: `GET /api/public/concerts`, `/page`, `/{id}` (read-only, `permitAll` jak produkty).
- **Zakup biletu** – `POST /api/public/ticket-orders` z `PlaceTicketOrderCommand` (`concertId`, `items`: mapa `poolId → quantity`). W jednej transakcji: `TicketPool.reserve()`, zapis `TicketOrder` (status `PAID`), pozycje `TicketOrderItem`, wiersze `tickets` + `ticket_codes` z unikalnym kodem (`BH-` + losowy sufiks).
- **Domena** – `TicketPool` z relacją `ManyToOne` do `Concert` i `mappedBy` po stronie `Concert`; metoda `reserve(int)`; encja JPA `Ticket` dla tabeli `tickets`; `TicketRepository` + adapter SQL.
- **Migracja V12** – kolumna `ticket_order_id` w `tickets` (FK do `ticket_orders`) pod listę uczestników i eksport.
- **Walidacja biletu** – `POST /api/admin/ticketing/scan` (`ScanTicketRequest` / `ScanTicketResponse`): odczyt po `code_value`, dla `ACTIVE` → `USED` + zapis `ticket_validations`; odrzucenie `USED` / `CANCELLED` / nieznany kod.
- **Raportowanie admin** – `GET /api/admin/concerts/{id}/ticketing/summary`, `/attendees`, `/attendees/export` (CSV); `TicketingReportingService` (JdbcTemplate + agregacje SQL); `GET /api/admin/reports/ticketing/event-summary?concertId=` (`TicketingEventSnapshotResponse`).
- **Lista zamówień biletów** – opcjonalny filtr `concertId` na `GET /api/admin/ticket-orders/page`.
- **Frontend (zsi-admin-web)** – szczegóły koncertu: KPI sprzedaży, tabela per pula, lista uczestników, przycisk eksportu CSV; filtr koncertu na liście zamówień biletów; widok „Raport wydarzenia (bilety)”; widok „Skan biletu”; serwisy HTTP zgodne z nowymi endpointami.
- **Obsługa błędów** – `IllegalStateException` → HTTP 409 (np. brak miejsc w puli, konflikt stanu).
- **Testy** – `TicketPoolReserveTest` (JUnit): rezerwacja i przekroczenie puli.

### Przykładowe przepływy E2E (na obronę)

Poniższe scenariusze można powiązać bezpośrednio z endpointami i klasami w kodzie.

#### Flow 1: Fan – przegląd koncertów i zakup biletu (happy path)

| Krok | Aktor | Akcja | Endpoint / klasa | Oczekiwany efekt |
|------|--------|--------|-------------------|------------------|
| 1 | Fan (klient HTTP / przyszła aplikacja mobilna) | Lista koncertów lub stronicowanie | `GET /api/public/concerts` lub `/page` → `ConcertPublicController` → `ConcertPublicService` → delegacja do `ConcertAdminService` | 200 OK, lista z datą, miejscem, identyfikatorem |
| 2 | Fan | Szczegóły koncertu z pulami | `GET /api/public/concerts/{id}` | 200 OK, `TicketPoolResponse` z `remainingQuantity` / `totalQuantity` (dostępność bez logowania admina) |
| 3 | Fan | Składanie zamówienia (wybrana pula + ilość) | `POST /api/public/ticket-orders`, body: `PlaceTicketOrderCommand` | 201 Created, nagłówek `Location`, body `TicketPurchaseResponse` (`orderId`, lista `ticketCodes`) |
| 4 | Backend (w jednej transakcji `@Transactional`) | Weryfikacja pul w ramach koncertu | `TicketOrderPublicService.purchase()` – dopasowanie `poolId` do `concert.getTicketPools()` | Odrzucenie pul „obcych” dla koncertu (`IllegalArgumentException` → 400) |
| 5 | Domena | Rezerwacja miejsc | `TicketPool.reserve(quantity)` | Zmniejszenie `remaining_quantity`; przy braku miejsc `IllegalStateException` → **409** (`GlobalExceptionHandler`) |
| 6 | Persystencja | Zapis zamówienia i biletów | `TicketOrderRepository`, `TicketOrderItemRepository`, `TicketRepository`, `TicketCodeRepository` | Wiersz `ticket_orders` (status `PAID`), pozycje `ticket_order_items`, dla każdej sztuki: `tickets` + `ticket_codes` z tym samym kodem skanowym |
| 7 | Tożsamość | Powiązanie z użytkownikiem | `TicketOrderPublicController` przekazuje `Authentication.getName()` | Jak w merchu (Sprint 12): z JWT – `sub`/login; bez tokena – `anonymousUser` (świadoma decyzja na MVP) |

**Przykładowe body zakupu (`POST /api/public/ticket-orders`):**

```json
{
  "concertId": "550e8400-e29b-41d4-a716-446655440000",
  "items": {
    "660e8400-e29b-41d4-a716-446655440001": 2,
    "660e8400-e29b-41d4-a716-446655440002": 1
  }
}
```

- `items`: mapa `UUID puli → liczba biletów` (ten sam wzorzec co `PlaceOrderCommand.items` dla produktów).

#### Flow 2: Admin – monitoring, lista uczestników, eksport CSV

| Krok | Aktor | Akcja | Endpoint / komponent UI | Oczekiwany efekt |
|------|--------|--------|-------------------------|------------------|
| 1 | Admin | Wejście w szczegóły koncertu | Panel: `concert-detail.component.ts` | Równolegle: `GET /api/admin/concerts/{id}`, `GET .../ticketing/summary`, `GET .../ticketing/attendees` |
| 2 | Backend | Agregacja sprzedaży per pula | `TicketingReportingService.concertSummary()` – zapytanie SQL (`JdbcTemplate`) | DTO `ConcertTicketingSummaryResponse` – sprzedane, przychód, `remaining` z tabeli `ticket_pools` + sumy z `ticket_order_items` |
| 3 | Admin | Filtrowanie zamówień po koncercie | `GET /api/admin/ticket-orders/page?concertId=` + lista w `ticket-order-list.component.ts` | Tylko zamówienia dla wskazanego koncertu (`TicketOrderRepository` / JPQL z `t.concert.id`) |
| 4 | Admin | Pobranie listy wejściowej | `GET .../ticketing/attendees/export` → plik CSV | `ConcertTicketingAdminController` – `text/csv`, dane z `tickets` + join do `ticket_pools` |

#### Flow 3: Admin – walidacja biletu przy wejściu (skan)

| Krok | Aktor | Akcja | Endpoint / klasa | Oczekiwany efekt |
|------|--------|--------|------------------|------------------|
| 1 | Admin (operator bramki) | Wpisuje / skanuje kod | `POST /api/admin/ticketing/scan`, `ScanTicketRequest` | `TicketScanAdminController` → `TicketScanAdminService` (rola `ADMIN` – jak reszta panelu) |
| 2 | Backend | Wyszukanie kodu | `TicketCodeRepository.findByCodeValue` | Brak kodu → odpowiedź `valid: false`, `result: UNKNOWN` (bez mutacji) |
| 3 | Reguły | Status `CANCELLED` / `USED` | Odczyt `TicketCode.status` | Odpowiedź odrzucona + zapis `TicketValidation` z `validationResult = DENIED` (ślad audytowy) |
| 4 | Domena | Akceptacja wejścia | `TicketCode.markUsed()` | Tylko dla `ACTIVE`: ustawienie `USED`, `used_at`; zapis `TicketValidation` `SUCCESS` |
| 5 | Kontekst dla operatora | Nazwa koncertu / puli | `TicketRepository.findById(ticketId)` → `TicketPool` → `Concert` | Pola `concertName`, `poolName` w `ScanTicketResponse` (lazy load w aktywnej transakcji) |

#### Flow 4: Admin – raport finansowy wydarzenia (jak merch)

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Admin | Wybór koncertu i odświeżenie | `GET /api/admin/reports/ticketing/event-summary?concertId=` + `ticketing-event-report.component.ts` | `TicketingEventSnapshotResponse`: sprzedane, pozostałe w pulach, przychód, pojemność sali, `occupancyPercent` |
| 2 | Backend | Ponowne użycie logiki podsumowania | `TicketingReportingService.eventSnapshot()` wywołuje `concertSummary()` | Jedno źródło prawdy dla KPI – mniej rozjazdów między ekranami |

#### Diagram (uproszczony): zakup → zapis w bazie

```
Fan                    TicketOrderPublicController    TicketOrderPublicService          Domena / DB
 |                              |                              |                              |
 | POST /ticket-orders          |                              |                              |
 |----------------------------->| placeOrder(cmd, userId)      |                              |
 |                              |----------------------------->| find Concert + pools         |
 |                              |                              | pool.reserve(qty)            |
 |                              |                              | save Order, Items            |
 |                              |                              | for each ticket: Ticket +    |
 |                              |                              |   TicketCode (same code)     |
 | 201 + TicketPurchaseResponse |<-----------------------------|                              |
 |<-----------------------------|                              |                              |
```

### Decyzje techniczne (szczegółowo – uzasadnienie klas na obronie)

Poniżej: **dlaczego ta klasa / ten wzorzec**, a nie inny – zgodnie z `architecture-and-patterns.md` (DDD, ports & adapters, cienkie kontrolery, DTO jako rekordy).

#### 1. Dwa „publiczne” kontrolery koncertów vs jeden serwis read-only

- **`ConcertPublicController` + `ConcertPublicService`** zamiast wystawiania `ConcertAdminController` pod `/api/public`:
  - **Powód:** rozdzielenie **ścieżki fana** (`permitAll`, brak roli `ADMIN`) od **panelu** (`@PreAuthorize` na adminie). Na obronie: *„Nie chcę, żeby publiczny klient musiał udawać admina ani żeby przypadkiem wycieknąły endpointy zapisu.”*
- **`ConcertPublicService` deleguje do `ConcertAdminService`:**
  - **Powód:** jeden zestaw mapowań `Concert` → `ConcertResponse` / `ConcertDetailResponse` – brak duplikacji zapytań i reguł sortowania/filtrowania listy. *„Single source of truth dla read-modelu koncertu.”*

#### 2. `TicketOrderPublicService` zamiast rozszerzenia `TicketOrderAdminService`

- **Osobny serwis aplikacyjny** pod zakup:
  - **Powód:** adminowy `TicketOrderAdminService` obsługuje **ręczny CRUD** (np. pod formularze z ID zamówienia z zewnątrz), a proces fana to **use case „checkout”**: rezerwacja puli + spójny zapis wielu tabel w jednej transakcji. Mieszanie obu roli w jednej klasie rozmyłoby granicę use case’u i utrudniłoby testowanie.
- **`PlaceTicketOrderCommand` jako rekord:**
  - **Powód:** spójność z `PlaceOrderCommand` w e-commerce; walidacja deklaratywna (`@NotEmpty`, `@Min` na mapie pozycji).

#### 3. `TicketPool.reserve(int)` w encji, a nie `setRemainingQuantity` w serwisie

- **Logika w domenie:**
  - **Powód:** zasada z dokumentu architektury – *stan puli zmieniamy metodą biznesową*, która może rzucić wyjątek przy naruszeniu reguł. Na obronie: *„Serwis tylko orkiestruje; reguła ‚nie sprzedaj więcej niż remaining’ żyje przy agregacie (puli).”*

#### 4. Relacja `TicketPool` ↔ `Concert` (`mappedBy` + `ManyToOne`)

- **Zmiana z samego `@JoinColumn` po stronie `Concert` na dwukierunkową relację z `mappedBy`:**
  - **Powód:** potrzeba nawigacji **Pula → Koncert** przy skanowaniu (`Ticket` → `TicketPool` → `Concert`) bez dodatkowych zapytań SQL w kodzie walidacji. *„To jest typowy koszt w JPA za wygodny model obiektowy.”*

#### 5. Dwie tabele: `tickets` (V1) i `ticket_codes` (V9) – oraz encja `Ticket`

- **Istniejący schemat** wymaga FK z `ticket_codes.ticket_id` do `tickets.id`.
- **Encja `Ticket` + `TicketRepository`:**
  - **Powód:** spełnienie integralności referencyjnej i jednoznaczne powiązanie **kodu skanowego** z **pulą, użytkownikiem, zamówieniem** (`ticket_order_id` z V12). *„Nie wprowadzaliśmy drugiego modelu ‚biletu’ w innym miejscu – użyliśmy tabeli, która już była w migracjach.”*
- **Ta sama wartość w `tickets.ticket_code` i `ticket_codes.code_value`:**
  - **Powód:** minimum effort – jeden ciąg do wyświetlenia w aplikacji mobilnej i do skanera; `ticket_codes` niesie status (`ACTIVE`/`USED`) pod walidację.

#### 6. `TicketCodeRepository.findByCodeValue` / `existsByCodeValue`

- **Powód:** walidacja i generator kodów muszą sprawdzać unikalność po wartości – indeks `UNIQUE` na `code_value` jest w SQL; repozytorium odzwierciedla ten dostęp. *„Port wtórny – interfejs w module, implementacja w `infrastructure`.”*

#### 7. `TicketScanAdminService` zamiast tylko CRUD `TicketValidationAdminService`

- **Dedykowany use case `POST .../scan`:**
  - **Powód:** operator potrzebuje **jednej operacji** (wejście / odmowa + komunikat), a nie sekwencji „znajdź kod, PATCH statusu, POST walidacji”. Mniej błędów po stronie UI i czytelny przepływ na obronie.

#### 8. `TicketingReportingService` + `JdbcTemplate` zamiast wyłącznie JPQL

- **Powód:** agregacje `SUM(quantity)`, `SUM(quantity * unit_price)` z joinami po wielu tabelach są w SQL proste i czytelne; unikamy rozbudowanych `@Query` na encjach, które nie są agregatami raportowymi. Zgodnie z podejściem **lean**: read-model raportowy nie musi być pełnym obiektem domenowym.
- **Osobne endpointy pod `/concerts/{id}/ticketing/...` i `/reports/ticketing/...`:**
  - **Powód:** pierwsze grupują zasoby **pod kontekstem koncertu** (szczegóły w panelu); drugi – **raportowanie** (jak `MerchReportController`), spójne z menu „Raportowanie”.

#### 9. `IllegalStateException` → HTTP 409 w `GlobalExceptionHandler`

- **Powód:** konflikt zasobu (np. za mało biletów w puli, ponowne `markUsed`) to semantycznie **konflikt stanu**, a nie „zły request” (400). Na obronie można porównać z typowym **409 Conflict** przy nadmiernej sprzedaży.

#### 10. Migracja V12 – `ticket_order_id` na `tickets`

- **Powód:** lista uczestników i CSV opierają się na powiązaniu **wydanego biletu z zamówieniem** bez zgadywania przez same kody. Uzupełnia model bez ruszania istniejących tabel zamówień z V9.

#### 11. Frontend: `forkJoin` w `concert-detail`, filtr `concertId` na liście zamówień

- **Powód:** ten sam wzorzec co w innych ekranach admina – równoległe GET-y dla niezależnych read-modeli; filtr jako opcjonalny parametr query zgodny z backendem.

### Decyzje techniczne (skrót)

- Spójność z Sprintem 12: publiczne POST z `Authentication.getName()` dla `userId` (możliwy `anonymousUser` bez JWT).
- Kody w `tickets.ticket_code` i `ticket_codes.code_value` ustawiane na tę samą wartość (minimum effort, jeden kod do skanera).
- Agregacje sprzedaży w SQL (LEFT JOIN pul z sumami z `ticket_order_items`), wykluczenie zamówień ze statusem `CANCELLED` (na przyszłość).

### Ryzyka / otwarte tematy

- Brak twardego blokowania współbieżnych zakupów na puli (wyścigi – akceptowalne w wersji studenckiej).
- Zwrócone JSON-y z `BigDecimal` mapują się na number w Angularze – dla bardzo dużych kwot ewentualna utrata precyzji po stronie UI.

### Wplyw na wymagania projektu

- Domknięcie procesu ticketingu end-to-end pod dokumentację obrony i przyszłą aplikację mobilną fana.
- Raport finansowy wydarzenia i lista uczestników z eksportem CSV.

---

## 17. Sprint 14 - Logistyka trasy (domknięcie lean)

Status: `Done`  
Cel: domknięcie sprintu 14 przy minimalnym rozroście domeny: koszty i przychody pozostają na poziomie trasy, z opcjonalną kategorią (słownik) i opcjonalnym powiązaniem z etapem (`tour_legs`), walidacje dat i budżetu etapu, automatyczne zamknięcie rozliczenia z danych zgodnych z widokiem/funkcją SQL, spójny panel admina.

### Zrealizowane

- **Migracja Flyway** – [backend/src/main/resources/db/migration/V13__Logistics_Cost_Revenue_Categories_Legs.sql](backend/src/main/resources/db/migration/V13__Logistics_Cost_Revenue_Categories_Legs.sql): kolumny `cost_category_id`, `tour_leg_id` na `tour_costs`; `revenue_category_id`, `tour_leg_id` na `tour_revenues` (FK do słowników z V10 i `tour_legs`).
- **Encje** – [TourCost.java](backend/src/main/java/com/bandhub/zsi/logistics/domain/TourCost.java), [TourRevenue.java](backend/src/main/java/com/bandhub/zsi/logistics/domain/TourRevenue.java): relacje `ManyToOne` do `TourCostCategory` / `TourRevenueCategory` oraz opcjonalnie do `TourLeg`; fabryka/aktualizacja przez metody z parametrami kategorii i etapu (bez publicznych setterów poza aktualizacją wewnętrzną).
- **DTO** – rozszerzone `CreateCostRequest` / `UpdateCostRequest` / `CreateRevenueRequest` / `UpdateRevenueRequest` o opcjonalne UUID kategorii i etapu; `TourCostResponse` / `TourRevenueResponse` zwracają `id`/`nazwę` kategorii i `tourLegId` dla UI.
- **Logika aplikacyjna** – [LogisticsAdminService.java](backend/src/main/java/com/bandhub/zsi/logistics/LogisticsAdminService.java):
  - walidacja zakresu dat trasy przy tworzeniu/aktualizacji trasy;
  - data kosztu/przychodu musi mieścić się w `[start_date, end_date]` trasy (gdy te daty są ustawione);
  - kategoria musi istnieć i być `active`;
  - etap musi pasować do `tour_id` (`TourLegRepository.findByIdAndTour_Id`);
  - **budżet etapu**: jeśli koszt ma przypisany etap z `planned_budget` i ta sama waluta co koszt, suma kosztów na tym etapie nie może przekroczyć budżetu → `IllegalStateException` → **409** (`GlobalExceptionHandler`);
  - odczyt szczegółów trasy przez `TourRepository.findWithDetailsById`: **dwa** zapytania JPQL z `JOIN FETCH` — najpierw `costs` + kategoria + etap, potem `revenues` + kategoria + etap (ten sam `Tour` w persistence context); unikamy **`MultipleBagFetchException`** (dwa `@OneToMany` w jednym fetch) oraz `LazyInitializationException` przy mapowaniu DTO.
- **Przychód z biletów vs SQL** – [SqlTourRepository.java](backend/src/main/java/com/bandhub/zsi/infrastructure/SqlTourRepository.java): `sumTicketSalesRevenue` liczy `SUM(ticket_orders.total_amount)` po koncertach trasy, z wykluczeniem `status = 'CANCELLED'` (zgodnie z ideą `vw_tour_profitability` / `fn_close_tour_settlement`).
- **Rozliczenie** – [TourSettlementAdminService.java](backend/src/main/java/com/bandhub/zsi/logistics/TourSettlementAdminService.java): `closeFromComputedData` wywołuje `fn_close_tour_settlement(tour_id, settled_by)` przez `JdbcTemplate`, opcjonalnie dopisuje `notes` przez `TourSettlement.mergeNotes` i zapis; [TourSettlementRepository](backend/src/main/java/com/bandhub/zsi/logistics/TourSettlementRepository.java) + JPA: `findByTour_Id`.
- **API admin** – [LogisticsAdminController.java](backend/src/main/java/com/bandhub/zsi/logistics/LogisticsAdminController.java):
  - `GET /api/admin/logistics/tours/{tourId}/settlement` → 404 jeśli brak wiersza;
  - `POST /api/admin/logistics/tours/{tourId}/settlement/close` + body [CloseTourSettlementRequest](backend/src/main/java/com/bandhub/zsi/logistics/dto/CloseTourSettlementRequest.java) (opcjonalne `notes`); do `settled_by` trafia czytelna etykieta z JWT (`preferred_username` / `name`) przez [AuthenticationDisplayName](backend/src/main/java/com/bandhub/zsi/shared/security/AuthenticationDisplayName.java), z fallbackiem do `getName()`.
  - `TourSettlementResponse` zawiera **`tourName`** (nazwa trasy dla list i szczegółów rozliczenia).
- **Etap trasy** – [TourLegAdminService.java](backend/src/main/java/com/bandhub/zsi/logistics/TourLegAdminService.java): `planned_budget >= 0`, data etapu w zakresie dat trasy.
- **Frontend** – [logistics.service.ts](zsi-admin-web/src/app/core/services/logistics.service.ts): modele pól kategorii/etapu, `getSettlementForTour`, `closeSettlementForTour`; [tour-detail.component.ts](zsi-admin-web/src/app/features/logistics/tour-detail.component.ts): kolumny tabel, selecty kategorii i odcinków, blok rozliczenia; [tour-leg-form.component.ts](zsi-admin-web/src/app/features/logistics/tour-legs/tour-leg-form.component.ts); [tour-settlement-close.component.ts](zsi-admin-web/src/app/features/logistics/tour-settlements/tour-settlement-close.component.ts); [tour-settlement-detail.component.ts](zsi-admin-web/src/app/features/logistics/tour-settlements/tour-settlement-detail.component.ts); [app.routes.ts](zsi-admin-web/src/app/app.routes.ts) – usunięcie placeholderów dla `tour-legs` i `tour-settlements`.
- **Testy** – [LogisticsAdminServiceTest.java](backend/src/test/java/com/bandhub/zsi/logistics/LogisticsAdminServiceTest.java), [TourLegAdminServiceTest.java](backend/src/test/java/com/bandhub/zsi/logistics/TourLegAdminServiceTest.java) (JUnit + Mockito).
- **Aplikacja mobilna / fan** – bez zmian w zakresie logistyki; podgląd koncertów nadal przez istniejące [ConcertPublicController](backend/src/main/java/com/bandhub/zsi/ticketing/ConcertPublicController.java) (`/api/public/concerts`).

### Przykładowe przepływy E2E (obrona)

#### Flow 1: Admin – koszt z kategorią i etapem (happy path)

| Krok | Aktor | Akcja | Endpoint / komponent | Oczekiwany efekt |
|------|--------|--------|----------------------|------------------|
| 1 | Admin | Tworzy kategorie kosztów (jeśli brak) | Panel `/admin/tour-cost-categories` | Aktywny słownik `tour_cost_categories` |
| 2 | Admin | Dodaje etap trasy w ramach trasy | `/admin/tour-legs/new?tourId=...` → `TourLegFormComponent` | Wiersz `tour_legs` z `tour_id`, `planned_budget` |
| 3 | Admin | Otwiera panel trasy | `/admin/logistics/:id` → `TourDetailComponent` | `GET /api/admin/logistics/tours/{id}` z kosztami/rozbiciem |
| 4 | Admin | Dodaje koszt: kategoria + opcjonalnie odcinek | `POST .../tours/{id}/costs` body z `costCategoryId`, `tourLegId` | Wiersz `tour_costs` z FK; przy przekroczeniu budżetu etapu → **409** |

#### Flow 2: Admin – zamknięcie rozliczenia z bazy

| Krok | Aktor | Akcja | Endpoint / klasa | Oczekiwany efekt |
|------|--------|--------|------------------|------------------|
| 1 | Admin | Z panelu trasy lub z `/admin/tour-settlements/new` | `POST /api/admin/logistics/tours/{tourId}/settlement/close` | Wywołanie `fn_close_tour_settlement`; `tour_settlements` UPSERT po `tour_id` |
| 2 | Backend | Tożsamość | `AuthenticationDisplayName.resolve()` → `settled_by` w SQL | Login / nazwa z tokena zamiast samego `sub` |
| 3 | Admin | Podgląd | `GET .../tours/{tourId}/settlement` lub lista rozliczeń | Zgodność kwot z logiką SQL (koszty + przychody ręczne + bilety) |

#### Flow 3: Fan – tylko koncerty (bez logistyki)

| Krok | Aktor | Akcja | Endpoint | Uzasadnienie |
|------|--------|--------|----------|--------------|
| 1 | Fan / klient mobilny (przyszłość) | Lista/szczegóły koncertów | `GET /api/public/concerts`, `/page`, `/{id}` | Minimum pod aplikację mobilną; logistyka wewnętrzna zespołu w panelu admina |

### Decyzje techniczne (klasy – dlaczego tak)

- **Dwa `JOIN FETCH` zamiast jednego `@EntityGraph` na oba worki**: Hibernate nie pozwala w jednym zapytaniu jednocześnie fetchować dwóch kolekcji typu „bag” (`costs`, `revenues`) — skutkowało to HTTP 500; rozdzielenie zapytań rozwiązuje problem i nadal ładuje kategorie oraz etapy dla obu typów wpisów.
- **Koszty nadal pod `Tour` (agregacja po `tour_id`)**: unikamy osobnej tabeli „koszty etapu” i duplikacji CRUD – etap jest atrybutem opcjonalnym (analityka / budżet odcinka).
- **`JdbcTemplate` dla `fn_close_tour_settlement`**: jedna ścieżka prawdy z migracją V10; brak duplikacji reguł agregacji w Javie przy zamykaniu rozliczenia.
- **Osobny `TourSettlementAdminService.closeFromComputedData`**: use case „zamknij z danych” vs ręczny CRUD w tym samym serwisie – na obronie: *„nie muszę przepisywać sum z kalkulatora”*.
- **Angular – `getSettlementForTour` z `catchError` na 404**: brak rozliczenia to normalny stan, nie błąd aplikacji.

### Ryzyka / otwarte tematy

- Wielowalutowe trasy: reguła budżetu etapu ignoruje koszty w innej walucie niż etap (świadome uproszczenie).
- Zamknięcie rozliczenia nadpisuje pola liczbowe wg funkcji SQL – ręczna edycja tych samych pól przez stary CRUD nadal możliwa z innych ekranów (świadome dla projektu studenckiego).

### Wplyw na wymagania projektu

- Wzmocnienie procesu „logistyka trasy”: typy kosztów (słownik), etapy, bilans, rozliczenie końcowe z bazy.
- Więcej uzasadnionych widoków admina (formularze odcinków i zamknięcia rozliczenia) bez rozbudowy modułu mobilnego poza istniejące publiczne koncerty.

---

## 18. Miejsce na kolejne podsumowania

Kolejne wpisy dodajemy sekcyjnie:

## Sprint [numer] - [Tytul]

Status: `Done/In Progress`  
Cel: [krotki opis]

### Zrealizowane

- ...

### Decyzje techniczne

- ...

### Ryzyka / otwarte tematy

- ...

### Wplyw na wymagania projektu

- ...

