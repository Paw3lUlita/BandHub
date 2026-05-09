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

## 18. Sprint 15 - Raporty biznesowe (PDF / Excel)

Status: `Done`  
Cel: spełnienie wymagania **raportów biznesowych z eksportem do PDF i Excel** (bare minimum): trzy raporty (merch, ticketing, rentowność trasy), jeden generator w panelu admina, spójność z istniejącą logiką domenową i audyt w tabelach `report_runs` / `export_jobs`.

### Zrealizowane

- **Zależności Maven** – [backend/pom.xml](backend/pom.xml): `com.github.librepdf:openpdf` (generacja PDF), `org.apache.poi:poi-ooxml` (arkusz `.xlsx`).
- **DTO API raportów** – [BusinessReportType.java](backend/src/main/java/com/bandhub/zsi/reporting/dto/BusinessReportType.java) (enum: `MERCH`, `TICKETING_EVENT`, `TOUR_PROFITABILITY`); [BusinessReportPreviewResponse.java](backend/src/main/java/com/bandhub/zsi/reporting/dto/BusinessReportPreviewResponse.java) (`reportType` + `payload` jako istniejące rekordy odpowiedzi modułów).
- **Renderer binarny (adapter infrastruktury)** – [ReportBinaryRenderer.java](backend/src/main/java/com/bandhub/zsi/reporting/infrastructure/ReportBinaryRenderer.java): klasa pakietowa `infrastructure`; dla każdego typu raportu metody `*Pdf` i `*Xlsx` budują dokument z tymi samymi metrykami co podgląd JSON (tabele PDF, arkusz z kolumnami „Metryka / Wartość”). Teksty w PDF bez polskich znaków diakrytycznych (Helvetica) – świadomy kompromis pod stabilność kodowania.
- **Orkiestracja** – [BusinessReportService.java](backend/src/main/java/com/bandhub/zsi/reporting/BusinessReportService.java):
  - `preview` / `previewWithAudit`: delegacja do `MerchReportService`, `TicketingReportingService.eventSnapshot`, `LogisticsAdminService.getProfitability` (brak duplikacji SQL/agregacji).
  - `exportWithAudit`: generacja `byte[]` + `ReportExportResult` (typ MIME, nazwa pliku).
  - **Audyt:** przy podglądzie zapis `ReportRun` ze statusem `COMPLETED` i `file_format = PREVIEW`; przy eksporcie najpierw `RUNNING`, po sukcesie `COMPLETED` + `ExportJob` (`module = reporting`, `file_path = inline://{filename}`), przy błędzie `FAILED` na `ReportRun`.
  - Parametry zapisu jako JSON (`ObjectMapper`) w `parameters_json`.
- **REST** – [BusinessReportsController.java](backend/src/main/java/com/bandhub/zsi/reporting/BusinessReportsController.java):
  - `GET /api/admin/reports/business/preview?type=...` (+ `from`/`to` lub `concertId` lub `tourId`) → JSON + rekord w `report_runs`.
  - `GET /api/admin/reports/business/export?type=...&format=pdf|xlsx` → `Content-Disposition: attachment`, `application/pdf` lub `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`.
  - Ochrona: `@PreAuthorize("hasRole('ADMIN')")` jak pozostałe raporty admina.
- **Frontend** – [business-report.service.ts](zsi-admin-web/src/app/core/services/business-report.service.ts); [report-generator.component.ts](zsi-admin-web/src/app/features/reporting/report-generator.component.ts); trasa `admin/reports/generator` w [app.routes.ts](zsi-admin-web/src/app/app.routes.ts); pozycja menu w [admin-layout.component.ts](zsi-admin-web/src/app/layout/admin-layout.component.ts). Podgląd przez `computed()` mapujące `payload` na karty (spójnie z `merch-report` / `ticketing-event-report`). Pobieranie plików: `HttpClient` + `responseType: 'blob'` + tymczasowy link `<a download>` (jak eksport CSV uczestników).

### Przykładowe przepływy E2E (obrona)

#### Flow 1: Merch – podgląd i Excel

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Admin | Loguje się do Keycloak | Panel Angular | JWT z rolą `ADMIN` |
| 2 | Admin | Otwiera generator | `/admin/reports/generator` | Widok wyboru typu raportu |
| 3 | Admin | Wybiera „Merch”, opcjonalnie zakres dat, **Podgląd** | `GET .../business/preview?type=MERCH&from=&to=` | JSON z `orderCount`, `totalRevenue`, … + wiersz w `report_runs` (`file_format=PREVIEW`) |
| 4 | Admin | **Pobierz Excel** | `GET .../business/export?type=MERCH&format=xlsx&...` | Plik `.xlsx` + `report_runs` (COMPLETED) + `export_jobs` (`inline://merch-sales.xlsx`) |

#### Flow 2: Ticketing – koncert i PDF

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Admin | Wybiera typ „Ticketing”, koncert z listy | `concertId` z `GET /api/admin/concerts` | Walidacja: bez `concertId` → **400** |
| 2 | Admin | Podgląd | `GET .../preview?type=TICKETING_EVENT&concertId=` | Ten sam model co `TicketingEventReportController` (sprzedaż, obłożenie) |
| 3 | Admin | Pobierz PDF | `GET .../export?format=pdf` | PDF z tabelą metryk; audyt jak wyżej |

#### Flow 3: Logistyka – rentowność trasy

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Admin | Wybiera „Rentowność trasy”, trasę z listy | `tourId` z `GET /api/admin/logistics/tours` | Spójność z `GET .../logistics/tours/{id}/profitability` |
| 2 | Admin | Eksport XLSX | `type=TOUR_PROFITABILITY&format=xlsx` | Arkusz: koszty, przychody z biletów, ręczne, bilans |

### Decyzje techniczne (klasy – dlaczego tak)

- **Osobny kontroler pod `/reports/business` zamiast rozszerzania `MerchReportController` eksportem plików:** cienkie kontrolery domenowe zostają przy JSON; generator i audyt w jednym miejscu modułu `reporting` – na obronie: *„Jedna ścieżka do wymogu PDF/Excel bez mieszania zapisu binarnego z prostymi GET-ami raportowymi.”*
- **Delegacja do istniejących serwisów:** liczby w pliku muszą być zgodne z JSON – jedno źródło prawdy (`MerchReportService`, `TicketingReportingService`, `LogisticsAdminService`).
- **`ReportBinaryRenderer` w `infrastructure`:** zgodnie z Ports & Adapters – generacja pliku to szczegół techniczny, nie reguła domenowa.
- **`inline://` w `export_jobs.file_path`:** brak magazynu plików na dysku w MVP; ścieżka opisuje nazwę pliku wysłanego do klienta (świadome uproszczenie pod obronę modelu audytu).

### Ryzyka / otwarte tematy

- PDF bez pełnej obsługi polskich znaków (etykiety uproszczone).
- Duże eksporty w pamięci (`byte[]`) – akceptowalne dla małych zbiorów w projekcie inżynierskim.

### Wplyw na wymagania projektu

- Realizacja punktu o **raportach biznesowych i eksporcie do PDF/Excel** z poziomu panelu administracyjnego.
- Wykorzystanie istniejących tabel audytu raportów (`report_runs`, `export_jobs`) z wcześniejszych migracji.

---

## 19. Sprint 16 - Wydruki parametryzowane DOCX (rozliczenie trasy)

Status: `Done`  
Cel: spełnienie wymagania **parametryzowanych wydruków na bazie szablonów Word (.docx)** z możliwością **zmiany szablonu przez użytkownika** (upload + aktywacja), w wariancie lean: jeden moduł szablonów (`TOUR_SETTLEMENT`), jeden typ raportu w generatorze (`TOUR_SETTLEMENT_DOCX`), dane z istniejącej logistyki (`TourSettlementAdminService`, `LogisticsAdminService`).

### Zrealizowane

