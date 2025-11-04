-- --------------------------------------------------------------------------------
-- 1. Adatok torlese minden inditasnal, tiszta indulas vegett
-- --------------------------------------------------------------------------------

DELETE FROM user_roles;
DELETE FROM roles;
DELETE FROM users;
DELETE FROM invoices;

-- --------------------------------------------------------------------------------
-- 2. Szerepkorok letrehozasa
-- --------------------------------------------------------------------------------

INSERT INTO roles (id, name, description) VALUES
                                              (1, 'ADMIN', 'Teljes adminisztrációs jogkör.'),
                                              (2, 'BOOKKEEPER', 'Számlák létrehozása, szerkesztése és megtekintése.'),
                                              (3, 'USER', 'Csak számlák és műszerfal megtekintése.');

-- --------------------------------------------------------------------------------
-- 3. Letrehozni felhasznalokat, eleve a hashelt jelszokat kell beallitani
-- user - testUser1pass
-- konyvelo - testBk1pass
-- admin - adminpass1
-- --------------------------------------------------------------------------------

INSERT INTO users (id, name, username, password, failed_login_attempts) VALUES
                                                                            (1, 'Fő Admin', 'admin', '$2a$10$ovnCI81XUnypNiKdVSV/fOga5FNHSxbTDbLyU4A44vYcHsTqUsAIG', 0),
                                                                            (2, 'Pénzügyi Könyvelő', 'konyvelo', '$2a$10$k03kyqgSMD6UrdYCsO5KLu/wUy.1Nfe.IKQBQ4EzWNmFX6FZEpduW', 0),
                                                                            (3, 'Teszt Felhasználó', 'user', '$2a$10$/4hX/02rlMBwrQaEkZDdj.FENZDKRLBTnK4QVKd.SE/3aLMz9gIzm', 0);

-- --------------------------------------------------------------------------------
-- 4. Kapcsolotabla feltoltese a felhasznalok es szerepkoreik kozott
-- --------------------------------------------------------------------------------

INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1),
                                              (2, 2),
                                              (3, 3);

-- --------------------------------------------------------------------------------
-- 5. nehany szamla letrehozasa
-- --------------------------------------------------------------------------------

INSERT INTO invoices (id, customer_name, issue_date, due_date, item_name, comment, price) VALUES
                                                                                              (1, 'Bíráló Tanár Zrt.', '2025-10-05', '2025-11-04', 'Rendszerfejlesztés', 'Adminisztrációs és számlakezelő modul', 750000.00),
                                                                                              (2, 'Teszt Ügyfél Kft.', '2025-10-18', '2025-11-17', 'Licenc díj', 'Éves szoftverhasználati licenc díj', 25000.00);

-- 6. beallitasa az ID-nak hogy ne eggyel kezdje megint ha ujat adunk hozza ezzel exceptiont elkerulve
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

ALTER TABLE roles ALTER COLUMN id RESTART WITH 4;

ALTER TABLE invoices ALTER COLUMN id RESTART WITH 3;