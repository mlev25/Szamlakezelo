-- --------------------------------------------------------------------------------
-- 1. ADATOK TÖRLÉSE (Tiszta Lap Induláshoz)
-- Minden indításkor memóriában fut a H2, de a H2 beágyazott adatbázis megjegyzi a kapcsolatokat,
-- ezért a törlést fordított sorrendben kell végezni a külföldi kulcsok miatt.
-- --------------------------------------------------------------------------------

DELETE FROM user_roles;
DELETE FROM roles;
DELETE FROM users;
DELETE FROM invoices;

-- --------------------------------------------------------------------------------
-- 2. SZEREPKÖRÖK LÉTREHOZÁSA (ROLE)
-- A Role tábla name oszlopa string enumerációt tárol: 'ROLE_ADMINISTRATOR', 'ROLE_BOOKKEEPER', 'ROLE_USER'.
-- --------------------------------------------------------------------------------

INSERT INTO roles (id, name, description) VALUES
                                              (1, 'ADMIN', 'Teljes adminisztrációs jogkör.'),
                                              (2, 'BOOKKEEPER', 'Számlák létrehozása, szerkesztése és megtekintése.'),
                                              (3, 'USER', 'Csak számlák és műszerfal megtekintése.');

-- --------------------------------------------------------------------------------
-- 3. FELHASZNÁLÓK LÉTREHOZÁSA (USER)
-- Minden felhasználóhoz meg kell adni a kötelező mezőket.
-- JELSZÓ: Mindegyik felhasználó jelszava "password123", BCrypt-tel hashelve: $2a$10$T8T/rO9s/o7pC7d5z2Q3R.D7i0Fh6t1yX0Y5zX.s3X/X2xX3J2X1X2 (Csak teszteléshez!)
-- --------------------------------------------------------------------------------

INSERT INTO users (id, name, username, password, failed_login_attempts) VALUES
                                                                            (1, 'Fő Admin', 'admin', '$2a$10$ovnCI81XUnypNiKdVSV/fOga5FNHSxbTDbLyU4A44vYcHsTqUsAIG', 0),
                                                                            (2, 'Pénzügyi Könyvelő', 'konyvelo', '$2a$10$k03kyqgSMD6UrdYCsO5KLu/wUy.1Nfe.IKQBQ4EzWNmFX6FZEpduW', 0),
                                                                            (3, 'Teszt Felhasználó', 'user', '$2a$10$/4hX/02rlMBwrQaEkZDdj.FENZDKRLBTnK4QVKd.SE/3aLMz9gIzm', 0);

-- --------------------------------------------------------------------------------
-- 4. KAPCSOLÓTÁBLA (user_roles) FELTÖLTÉSE (ManyToMany)
-- Felhasználó 1 (admin) -> ROLE_ADMINISTRATOR (1)
-- Felhasználó 2 (konyvelo) -> ROLE_BOOKKEEPER (2)
-- Felhasználó 3 (user) -> ROLE_USER (3)
-- --------------------------------------------------------------------------------

INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1),
                                              (2, 2),
                                              (3, 3);

-- --------------------------------------------------------------------------------
-- 5. SZÁMLÁK LÉTREHOZÁSA (INVOICE)
-- --------------------------------------------------------------------------------

INSERT INTO invoices (id, customer_name, issue_date, due_date, item_name, comment, price) VALUES
                                                                                              (1, 'Bíráló Tanár Zrt.', '2025-10-05', '2025-11-04', 'Rendszerfejlesztés', 'Adminisztrációs és számlakezelő modul', 750000.00),
                                                                                              (2, 'Teszt Ügyfél Kft.', '2025-10-18', '2025-11-17', 'Licenc díj', 'Éves szoftverhasználati licenc díj', 25000.00);

ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

-- Állítsa be a szerepkörök tábla ID számlálóját (3+1=4)
ALTER TABLE roles ALTER COLUMN id RESTART WITH 4;

-- Állítsa be a számlák tábla ID számlálóját (2+1=3)
ALTER TABLE invoices ALTER COLUMN id RESTART WITH 3;