- **Migracja Flyway** — [V14__Docx_Templates.sql](backend/src/main/resources/db/migration/V14__Docx_Templates.sql): tabela `docx_templates` (`name`, `module_code`, `template_version`, `active`, `file_path`, `created_at`), indeks unikalny częściowy: co najwyżej jeden aktywny szablon na `module_code`.
- **Encja domenowa** — [DocxTemplate.java](backend/src/main/java/com/bandhub/zsi/reporting/domain/DocxTemplate.java): metadane szablonu; fabryka `create`, metody `rename`, `setActive` (bez publicznych setterów Lombok poza kontrolowanym API).
- **Port repozytorium** — [DocxTemplateRepository.java](backend/src/main/java/com/bandhub/zsi/reporting/DocxTemplateRepository.java); **adapter JPA** — [SqlDocxTemplateRepository.java](backend/src/main/java/com/bandhub/zsi/infrastructure/SqlDocxTemplateRepository.java) (pakiet `infrastructure`, klasa niepubliczna).
- **Stałe modułu** — [DocxTemplateModuleCodes.java](backend/src/main/java/com/bandhub/zsi/reporting/dto/DocxTemplateModuleCodes.java): `TOUR_SETTLEMENT`.
- **DTO API** — [DocxTemplateResponse.java](backend/src/main/java/com/bandhub/zsi/reporting/dto/DocxTemplateResponse.java); [TourSettlementDocxPreviewPayload.java](backend/src/main/java/com/bandhub/zsi/reporting/dto/TourSettlementDocxPreviewPayload.java) (podgląd JSON dla generatora).
- **Typ raportu** — [BusinessReportType.java](backend/src/main/java/com/bandhub/zsi/reporting/dto/BusinessReportType.java): wartość `TOUR_SETTLEMENT_DOCX`.
- **Silnik placeholderów** — [TourSettlementDocxPlaceholderBuilder.java](backend/src/main/java/com/bandhub/zsi/reporting/infrastructure/TourSettlementDocxPlaceholderBuilder.java): mapa `${tourName}`, `${settlementTotalCosts}`, `${profitBalance}`, itd. (puste stringi, gdy brak rozliczenia w bazie).
- **Renderer DOCX** — [TourSettlementDocxRenderer.java](backend/src/main/java/com/bandhub/zsi/reporting/infrastructure/TourSettlementDocxRenderer.java): Apache POI `XWPF` — paragrafy, tabele, nagłówki; MVP: po zamianie jeden run na paragraf (uproszczenie formatowania Word).
- **Orkiestracja** — rozszerzenie [BusinessReportService.java](backend/src/main/java/com/bandhub/zsi/reporting/BusinessReportService.java): `preview` / `exportWithAudit` dla `TOUR_SETTLEMENT_DOCX`, format `docx`, odczyt pliku z `{app.upload.dir}/docx-templates/`, audyt jak PDF/XLSX (`report_runs`, `export_jobs`, `inline://`).
- **REST szablonów** — [DocxTemplateAdminService.java](backend/src/main/java/com/bandhub/zsi/reporting/DocxTemplateAdminService.java), [DocxTemplateAdminController.java](backend/src/main/java/com/bandhub/zsi/reporting/DocxTemplateAdminController.java): `GET/POST /api/admin/reports/docx-templates`, `PATCH .../{id}/activate`, `DELETE .../{id}` (upload `multipart`, walidacja `.docx`).
- **REST generatora** — [BusinessReportsController.java](backend/src/main/java/com/bandhub/zsi/reporting/BusinessReportsController.java): istniejące `/preview` i `/export` z `format=docx` dla typu `TOUR_SETTLEMENT_DOCX`.
- **Test jednostkowy** — [TourSettlementDocxRendererTest.java](backend/src/test/java/com/bandhub/zsi/reporting/infrastructure/TourSettlementDocxRendererTest.java): podstawianie w wygenerowanym programowo `.docx` bez zmiany kodu produkcyjnego przy zmianie treści szablonu.
- **Frontend** — [docx-template.service.ts](zsi-admin-web/src/app/core/services/docx-template.service.ts), [docx-template-list.component.ts](zsi-admin-web/src/app/features/reporting/docx-templates/docx-template-list.component.ts), trasa `admin/reports/docx-templates`, pozycja menu; rozszerzenie [business-report.service.ts](zsi-admin-web/src/app/core/services/business-report.service.ts) i [report-generator.component.ts](zsi-admin-web/src/app/features/reporting/report-generator.component.ts); przycisk **Pobierz DOCX** na [tour-settlement-detail.component.ts](zsi-admin-web/src/app/features/logistics/tour-settlements/tour-settlement-detail.component.ts).

### Przykładowe przepływy E2E (obrona)

#### Flow 1: Wgranie szablonu i zmiana bez deployu

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Admin | Loguje się (rola `ADMIN`) | Keycloak + panel | JWT |
| 2 | Admin | **Szablony DOCX** | `/admin/reports/docx-templates` | Formularz + lista |
| 3 | Admin | Wgrywa plik `.docx` z tekstem `${tourName}` | `POST .../docx-templates` (`multipart`) | Wiersz w `docx_templates`, plik w `uploads/.../docx-templates/*.docx`; pierwszy szablon modułu = **aktywny** |
| 4 | Admin | Edytuje ten sam plik lokalnie w Word (inna treść / nowe placeholdery), ponownie wgrywa | kolejny `POST` | Nowa wersja (`template_version`), domyślnie nieaktywna do czasu aktywacji |
| 5 | Admin | **Aktywuj** na liście | `PATCH .../{id}/activate` | Poprzedni aktywny w module wyłączony, wybrany włączony (unikalny indeks) |

#### Flow 2: Generacja wydruku z audytem

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Admin | Generator raportów, typ **Logistyka — rozliczenie trasy (DOCX)**, wybór trasy, **Podgląd** | `GET .../business/preview?type=TOUR_SETTLEMENT_DOCX&tourId=` | JSON z `TourSettlementDocxPreviewPayload` + `report_runs` (`PREVIEW`) |
| 2 | Admin | **Pobierz DOCX** | `GET .../business/export?type=TOUR_SETTLEMENT_DOCX&format=docx&tourId=` | Plik `.docx` z podstawionymi wartościami; `report_runs` (`COMPLETED`, `DOCX`) + `export_jobs` |
| 3 | Admin | Alternatywnie z ekranu rozliczenia | **Pobierz DOCX** na szczególe rozliczenia | Ten sam endpoint z `tourId` rozliczenia |

### Decyzje techniczne (klasy — dlaczego tak)

- **Szablony w module `reporting` obok audytu raportów:** jedna ścieżka do wymogu „wydruk + ślad w systemie”; szablon to artefakt raportowania, nie encja logistyczna.
- **`TOUR_SETTLEMENT_DOCX` zamiast rozszerzenia `TOUR_PROFITABILITY` o DOCX:** rozdzielenie formatów wyjściowych (PDF/XLSX vs szablon użytkownika) i czytelniejszy opis w `report_runs.report_name`.
- **Pliki w podkatalogu `docx-templates`:** separacja od zdjęć galerii; ta sama właściwość `app.upload.dir` co CMS.
- **„Bare minimum” renderer:** prosta zamiana `${...}` w tekście paragrafu; złożone szablony Word (podział placeholdera na wiele runów) mogą wymagać ręcznego zapisania frazy w jednym runie w edytorze.

### Ryzyka / otwarte tematy

- Placeholdery rozcięte przez Word na wiele runów — w MVP zalecenie: wpisywać placeholder jako ciągły tekst w jednym miejscu.
- Generacja w pamięci (`byte[]`) — jak w Sprint 15.

### Wplyw na wymagania projektu

- Realizacja punktu o **parametryzowanych wydrukach** i **zmianie szablonu przez użytkownika** (upload z panelu admina).

---

## 20. Sprint 17-18 - Aplikacja mobilna fana (Expo + React Native Web)

Status: `Done`  
Cel: dostarczenie minimalnej aplikacji fana (web-preview + mobile runtime) z logowaniem OIDC, konsumpcją istniejących publicznych endpointów (`products`, `orders`, `concerts`, `ticket-orders`) oraz funkcją setlist i CMS feed pod obronę projektu inżynierskiego.

### Zrealizowane

