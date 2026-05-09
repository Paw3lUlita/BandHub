-- V17__Ui_Dictionary_Auth_Hints.sql
-- Dorzuca klucze slownika UI dla walidacji formularza rejestracji w mobilce
-- (hint pod username, komunikaty bledow). Insert idempotentny - jezeli klucz
-- juz istnieje (np. po recznym dodaniu z panelu admina), pomijamy.

INSERT INTO ui_dictionary (key_name, value, description, updated_at) VALUES
    ('auth.hint.username',                   'Litery, cyfry oraz . _ - @ (bez spacji), 3-64 znakow.',                              'Hint pod polem username',          NOW()),
    ('auth.error.username.tooShort',         'Nazwa uzytkownika musi miec minimum 3 znaki.',                                       'Walidacja username minLength',     NOW()),
    ('auth.error.username.invalidCharacter', 'Nazwa uzytkownika moze zawierac tylko litery, cyfry oraz . _ - @ (bez spacji).',     'Walidacja username regex',         NOW()),
    ('auth.error.password.tooShort',         'Haslo musi miec minimum 8 znakow.',                                                  'Walidacja password minLength',     NOW())
ON CONFLICT (key_name) DO NOTHING;
