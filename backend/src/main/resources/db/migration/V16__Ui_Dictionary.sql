-- V16__Ui_Dictionary.sql
-- Slownik mikro-copywritingu: kazdy widoczny w UI tekst (etykieta przycisku, nagłówek
-- sekcji, komunikat) jest sterowany z bazy. Klient (mobile/admin) pobiera plaska mape
-- key->value przy starcie i przekazuje przez helper / pipe.

CREATE TABLE ui_dictionary
(
    key_name    VARCHAR(150) PRIMARY KEY,
    value       TEXT          NOT NULL,
    description VARCHAR(255),
    updated_at  TIMESTAMP     NOT NULL,
    updated_by  VARCHAR(255)
);

-- Seed kluczy uzywanych przez mobile fan app. Wartosci w jezyku polskim.
-- Manager moze edytowac dowolnie z panelu admina (np. z kampania "BandHub Live 2025!").
INSERT INTO ui_dictionary (key_name, value, description, updated_at) VALUES
    ('home.section.news',          'Aktualnosci',                     'Naglowek sekcji aktualnosci na Home',           NOW()),
    ('home.section.setlists',      'Setlisty',                        'Naglowek sekcji setlist na Home',                NOW()),
    ('home.section.gallery',       'Galeria',                         'Naglowek sekcji galerii na Home',                NOW()),
    ('home.empty.news',            'Brak aktualnosci.',               'Empty state - aktualnosci',                      NOW()),
    ('home.empty.setlists',        'Brak setlist.',                   'Empty state - setlisty',                         NOW()),
    ('home.empty.gallery',         'Brak zdjec.',                     'Empty state - galeria',                          NOW()),
    ('tabs.home',                  'Home',                            'Etykieta zakladki Home',                         NOW()),
    ('tabs.concerts',              'Koncerty',                        'Etykieta zakladki Koncerty',                     NOW()),
    ('tabs.merch',                 'Merch',                           'Etykieta zakladki Merch',                        NOW()),
    ('tabs.tickets',               'Bilety',                          'Etykieta zakladki Bilety',                       NOW()),
    ('tabs.account',               'Konto',                           'Etykieta zakladki Konto',                        NOW()),
    ('account.title.guest',        'Witaj w BandHub',                 'Naglowek dla niezalogowanego fana',              NOW()),
    ('account.subtitle.guest',     'Zaloguj sie lub zaloz konto fana, aby kupowac bilety i merch.', 'Podtytul guest', NOW()),
    ('account.greeting',           'Witaj',                           'Powitanie zalogowanego fana',                    NOW()),
    ('account.subtitle.user',      'Jestes zalogowany jako fan BandHub.', 'Podtytul zalogowanego',                     NOW()),
    ('account.section.orders',     'Moje zamowienia merch',           'Sekcja historii zamowien w Koncie',              NOW()),
    ('account.empty.orders',       'Brak zamowien. Zajrzyj do zakladki Merch.', 'Empty state - zamowienia',          NOW()),
    ('account.button.logout',      'Wyloguj',                         'Przycisk wylogowania',                            NOW()),
    ('auth.tab.login',             'Logowanie',                       'Tab logowania w formularzu',                      NOW()),
    ('auth.tab.register',          'Rejestracja',                     'Tab rejestracji w formularzu',                    NOW()),
    ('auth.subtitle.login',        'Zaloguj sie danymi z konta fana, aby kupowac bilety i merch.', 'Podtytul login',  NOW()),
    ('auth.subtitle.register',     'Utworz konto fana w BandHub. Otrzymasz role FAN i wpadniesz prosto do appki.', 'Podtytul rejestracji', NOW()),
    ('auth.label.username',        'Nazwa uzytkownika',               'Etykieta pola username',                          NOW()),
    ('auth.label.password',        'Haslo',                           'Etykieta pola password',                          NOW()),
    ('auth.label.email',           'Email (opcjonalnie)',             'Etykieta pola email',                             NOW()),
    ('auth.label.firstName',       'Imie',                            'Etykieta pola imie',                              NOW()),
    ('auth.label.lastName',        'Nazwisko',                        'Etykieta pola nazwisko',                          NOW()),
    ('auth.placeholder.username',  'np. fan123',                      'Placeholder username',                            NOW()),
    ('auth.placeholder.password',  'minimum 8 znakow',                'Placeholder password',                            NOW()),
    ('auth.placeholder.email',     'fan@bandhub.pl',                  'Placeholder email',                               NOW()),
    ('auth.button.login',          'Zaloguj sie',                     'Przycisk login',                                  NOW()),
    ('auth.button.register',       'Zarejestruj sie',                 'Przycisk register',                               NOW()),
    ('auth.error.generic',         'Operacja nieudana',               'Domyslny komunikat bledu auth',                  NOW()),
    ('require_auth.title',         'Wymagane logowanie',              'Naglowek karty wymuszajacej logowanie',          NOW()),
    ('require_auth.message',       'Aby korzystac z tej zakladki, musisz byc zalogowanym fanem BandHub.', 'Tresc karty wymagane logowanie', NOW()),
    ('require_auth.cta',           'Przejdz do logowania',            'Przycisk CTA na karcie wymuszajacej logowanie', NOW()),
    ('merch.title',                'Sklep merch',                     'Naglowek zakladki Merch',                         NOW()),
    ('merch.subtitle',             'Oficjalny merch zespolu',         'Podtytul Merch',                                  NOW()),
    ('merch.button.cart',          'Koszyk',                          'Etykieta przycisku koszyka',                      NOW()),
    ('merch.empty',                'Brak produktow w sklepie.',       'Empty state Merch',                               NOW()),
    ('merch.label.stock',          'Stan',                            'Etykieta stanu magazynu',                         NOW()),
    ('merch.gate.message',         'Zaloguj sie, aby przegladac i kupowac merch oficjalny.', 'Auth gate Merch',     NOW()),
    ('tickets.title',              'Moje bilety',                     'Naglowek zakladki Bilety',                        NOW()),
    ('tickets.subtitle',           'Lokalna historia zakupow z endpointu /api/public/ticket-orders', 'Podtytul Bilety', NOW()),
    ('tickets.empty',              'Brak biletow. Kup pierwszy bilet z zakladki Koncerty.', 'Empty state Bilety', NOW()),
    ('tickets.gate.message',       'Zaloguj sie, aby zobaczyc swoje bilety i kody wstepu.', 'Auth gate Bilety',   NOW()),
    ('tickets.label.purchasedAt',  'Kupiono',                         'Etykieta data zakupu',                            NOW()),
    ('tickets.label.codes',        'Kody',                            'Etykieta liczby kodow biletow',                   NOW()),
    ('common.loading',             'Ladowanie...',                    'Generyczny tekst ladowania',                      NOW()),
    ('common.error',               'Wystapil blad. Sprobuj ponownie.', 'Generyczny komunikat bledu',                    NOW());