- **Nowa aplikacja w repo**: [bandhub-mobile](bandhub-mobile) (osobny projekt obok `backend` i `zsi-admin-web`), wygenerowany na Expo Router.
- **Publiczne endpointy read-only pod mobile** (bez rozbudowy domeny):
  - [NewsPublicController.java](backend/src/main/java/com/bandhub/zsi/cms/NewsPublicController.java): `GET /api/public/news`, `/page`, `/{id}` (delegacja do istniejącego `CmsAdminService`).
  - [GalleryFeedPublicController.java](backend/src/main/java/com/bandhub/zsi/cms/GalleryFeedPublicController.java): `GET /api/public/gallery` (delegacja do istniejącego `GalleryAdminService`).
  - [SetlistPublicController.java](backend/src/main/java/com/bandhub/zsi/fan/SetlistPublicController.java): `GET /api/public/setlists`, `/page`, `/{id}`, `/{id}/items`.
- **Setlisty fan-facing**:
  - [SetlistItemAdminService.java](backend/src/main/java/com/bandhub/zsi/fan/SetlistItemAdminService.java): dodana metoda `getBySetlistId(UUID)`, użyta przez publiczny endpoint.
- **CORS pod web-preview Expo (dynamiczne porty)**:
  - [SecurityConfig.java](backend/src/main/java/com/bandhub/zsi/config/SecurityConfig.java): zamiast sztywnej listy originów użyto `setAllowedOriginPatterns(http://localhost:*, http://127.0.0.1:*)` — Expo Web na dev podpisuje się dowolnym wolnym portem (8081 zajęty przez Keycloaka, więc Expo wybiera np. 8082/19000), a `setAllowCredentials(true)` wymaga properly skonfigurowanego patternu.
- **Rejestracja i logowanie fana (in-app, bez popupów)**:
  - [FanRegistrationPublicController.java](backend/src/main/java/com/bandhub/zsi/fan/FanRegistrationPublicController.java): `POST /api/public/register` (permitAll), tworzy konto fana w Keycloaku.
  - [FanRegistrationService.java](backend/src/main/java/com/bandhub/zsi/fan/FanRegistrationService.java): re-use istniejącego `UserAdminService` (Keycloak Admin Client) — tworzy usera, ustawia hasło `temporary=false`, przypisuje rolę `FAN`.
  - [FanRegistrationRequest.java](backend/src/main/java/com/bandhub/zsi/fan/dto/FanRegistrationRequest.java) / [FanRegistrationResponse.java](backend/src/main/java/com/bandhub/zsi/fan/dto/FanRegistrationResponse.java): walidacja Bean Validation (`@NotBlank`, `@Email`, `@Size`).
- **Warstwa mobilna - konfiguracja i API**:
  - [config.ts](bandhub-mobile/lib/config.ts): `EXPO_PUBLIC_*` + helper budowania URL API.
  - [http.ts](bandhub-mobile/lib/http.ts): ujednolicone requesty (`apiRequest`, `apiRequestRaw`) z obsługą JWT.
  - [api.ts](bandhub-mobile/lib/api.ts): kontrakty do `news`, `gallery`, `setlists`, `concerts`, `ticket-orders`, `products`, `orders`.
  - [api.ts](bandhub-mobile/types/api.ts): typy DTO odpowiadające backendowym rekordom + lokalne modele historii.
  - [storage.ts](bandhub-mobile/lib/storage.ts): persystencja tokenu oraz lokalnej historii zakupów (`AsyncStorage`).
- **Warstwa mobilna - state management**:
  - [AuthProvider.tsx](bandhub-mobile/providers/AuthProvider.tsx): in-app **Direct Access Grant** do Keycloaka (`POST .../protocol/openid-connect/token` z `grant_type=password`), bez zewnętrznego okna; `register()` woła `POST /api/public/register` i automatycznie loguje. Decoduje `preferred_username` z JWT do nagłówka konta.
  - [CartProvider.tsx](bandhub-mobile/providers/CartProvider.tsx): lokalny koszyk (add/remove/clear/total).
  - [AuthForm.tsx](bandhub-mobile/components/ui/AuthForm.tsx): wspólny formularz login/rejestracja (segmented tabs, walidacja, błędy z Keycloaka).
- **Warstwa mobilna - UI i routing**:
  - [app/_layout.tsx](bandhub-mobile/app/_layout.tsx): root stack + providers.
  - [app/(tabs)/_layout.tsx](bandhub-mobile/app/(tabs)/_layout.tsx): taby `Home`, `Koncerty`, `Merch`, `Bilety`, `Konto`.
  - [index.tsx](bandhub-mobile/app/(tabs)/index.tsx): feed CMS (news + galeria) + setlisty.
  - [concerts.tsx](bandhub-mobile/app/(tabs)/concerts.tsx), [concert-detail.tsx](bandhub-mobile/app/concerts/[id].tsx): listowanie koncertów + zakup biletu (`POST /api/public/ticket-orders`).
  - [merch.tsx](bandhub-mobile/app/(tabs)/merch.tsx), [product-detail.tsx](bandhub-mobile/app/products/[id].tsx), [cart.tsx](bandhub-mobile/app/cart.tsx), [checkout.tsx](bandhub-mobile/app/checkout.tsx): katalog produktów + koszyk + checkout (`POST /api/public/orders`).
  - [tickets.tsx](bandhub-mobile/app/(tabs)/tickets.tsx), [ticket-detail.tsx](bandhub-mobile/app/tickets/[id].tsx): „Moje bilety” oparte o lokalną historię z odpowiedzi API.
  - [account.tsx](bandhub-mobile/app/(tabs)/account.tsx): logowanie/wylogowanie + lokalna historia zamówień merch.
  - [Screen.tsx](bandhub-mobile/components/ui/Screen.tsx): wspólny kontener ekranu.

### Kontrakty API użyte przez mobilkę

- **CMS**: `GET /api/public/news/page`, `GET /api/public/news/{id}`, `GET /api/public/gallery`.
- **Setlisty**: `GET /api/public/setlists/page`, `GET /api/public/setlists/{id}/items`.
- **Ticketing**: `GET /api/public/concerts/page`, `GET /api/public/concerts/{id}`, `POST /api/public/ticket-orders`.
- **E-commerce**: `GET /api/public/products/page`, `GET /api/public/products/{id}`, `POST /api/public/orders`.
- **Auth fana**:
  - `POST {keycloakIssuer}/protocol/openid-connect/token` (Direct Grant `password`, klient `bandhub-public-client`).
  - `POST /api/public/register` — body `{username, password, email?, firstName?, lastName?}`, odpowiedź `{userId, username}`.

### Szczegółowe flow E2E (obrona)

#### Flow 1: Rejestracja, logowanie i checkout merchu

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Nowy fan | W zakładce **Konto** wybiera „Rejestracja”, podaje `username`, `password`, opcjonalnie email/imię | `POST /api/public/register` | `201 Created` + nowy user w Keycloaku z rolą `FAN` |
| 2 | App | Po rejestracji automatyczne logowanie | `POST {issuer}/protocol/openid-connect/token` (Direct Grant) | `access_token` zapisany w `AsyncStorage` |
| 3 | Istniejący fan | W zakładce **Konto** wybiera „Logowanie”, podaje login + hasło | `POST .../token` | Token zapisany |
| 4 | Fan | Przegląda produkty, dodaje do koszyka | `GET /api/public/products/page`, UI `CartProvider` | Lokalny koszyk |
| 5 | Fan | Checkout (adres, provider płatności) | `POST /api/public/orders` z `Authorization: Bearer <token>` | `201 Created`, nagłówek `Location` |
| 6 | App | Zapis historii zamówienia | `saveMerchOrder(...)` | Widoczne w zakładce **Konto** |

#### Flow 2: Fan kupuje bilet i widzi kod

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Fan | Otwiera listę koncertów | `GET /api/public/concerts/page` | Lista koncertów |
| 2 | Fan | Otwiera szczegóły koncertu | `GET /api/public/concerts/{id}` | Pula biletów i dostępność |
| 3 | Fan | Wybiera pulę + ilość i kupuje | `POST /api/public/ticket-orders` | `TicketPurchaseResponse(orderId, ticketCodes)` |
| 4 | Aplikacja | Zapisuje zakup lokalnie | `saveTicketPurchase(...)` | Zakładka **Bilety** pokazuje wpis |
| 5 | Fan | Otwiera szczegóły biletu | `tickets/[id]` | Lista kodów biletowych do okazania |

#### Flow 3: Fan przegląda setlisty koncertów

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Fan | Otwiera Home | `GET /api/public/setlists/page` | Lista opublikowanych setlist |
| 2 | Fan | Otwiera szczegóły setlisty | `GET /api/public/setlists/{id}/items` | Lista utworów (`songOrder`, `songTitle`, `durationSeconds`) |
| 3 | Fan | Korzysta z feedu CMS | `GET /api/public/news/page`, `GET /api/public/gallery` | Treści aktualizowane z panelu admina |

### Decyzje techniczne (klasy - dlaczego tak)

- **Maksymalny reuse backendu**: publiczne kontrolery delegują do istniejących serwisów (`CmsAdminService`, `GalleryAdminService`, `SetlistAdminService`, `UserAdminService`) zamiast mnożenia nowych warstw.
- **Setlisty jako osobny endpoint publiczny**: fan może korzystać z funkcji setlist bez roli `ADMIN`, zgodnie z celem aplikacji mobilnej.
- **Direct Grant zamiast Authorization Code w mobilce**: świadoma decyzja na MVP inżynierki — formularz logowania jest **w aplikacji** (lepszy UX, brak okienek przeglądarki). Klient `bandhub-public-client` w Keycloaku ma `Direct Access Grants Enabled = ON`. W produkcji wybralibyśmy Authorization Code + PKCE; tu liczy się prostota demo.
- **Rejestracja po stronie backendu, nie wprost na Keycloaku**: aplikacja nie ma uprawnień admina realmu, dlatego endpoint `/api/public/register` używa wewnętrznego `Keycloak` admin clienta i ustawia rolę `FAN` w jednym miejscu (spójność z istniejącym `UserAdminService`).
- **Lokalna historia „moje bilety / moje zamówienia”**: w MVP użyto `AsyncStorage`, bo istniejące kontrakty publiczne zwracają `orderId`/kody przy zakupie i to wystarcza do demonstracji procesu end-to-end.
- **Expo Router + tabs**: szybkie osiągnięcie czytelnego przepływu UI przy minimalnej liczbie zależności i prostym debugowaniu na webie.

### Ryzyka / otwarte tematy

- Historia zamówień/biletów po reinstalacji aplikacji jest tracona (lokalna persystencja MVP).
- W środowiskach innych niż localhost należy zaktualizować `EXPO_PUBLIC_API_BASE_URL` oraz CORS.
- **Direct Grant** wymaga włączonej opcji `Direct Access Grants Enabled` na kliencie `bandhub-public-client` w Keycloaku (publiczny client, bez secret). W produkcji: migracja do Authorization Code + PKCE.
- **Rejestracja zwraca błędy Keycloaka** (`409` przy zajętym username) jako `IllegalArgumentException` — globalny `RestExceptionHandler` mapuje to na `400 Bad Request` z polem `message`.

### Wplyw na wymagania projektu

- Spełnienie wymogu drugiej aplikacji klienckiej (mobile fan app) działającej na tym samym backendzie i logice danych.
- Domknięcie fan-facing procesów: CMS (odczyt), ticketing (zakup + kod), e-commerce (checkout).
- Włączenie funkcji setlist do aplikacji mobilnej zgodnie z rozbudową modułu fan/mobile.
- Self-service rejestracji fana zamyka pełny scenariusz „nowy użytkownik → konto → zakup”.

---

## 21. Sprint 17-18 hot-fix - Site Settings (CMS-only branding) + auth gate w mobilce

Status: `Done`  
Cel: domknięcie wymagania projektowego o pełnym sterowaniu treściami (aktualności, opisy, zdjęcia) z poziomu administracji + UX poprawki w aplikacji mobilnej (gate logowania na płatne moduły, czytelny błąd rejestracji).

### Zrealizowane

- **Backend - moduł SiteSettings (singleton CMS)**:
  - [SiteSettings.java](backend/src/main/java/com/bandhub/zsi/cms/domain/SiteSettings.java): encja z `id=1` (singleton), pola: `bandName`, `tagline`, `heroImageUrl`, `aboutText`, audyt `updatedAt`/`updatedBy`. Metoda `update(...)` używa `Assert.hasText` na `bandName` jako invariant domeny.
  - [SiteSettingsRepository.java](backend/src/main/java/com/bandhub/zsi/cms/SiteSettingsRepository.java): `JpaRepository<SiteSettings, Short>`.
  - [SiteSettingsAdminService.java](backend/src/main/java/com/bandhub/zsi/cms/SiteSettingsAdminService.java): `getSettings()`, `updateSettings(req, updatedBy)` - zapisuje znacznik czasu i autora, rzuca `EntityNotFoundException` jeśli ktoś usunąłby singleton.
  - [SiteSettingsAdminController.java](backend/src/main/java/com/bandhub/zsi/cms/SiteSettingsAdminController.java): `GET/PUT /api/admin/site-settings` (`@PreAuthorize("hasRole('ADMIN')")`).
  - [SiteSettingsPublicController.java](backend/src/main/java/com/bandhub/zsi/cms/SiteSettingsPublicController.java): `GET /api/public/site-settings` (permitAll) - feed brandingu konsumowany przez mobilkę.
  - [SiteSettingsResponse.java](backend/src/main/java/com/bandhub/zsi/cms/dto/SiteSettingsResponse.java) / [UpdateSiteSettingsRequest.java](backend/src/main/java/com/bandhub/zsi/cms/dto/UpdateSiteSettingsRequest.java): DTO + walidacja Bean Validation (`@NotBlank bandName`, `@Size`).
  - [V15__Site_Settings.sql](backend/src/main/resources/db/migration/V15__Site_Settings.sql): tabela + seed singletona (id=1) z neutralnymi defaultami.
- **Admin web - edytor**:
  - [site-settings.component.ts](zsi-admin-web/src/app/features/cms/site-settings/site-settings.component.ts): formularz Reactive Forms (`bandName`, `tagline`, `heroImageUrl` jako select z `GET /api/admin/gallery`, `aboutText`), podgląd hero, alerty success/error, znacznik aktualizacji.
  - [cms.service.ts](zsi-admin-web/src/app/core/services/cms.service.ts): rozszerzenie o `getSiteSettings()` / `updateSiteSettings(req)`.
  - Routing: `/admin/site-settings` w [app.routes.ts](zsi-admin-web/src/app/app.routes.ts), pozycja w sidebarze ([admin-layout.component.ts](zsi-admin-web/src/app/layout/admin-layout.component.ts), sekcja "CMS / Treści" → "Ustawienia strony").
- **Mobile - branding pochodzi z CMS, nie z kodu**:
  - [BrandingProvider.tsx](bandhub-mobile/providers/BrandingProvider.tsx): kontekst React, zaciąga `/api/public/site-settings` przy starcie aplikacji (`reload()`), udostępnia `settings` całemu drzewku.
  - [_layout.tsx (root)](bandhub-mobile/app/_layout.tsx): owinięcie `AuthProvider → BrandingProvider → CartProvider`.
  - [(tabs)/_layout.tsx](bandhub-mobile/app/(tabs)/_layout.tsx): nagłówek zakładki Home używa `settings.bandName` (gdy załadowane), `tabBarLabel` zostaje stałe ("Home") jako etykieta nawigacji.
  - [(tabs)/index.tsx](bandhub-mobile/app/(tabs)/index.tsx): hero image (`settings.heroImageUrl`), tytuł (`bandName`), tagline, About - wszystko z CMS-a; usunięto twardo zakodowane teksty marketingowe ("BandHub Fan App", "Aktualnosci, setlisty i media z CMS").
  - [api.ts](bandhub-mobile/lib/api.ts) + [types/api.ts](bandhub-mobile/types/api.ts): nowy typ `SiteSettings` i klient `fetchSiteSettings()`.
- **Auth gate dla Bilety i Merch**:
  - [RequireAuth.tsx](bandhub-mobile/components/ui/RequireAuth.tsx): kontener pokazuje `children` tylko zalogowanym; w innym wypadku wyświetla kartę "Wymagane logowanie" + CTA do zakładki Konto.
  - [(tabs)/merch.tsx](bandhub-mobile/app/(tabs)/merch.tsx) i [(tabs)/tickets.tsx](bandhub-mobile/app/(tabs)/tickets.tsx): zawijają zawartość w `RequireAuth`, fetch katalogu/historii uzależniony od `isAuthenticated`.
- **Diagnostyka rejestracji**:
  - [UserAdminService.java](backend/src/main/java/com/bandhub/zsi/user/UserAdminService.java) - `createUser`: gdy Keycloak zwróci status inny niż 201/409, czytamy body (`response.readEntity(String.class)`) i przekazujemy w komunikacie wyjątku (`Keycloak <status>: <body>`). Dodatkowo `setEmailVerified(true)`, żeby brak weryfikacji maila nie blokował logowania.
  - [FanRegistrationService.java](backend/src/main/java/com/bandhub/zsi/fan/FanRegistrationService.java): tworzy usera bez hasła w fazie 1, w fazie 2 ustawia hasło `temporary=false` (brak required action `UPDATE_PASSWORD` przy logowaniu).

### Kontrakty API

- `GET /api/public/site-settings` → `SiteSettingsResponse`. Permit-all, czytane przez mobilkę przy starcie.
- `GET /api/admin/site-settings` / `PUT /api/admin/site-settings` (rola `ADMIN`).

### Flow E2E (obrona): edycja brandingu i propagacja do mobilki

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Manager | Otwiera "CMS / Treści → Ustawienia strony" | Angular `/admin/site-settings` | Załadowane aktualne wartości (`GET /api/admin/site-settings`) |
| 2 | Manager | Wybiera hero z galerii i edytuje `aboutText` | Reactive Form | Walidacja `bandName` jako `@NotBlank` |
| 3 | Manager | Klik "Zapisz" | `PUT /api/admin/site-settings` | `200 OK` + `updatedAt`/`updatedBy` w odpowiedzi |
| 4 | Fan (mobile) | Restart appki / odświeżenie Home | `GET /api/public/site-settings` przez `BrandingProvider` | Nowy hero + nazwa zespołu + about widoczne na Home, brak twardo wpisanych tekstów w kliencie |

### Decyzje techniczne (klasy - dlaczego tak)

- **Singleton z fixed id**: brak złożonego CRUD-u dla strony ustawień - wystarczy edycja jednego rekordu. `SMALLINT` jako klucz, seed w migracji V15 zapewnia stałą obecność.
- **Reuse `GalleryAdminService`**: hero image to URL do uploadowanego pliku - nie duplikujemy uploadu, tylko reużywamy już istniejący strumień `/api/admin/gallery`.
- **Public read-only**: dane brandingu nie są tajne - mobilka pobiera je bez tokenu (jak news/gallery).
- **Brak hardcodu w mobilce**: tekst hero, tytuł, about i nawet etykieta header zakładki Home pochodzą ze stanu `BrandingProvider` zasilanego z backendu - spełnia wymóg projektu o sterowaniu wszystkimi widocznymi treściami z administracji.
- **Auth gate w UI a nie w API**: katalog produktów / historia biletów to publiczne endpointy w MVP, ale UX projektu wymaga, by fan najpierw się zalogował - dlatego gate jest klientowy (`RequireAuth`), bez zmiany kontraktu backendu.
- **Diagnostyczne błędy rejestracji**: zamiast generycznego "Nie udało się utworzyć użytkownika" zwracamy `Keycloak 400: <body>` - manager / programista widzi konkretną przyczynę (np. `User exists with same email`, `password policy violation`).

### Ryzyka / otwarte tematy

- Brak inwalidacji cache w mobilce po edycji w adminie - fan musi zrestartować appkę albo `pull-to-refresh` (do dorzucenia w razie potrzeby).
- `aboutText` to plain text (`TEXT`) - jeśli powstanie potrzeba bogatej formatki (markdown / HTML) trzeba dorobić sanitizację.
- Auth gate jest UI-only; backend dalej dopuszcza anonimowe `GET /api/public/products`. To OK na MVP, w produkcji można dodać rate-limit lub przenieść za `JwtAuth`.

### Wplyw na wymagania projektu

- **Realizuje wymóg**: „Sterowanie wszystkimi widocznymi aktualnościami, opisami, zdjęciami itd. powinno odbywać się z poziomu administracyjnej części systemu (żadnych tekstów lub zmiennych grafik nie można umieszczać na sztywno w kodzie)" - branding (nazwa, tagline, hero, about) jest teraz w bazie i edytowalny z panelu admina; mobilka ma jedynie ramę UI.
- Domyka UX rejestracji fana po stronie mobilki (czytelny komunikat błędu Keycloaka + reset hasła non-temporary).
- Auth gate jasno komunikuje fanowi konieczność logowania przed merchem/biletami.

---

## 22. Sprint 17-18 hot-fix #2 - UI Dictionary (mikro-copywriting w pełni z CMS)

Status: `Done`  
Cel: domknąć wymóg „żadnych tekstów na sztywno w kodzie" zgodnie z architektonicznym blueprintem trzech filarów (encje biznesowe / branding / mikro-copywriting). Dodajemy trzeci filar — słownik UI sterowany z panelu admina, konsumowany w aplikacji mobilnej.

### Zrealizowane

- **Backend - moduł UiDictionary + cache**:
  - [UiDictionaryEntry.java](backend/src/main/java/com/bandhub/zsi/cms/domain/UiDictionaryEntry.java): encja `ui_dictionary` (PK = `key_name VARCHAR(150)`, `value TEXT`, `description`, audyt). Statyczne fabryki `create(...)` / metoda `update(...)` walidują niepuste klucze i wartości (`Assert.hasText`).
  - [UiDictionaryRepository.java](backend/src/main/java/com/bandhub/zsi/cms/UiDictionaryRepository.java): `JpaRepository<UiDictionaryEntry, String>`, package-private (kapsułkowanie modułu CMS).
  - [UiDictionaryService.java](backend/src/main/java/com/bandhub/zsi/cms/UiDictionaryService.java): operacje CRUD + `getFlatDictionary()` zwracający `Map<String,String>`. Adnotacje:
    - `@Cacheable(value = "uiDictionary", key = "'flat'")` — pojedynczy wpis w cache, klucz statyczny.
    - `@CacheEvict(value = "uiDictionary", allEntries = true)` na każdej mutacji (`create/update/delete`) — gwarancja, że po edycji w adminie nie wisi stary stan.
  - [UiDictionaryAdminController.java](backend/src/main/java/com/bandhub/zsi/cms/UiDictionaryAdminController.java): pełny CRUD (`GET/POST/PUT/DELETE /api/admin/ui-dictionary[...]`), `@PreAuthorize("hasRole('ADMIN')")`.
  - [UiDictionaryPublicController.java](backend/src/main/java/com/bandhub/zsi/cms/UiDictionaryPublicController.java): `GET /api/public/ui-dictionary` zwraca płaską mapę `klucz → wartość` (kontrakt jak w `ngx-translate`/`i18next`).
  - [V16__Ui_Dictionary.sql](backend/src/main/resources/db/migration/V16__Ui_Dictionary.sql): tabela + seed wszystkich kluczy używanych w aplikacji mobilnej (zakładki, etykiety, komunikaty empty-state, formularz auth, gate logowania) — komisja widzi, że ani jeden tekst nie żyje w kodzie kliencie.
  - DTO walidowane Bean Validation: [CreateUiDictionaryEntryRequest.java](backend/src/main/java/com/bandhub/zsi/cms/dto/CreateUiDictionaryEntryRequest.java), [UpdateUiDictionaryEntryRequest.java](backend/src/main/java/com/bandhub/zsi/cms/dto/UpdateUiDictionaryEntryRequest.java), [UiDictionaryEntryResponse.java](backend/src/main/java/com/bandhub/zsi/cms/dto/UiDictionaryEntryResponse.java).
- **Backend - włączenie cache + cache na SiteSettings**:
  - [BackendApplication.java](backend/src/main/java/com/bandhub/zsi/BackendApplication.java): dodano `@EnableCaching` na klasie głównej.
  - [SiteSettingsAdminService.java](backend/src/main/java/com/bandhub/zsi/cms/SiteSettingsAdminService.java): metoda `getSettings()` opatrzona `@Cacheable("siteSettings")`, `updateSettings(...)` opatrzone `@CacheEvict(allEntries = true)`. Zgodnie z rekomendacją blueprintu: dane czytane przy każdym starcie aplikacji powinny być cache'owane.
- **Admin web - słownik UI z inline-edycją**:
  - [ui-dictionary-list.component.ts](zsi-admin-web/src/app/features/cms/ui-dictionary/ui-dictionary-list.component.ts): widok tabeli (Klucz | Wartość | Opis | Akcje) na Angular Signals + filtr `computed` po prefixie/wartości. Przyciski „Zapisz" aktywne tylko gdy `isDirty(row)`. Formularz „Dodaj klucz" + diagnostyka błędów backendu (`status 0 / CORS`, timeout, Keycloak msg). Operacja delete chroniona `confirm(...)`.
  - [cms.service.ts](zsi-admin-web/src/app/core/services/cms.service.ts): rozszerzony o `getUiDictionary()` / `createUiDictionaryEntry()` / `updateUiDictionaryEntry(key, req)` / `deleteUiDictionaryEntry(key)` + interfejsy DTO.
  - Routing: `/admin/ui-dictionary` w [app.routes.ts](zsi-admin-web/src/app/app.routes.ts), pozycja w sidebarze ([admin-layout.component.ts](zsi-admin-web/src/app/layout/admin-layout.component.ts), sekcja „CMS / Treści" → „Słownik UI").
- **Mobile - provider słownika + helper `t()`**:
  - [DictionaryProvider.tsx](bandhub-mobile/providers/DictionaryProvider.tsx): kontekst React, na starcie aplikacji ładuje `/api/public/ui-dictionary` (fetchUiDictionary), trzyma mapę w stanie, eksponuje `t(key, fallback)`. Hook `useText()` zwraca samą funkcję — wygodniejsze API w komponentach. Fallback z drugiego argumentu pełni rolę „domyślnej wartości" w razie awarii backendu lub świeżo dodanego klucza.
  - [_layout.tsx (root)](bandhub-mobile/app/_layout.tsx): nowa hierarchia `AuthProvider → DictionaryProvider → BrandingProvider → CartProvider`.
  - [api.ts](bandhub-mobile/lib/api.ts): klient `fetchUiDictionary()` typowany jako `Promise<Record<string,string>>`.
- **Mobile - zamiana hardcodowanych tekstów na klucze**:
  - [(tabs)/_layout.tsx](bandhub-mobile/app/(tabs)/_layout.tsx): `tabBarLabel` i `title` każdej zakładki (`tabs.home`, `tabs.concerts`, `tabs.merch`, `tabs.tickets`, `tabs.account`).
  - [(tabs)/index.tsx](bandhub-mobile/app/(tabs)/index.tsx): nagłówki sekcji + empty-state'y (`home.section.*`, `home.empty.*`).
  - [(tabs)/account.tsx](bandhub-mobile/app/(tabs)/account.tsx): tytuł/podtytuł guest, powitanie zalogowanego, sekcja zamówień, empty-state, przycisk wyloguj (`account.title.guest`, `account.subtitle.guest`, `account.greeting`, `account.subtitle.user`, `account.section.orders`, `account.empty.orders`, `account.button.logout`).
  - [(tabs)/tickets.tsx](bandhub-mobile/app/(tabs)/tickets.tsx): tytuł, gate message, podtytuł, empty, etykiety wpisu (`tickets.title`, `tickets.subtitle`, `tickets.empty`, `tickets.gate.message`, `tickets.label.purchasedAt`, `tickets.label.codes`).
  - [(tabs)/merch.tsx](bandhub-mobile/app/(tabs)/merch.tsx): tytuł, podtytuł, etykieta przycisku koszyka, empty, gate, etykieta `Stan` (`merch.title`, `merch.subtitle`, `merch.button.cart`, `merch.empty`, `merch.gate.message`, `merch.label.stock`).
  - [components/ui/AuthForm.tsx](bandhub-mobile/components/ui/AuthForm.tsx): wszystkie taby, etykiety pól, placeholdery, podtytuły i przyciski są zasilane z `t(...)` (`auth.tab.login`, `auth.tab.register`, `auth.label.username`, `auth.label.password`, `auth.label.email`, `auth.label.firstName`, `auth.label.lastName`, `auth.placeholder.username`, `auth.placeholder.password`, `auth.placeholder.email`, `auth.subtitle.login`, `auth.subtitle.register`, `auth.button.login`, `auth.button.register`, `auth.error.generic`).
  - [components/ui/RequireAuth.tsx](bandhub-mobile/components/ui/RequireAuth.tsx): tytuł, opis i CTA gate'a logowania (`require_auth.title`, `require_auth.message`, `require_auth.cta`); fallbacki w kodzie pełnią rolę „kopii zapasowej".

### Kontrakty API

- `GET /api/public/ui-dictionary` → `Map<String, String>` (płaski JSON), permit-all. Backend cache'uje wynik (`uiDictionary`), więc kolejne starty appki są darmowe dla bazy.
- `GET /api/admin/ui-dictionary` → `List<UiDictionaryEntryResponse>` (rola `ADMIN`).
- `POST /api/admin/ui-dictionary` → `201 Created`, body `UiDictionaryEntryResponse`. Walidacja: `key` `@NotBlank @Size(max=150)`, `value` `@NotBlank`. Wyrzuca `IllegalArgumentException` przy duplikacie klucza (mapowane na `400`).
- `PUT /api/admin/ui-dictionary/{key}` → `200 OK`, body `UiDictionaryEntryResponse`. Aktualizuje `value` i `description`, audyt `updatedBy` z `Authentication.getName()`.
- `DELETE /api/admin/ui-dictionary/{key}` → `204 No Content`, `EntityNotFoundException` (`404`) gdy klucz nie istnieje.

### Flow E2E (obrona): zmiana etykiety w słowniku → mobilka

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Manager | Loguje się do panelu admina, wchodzi w „CMS / Treści → Słownik UI" | Angular `/admin/ui-dictionary` | Tabela kluczy + filtr |
| 2 | Manager | Filtruje po `tickets.title`, zmienia wartość z „Moje bilety" na „Moje wejściówki", klika „Zapisz" | `PUT /api/admin/ui-dictionary/tickets.title` | `200 OK` + `updatedAt` w odpowiedzi; backend invaliduje cache `uiDictionary` |
| 3 | Backend | Następne `GET /api/public/ui-dictionary` przelicza mapę raz i cache'uje | `@Cacheable("uiDictionary")` | Spójna mapa dla wszystkich klientów |
| 4 | Fan (mobile) | Restart appki | `DictionaryProvider` na starcie woła `fetchUiDictionary()` | Nowa mapa zapisana w stanie |
| 5 | Fan | Otwiera zakładkę „Bilety" | `useText()(`tickets.title`)` | Widzi „Moje wejściówki" — bez deploymentu mobilki |

### Flow E2E (defensywny): backend offline / brak klucza

| Krok | Akcja | Efekt |
|------|--------|--------|
| 1 | `fetchUiDictionary()` rzuca błędem (np. `Failed to fetch`) | `entries = {}`, `error` ustawiony, ale aplikacja **nie** się crashuje |
| 2 | Komponent woła `t('account.button.logout', 'Wyloguj')` | Ponieważ `entries['account.button.logout']` jest `undefined`, helper zwraca fallback `'Wyloguj'` |
| 3 | Manager doda nowy klucz `home.banner.cta` w panelu, ale mobilka jeszcze go nie używa | Brak ruchu w UI; nieużywane klucze nie kosztują nic poza wierszem w bazie |

### Decyzje techniczne (klasy - dlaczego tak)

- **PK = klucz tekstowy, nie UUID**: klucz słownika jest stabilny i ludzki (`auth.button.login`), ma przewagę nad sztucznym ID — w samym JSON-ie publicznym i tak operujemy kluczami, więc trzymanie ich jako PK eliminuje konieczność ekstra mapowania.
- **`@Cacheable("uiDictionary")` z kluczem `'flat'`**: jedna mapa, jedna pozycja w cache. Mutacje robią `@CacheEvict(allEntries = true)`, bo i tak rebuild jest tani (lista wpisów słownika jest mała). Jest to świadome podejście dla MVP — w produkcji można rozważyć cache na pojedyncze klucze.
- **Płaska mapa zamiast struktury zagnieżdżonej**: kontrakt zgodny z popularnymi bibliotekami i18n (`i18next`, `ngx-translate`). Klient nie musi parsować zagnieżdżeń, a backend nie musi grupować — krócej i prościej, łatwiej zaprezentować na obronie.
- **Helper `t(key, fallback)` zamiast pipe'a**: w React Native nie ma pipe'ów, więc używamy hooka `useText()` i wywołania `t('key', 'fallback')`. Fallback w kodzie pełni rolę awaryjnej kopii treści — w razie awarii backendu/cache appka jest dalej użyteczna i nie pokazuje gołych kluczy.
- **Inline-edycja w panelu**: w jednym widoku manager filtruje + edytuje wiele kluczy. Kompaktowo i ergonomicznie — brak konieczności przechodzenia do osobnego ekranu detali (zgodnie z rekomendacją blueprintu „Tabela (Klucz | Wartość) z możliwością edycji w locie").
- **Brak APP_INITIALIZER w mobilce**: zamiast blokować start appki, korzystamy z `DictionaryProvider` z fallbackiem. Daje to lepsze UX (appka nie wisi przy zerowym połączeniu) i zachowuje invariant: każdy widoczny tekst ma klucz.
- **Granica modułu**: `UiDictionaryRepository` jest package-private — nie wychodzi poza pakiet `com.bandhub.zsi.cms`. Zgodne z hexagonalnym podziałem (port: `UiDictionaryService`, adapter HTTP: dwa kontrolery).

### Ryzyka / otwarte tematy

- Cache w mobilce trzymany jest tylko w pamięci procesu — restart aplikacji wymusza ponowne pobranie. Można dorzucić `AsyncStorage` jako warstwę offline, ale dla projektu inżynierskiego MVP wystarcza.
- Nie obsługujemy wielojęzyczności — jedna wartość per klucz. Wymagałoby dodania kolumny `lang_tag` i przepuszczenia `Accept-Language`. To naturalne rozszerzenie, ale poza zakresem MVP.
- Markdown rendering dla `aboutText` / długich artykułów — pozostawione jako follow-up; obecnie plain text. Migracja do `react-native-markdown-display` to czysto frontowa zmiana.
- Sztywne tytuły nagłówków `Stack.Screen` w `_layout.tsx` (np. „Aktualnosc", „Setlista", „Bilet") nie przeszły jeszcze przez słownik. Etykiety w karcie nawigacji są sterowane z dictionary; nagłówki ekranów detalu warto przenieść w drugiej iteracji.

### Wplyw na wymagania projektu

- **Domyka wymóg projektu** „Sterowanie wszystkimi widocznymi aktualnościami, opisami, zdjęciami itd. powinno odbywać się z poziomu administracyjnej części systemu (żadnych tekstów lub zmiennych grafik nie można umieszczać na sztywno w kodzie)". Trzy filary blueprintu są wdrożone:
  1. **Encje biznesowe** (news, concerts, gallery, setlists) — sprinty 1–16.
  2. **Branding globalny (SiteSettings)** — sprint 17–18 hot-fix #1.
  3. **Mikro-copywriting (UI Dictionary)** — ten sprint.
- Demonstruje praktyki inżynieryjne na obronę: hexagonalny podział, cache (`@Cacheable` + `@CacheEvict`), Bean Validation, JPA entity invariants (`Assert.hasText`), reactive Angular Signals, defensywne fallbacki w kliencie.

---

## 23. Sprint 17-18 hot-fix #3 - Auth proxy (Backend-for-Frontend) + walidacja rejestracji

Status: `Done`  
Cel: domknąć rejestrację i logowanie fana w aplikacji mobilnej. Fan blokowany był na dwóch frontach: surowy błąd Keycloaka `error-username-invalid-character` (spacja w nazwie użytkownika) oraz CORS na bezpośrednim wywołaniu `:8081/protocol/openid-connect/token` z origin Expo Web (`:8082`). Rozwiązanie: trzy warstwy obrony przy walidacji + Backend-for-Frontend dla logowania.

### Zrealizowane

- **Backend - walidacja rejestracji (3 warstwy)**:
  - [FanRegistrationRequest.java](backend/src/main/java/com/bandhub/zsi/fan/dto/FanRegistrationRequest.java): pole `username` opatrzone `@Pattern(regexp = "^[A-Za-z0-9._@-]+$")` zgodnym z domyślnym User Profile Keycloaka. Polskie `message` dla każdego naruszenia (`@NotBlank`, `@Size`, `@Pattern`).
  - [UserAdminService.translateKeycloakError](backend/src/main/java/com/bandhub/zsi/user/UserAdminService.java): mapper znanych kodów błędów Keycloaka (`error-username-invalid-character`, `User exists with same username/email`, `password policy`, `error-invalid-email`, `error-user-attribute-required`) na czytelny PL komunikat. Nieznane kody trafiają do generycznego komunikatu z surowym body — zachowujemy diagnostykę.
- **Backend - auth proxy (BFF dla mobilki)**:
  - [FanAuthService.java](backend/src/main/java/com/bandhub/zsi/fan/FanAuthService.java): metoda `login(LoginRequest)` wywołuje password grant na Keycloak token endpoint przez `org.springframework.web.client.RestClient`. Pobiera `access_token`, `refresh_token`, `expires_in`, `token_type` i mapuje na DTO. Lokalna metoda `translateLoginError(...)` mapuje błędy 401/`invalid_grant` (zła nazwa/hasło), `account disabled`, `account is not fully set up`, `invalid_client` na PL.
  - [FanAuthPublicController.java](backend/src/main/java/com/bandhub/zsi/fan/FanAuthPublicController.java): `POST /api/public/auth/login` (permitAll). Body: `LoginRequest { username, password }`. Response: `LoginResponse { accessToken, refreshToken, expiresIn, tokenType }`.
  - DTO: [LoginRequest.java](backend/src/main/java/com/bandhub/zsi/fan/dto/LoginRequest.java), [LoginResponse.java](backend/src/main/java/com/bandhub/zsi/fan/dto/LoginResponse.java). `LoginRequest` ma `@NotBlank` na obu polach.
  - [application.properties](backend/src/main/resources/application.properties): nowe propsy `app.keycloak.issuer-uri` i `app.keycloak.public-client-id` jako konfiguracja serwisu.
- **Mobile - kontrakt z backendem zamiast Keycloaka**:
  - [AuthProvider.tsx](bandhub-mobile/providers/AuthProvider.tsx): `login()` woła `apiRequest('/api/public/auth/login', { method: 'POST', body: { username, password } })` zamiast bezpośredniego `fetch` do `:8081`. Usunięty pomocnik `parseKeycloakError(...)` i import `config.keycloakIssuer/keycloakClientId` (już niepotrzebne — Keycloak issuer żyje tylko po stronie backendu).
- **Mobile - czytelne błędy z backendu**:
  - [lib/http.ts](bandhub-mobile/lib/http.ts): nowa klasa `ApiError extends Error` z polami `status` i `validationErrors`. Funkcja `parseErrorPayload(text, status)` parsuje `ApiErrorResponse` z `RestExceptionHandler` — jeśli są `validationErrors` (`{ field → message }`), formatuje je per-pole; w innym wypadku bierze `message`. Koniec ze surowym JSON-em w UI.
- **Mobile - walidacja w AuthForm jeszcze przed submitem**:
  - [components/ui/AuthForm.tsx](bandhub-mobile/components/ui/AuthForm.tsx): regex `USERNAME_REGEX = /^[A-Za-z0-9._@-]+$/` i `PASSWORD_MIN_LENGTH = 8` jako moduł-stałe. Dwa `useMemo`-y zwracające `usernameValidationError`/`passwordValidationError` w trybie rejestracji. Stylowanie: `inputError` (czerwona ramka), `fieldHint` (szary szept pod polem), `fieldError` (czerwony komunikat). `submitDisabled` agregat blokuje przycisk dopóki walidacja nie przejdzie.
- **CMS - klucze auth.error.\* w słowniku UI**:
  - [V17__Ui_Dictionary_Auth_Hints.sql](backend/src/main/resources/db/migration/V17__Ui_Dictionary_Auth_Hints.sql): nowa migracja dorzucająca klucze `auth.hint.username`, `auth.error.username.tooShort`, `auth.error.username.invalidCharacter`, `auth.error.password.tooShort` z `ON CONFLICT (key_name) DO NOTHING` (idempotentnie). Manager może modyfikować te komunikaty z panelu „Słownik UI" bez ingerencji w kod, fallback w kodzie zostaje jako safety net.

### Kontrakty API

- `POST /api/public/auth/login` → `LoginResponse { accessToken, refreshToken, expiresIn, tokenType }`. PermitAll. Walidacja `@NotBlank` na obu polach (400 z `validationErrors`).
- `POST /api/public/register` → `FanRegistrationResponse { userId, username }`. Walidacja DTO (`@Pattern`, `@Size`, `@NotBlank`, `@Email`) wraca z 400 + `validationErrors` zanim w ogóle uderzymy w Keycloaka. Błędy Keycloaka tłumaczymy w `UserAdminService.translateKeycloakError(...)`.

### Flow E2E (obrona): rejestracja + auto-login fana

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Fan | Wpisuje `Michal fan` w formularzu rejestracji | `AuthForm` | Walidacja w UI: regex blokuje spację, czerwona ramka i komunikat z `auth.error.username.invalidCharacter`, przycisk „Zarejestruj się" zablokowany |
| 2 | Fan | Poprawia na `michalfan`, hasło `passw0rd!`, klika „Zarejestruj się" | `POST /api/public/register` | DTO przechodzi `@Pattern`, `FanRegistrationService` tworzy usera w Keycloaku z rolą FAN |
| 3 | App | `register()` w AuthProvider od razu woła `login()` | `POST /api/public/auth/login` | `FanAuthService` robi password grant po stronie serwera (brak CORS na Keycloaku) i zwraca `accessToken` |
| 4 | App | Zapis tokenu w `AsyncStorage`, dekodowanie `preferred_username` z JWT | `lib/storage`, `decodeUsernameFromJwt` | Stan `isAuthenticated = true`, fan widzi zakładkę Konto z imieniem |

### Flow E2E (negatywny): login z błędnym hasłem

| Krok | Aktor | Akcja | Endpoint / UI | Oczekiwany efekt |
|------|--------|--------|---------------|------------------|
| 1 | Fan | Wpisuje `michalfan` + `bad-pass` | `AuthForm` | Submit |
| 2 | Backend | `RestClient` dostaje 401 z Keycloaka z body `{"error":"invalid_grant",...}` | `FanAuthService.translateLoginError` | Rzuca `IllegalArgumentException("Nieprawidlowa nazwa uzytkownika lub haslo.")` |
| 3 | App | `apiRequest` parsuje `ApiErrorResponse` i tworzy `ApiError(message, 400)` | `lib/http.parseErrorPayload` | Komunikat „Nieprawidłowa nazwa użytkownika lub hasło." pod formularzem |

### Decyzje techniczne (klasy - dlaczego tak)

- **Backend-for-Frontend dla loginu**: mobilka rozmawia tylko z `:8080`. To jednolity entry point, brak konfigurowania Web Origins na realmie Keycloaka, brak ryzyka, że demo-port Expo (`:8082`, `:19006`) nie jest na białej liście. Komisja zobaczy, że świadomie ograniczyliśmy „surface area" CORS-a.
- **`RestClient` zamiast `WebClient` lub niskopoziomowego HTTP**: Spring 6.1+ oferuje synchroniczny, deklaratywny klient idealny do prostych proxy-call'i. Brak zależności od `spring-webflux`, kod krótszy niż `RestTemplate`.
- **`@Pattern` zsynchronizowany z Keycloak User Profile**: zamiast czekać na 400 z realmu, walidujemy DTO regexem zgodnym z domyślnym profilem Keycloaka. To eliminuje całą klasę błędów (`error-username-invalid-character`) bez dotykania konfiguracji realmu — zysk dla portability projektu (wystarczy uruchomić Keycloaka z domyślnym User Profile, żadnego custom-policy).
- **Mapper błędów Keycloaka po polsku**: zamiast wyrzucać surowy `{"errorMessage":"User exists with same username","field":"username"}` na ekran fana, tłumaczymy znane kody na PL. Nieznane wpadają w fallback z surowym body — pomaga w trakcie developmentu / testów.
- **Klasa `ApiError` w mobilce**: zachowanie typu wyjątku z `status` i `validationErrors`. To otwiera drogę do bardziej zaawansowanej obsługi (np. „dodaj komunikat pod konkretnym polem" zamiast w `<Text style={styles.error}>`), ale na MVP wystarczy do ładnego sformatowania.
- **Walidacja po stronie klienta + serwera**: defense-in-depth. UI pokazuje hint zanim fan wyśle requesta, backend i tak waliduje raz jeszcze. Dwa filtry, dwa różne komunikaty fallbackowe — żaden surowy 500 nie wycieknie do mobilki.
- **Migracja idempotentna `ON CONFLICT DO NOTHING`**: V17 nie zepsuje się, jeśli ktoś wcześniej dodał te klucze ręcznie z panelu „Słownik UI" — projekt inżynierski powinien wytrzymać re-run migracji bez sztucznych „undo".

### Ryzyka / otwarte tematy

- **Refresh token tracony**: backend zwraca `refreshToken` w `LoginResponse`, ale mobilka go nie używa — po wygaśnięciu access tokenu fan zaloguje się ponownie. Implementacja `POST /api/public/auth/refresh` to follow-up (analogiczne proxy do password grantu).
- **Brak rate-limiting na `/api/public/auth/login`**: w produkcji potrzebny jest limit prób (np. Spring Cloud Gateway z `RequestRateLimiter` lub Bucket4j). Na MVP polegamy na Keycloaku, który ma `Brute Force Detection` w realmie.
- **`Direct Access Grants Enabled` musi być włączony na kliencie publicznym**: opisane już w sekcji 20, dotyczy obu hot-fixów.
- **Logout nie odwołuje sesji w Keycloaku**: usuwamy tylko lokalny token. Pełny logout wymagałby `POST /protocol/openid-connect/logout` z `refresh_token` — jak refresh, follow-up.

### Wpływ na wymagania projektu

- Domyka funkcjonalność rejestracji i logowania w mobilce (warunek konieczny do testów ticketingu i merchu).
- Wzmacnia argumentację „spójność warstwy klienckiej": admin web używa standardowego OIDC (PKCE), mobilka używa proxy do password granta — oba scenariusze obsłużone, ale mobilka NIE eksponuje konfiguracji Keycloaka (publiczny client id) w aplikacji co dla projektu inżynierskiego jest dobrym argumentem o ograniczaniu „attack surface".
- Demonstruje praktyki inżynieryjne: defense-in-depth (walidacja klient + serwer), Backend-for-Frontend, mapping kodów Keycloaka, idempotentne migracje słownika UI.

---

## 24. Miejsce na kolejne podsumowania

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

