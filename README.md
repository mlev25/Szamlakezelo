Számlakezelő Rendszer
=====================

Ez a projekt egy webalkalmazás számlák és felhasználók kezelésére. A rendszer a **Spring Boot** és a **Vue 3** nyújtotta funkcionalitásokat ötvözi. Kiemelt figyelmet kapott a szerepköralapú hozzáférés-vezérlés.

**Technológiai áttekintés:**

Backend: Java Spring Boot - JDK 21

Frontend: Vue 3 (Composition API) 

Adatbázis: H2 in-memory (fejlesztési, tesztelési célú adatbázis), eredetileg Supabase PostgreSQL**

**Indítási útmutató:**
----------------------

A projekt futtatásához mind a **Backend (API)**, mind a **Frontend (Weboldal)** elindítása szükséges.

### 1\. Backend Indítása (Spring Boot)

#### Előfeltételek

*   **Java Development Kit (JDK) 21** telepítve.
    
*   Egy Java fejlesztői környezet (IDE), pl. **IntelliJ IDEA**.
    

#### Lépések

1.  **Megnyitás:** Nyissa meg a backend nevű könyvtárat az IDE-ben.
    
2.  **Konfiguráció Ellenőrzése:** Ellenőrizze az src/main/resources/application.properties fájlt:
    
    *   **Port:** Győződjön meg róla, hogy a port server.port=8080 értékre van állítva.
        
    *   **Annotation processing**: Inditásnál rákérdez, hogy engedélyezzük-e, de jobb előtte bekapcsolni File→Settings→Build,Execution,Deployment→Annotation processors→Enable annotation processing , ha még nincs beállítva.
        
3.  **Futtatás:** Keresse meg a fő Spring Boot alkalmazásfájlt (pl. SzamlakezeloBackendApplication.java) és indítsa el a main metódust, vagy a jobb oldali Maven fülre rákattintva SzamlakezeloBackend→Plugins→spring-boot→spring-boot:run segítségével, vagy alternatívaként Git Bash-ből a backend könyvtáron belül az mvn spring-boot:run parancsot kiadva (ha Maven-t használ).
    
4.  **Ellenőrzés:** Az API a **http://localhost:8080** címen érhető el. A H2 beágyazott adatbázis is megtekinthető a **http://localhost:8080/h2-console** címen, **"sa"** felhasználónév, "" (üres) jelszó, **jdbc:h2:mem:szamlakezelo** JDBC URL adatokat megadva.
    
5.  **Hiba esetén**: Előfordulhat hogy a port nem elérhető, így át kell írni az application.properties fájlban a server.port értékét. Ha ez szükséges, akkor frontend oldalon is az src/utils/axios.js fájlban a baseURL-ben a portot át kell írni az új értékre: “http://localhost:8080/api“ → ”http://localhost:ujport/api” .
    

### 2\. Frontend Indítása (Vue + Vite)

#### Előfeltételek

*   **Node.js** telepítve (ajánlott az LTS verzió).
    

#### Lépések

1.  **Navigálás:** Nyisson meg egy terminált (cmd/powershell) a **frontend** alkönyvtárban, vagy ha van VSC kódszerkesztő telepítve, ott is lehet terminált nyitni.
    
2.  npm install
    
3.  npm run dev
    
4.  **Megnyitás:** A webes felület a terminálban jelzett címen lesz elérhető (tipikusan **http://localhost:5173/**).
    
5.  Ha más porton érhető el, feltétlenül hozzá kell adni az **src/main/java/com/szamlakezelo/backend/config/SecurityConfig.java** fájlban található corsConfigurationSource metódus "configuration.setAllowedOrigins(List.of(…)" sorhoz a List.of() -ba a "http://localhost:jelenlegiport" linket, hogy a frontendről ne tiltsa le a Spring Security a bejövő kéréseket.
    

Kezdő Hitelesítő Adatok a teszteléshez:
---------------------------------------

A teszteléshez használható, előre feltöltött felhasználói fiókok:

**Felhasználónév Jelszó Szerepkör**

admin adminpass1 ADMIN

konyvelo testBk1pass BOOKKEEPER

user testUser1pass USER

Főbb Funkcionalitások
---------------------

### I. Hitelesítés és Biztonság

*   **JWT Biztonság:** Minden API kérés token alapú hitelesítést igényel.
    
*   **CAPTCHA:** Képi CAPTCHA védelem a többszöri (3x) sikertelen bejelentkezési kísérlet után.
    

### II. Szerepköralapú Hozzáférés-vezérlés (RBAC)

*   **Szerepkörök:** ADMIN, BOOKKEEPER, USER.
    
*   **Dinamikus Navigáció:** A menüpontok (pl. Admin Panel) a felhasználói szerepkörök alapján jelennek meg.
    
*   **Backend Autorizáció:** Szigorú jogosultság-ellenőrzés minden API végponton.
    

### III. Admin Panel (ADMIN Szerepkör)

*   **Felhasználók Kezelése:** Listázás, törlés.
    
*   **Dinamikus Szerepkör Szerkesztés:** A szerepkörök közvetlenül, szerkesztési módban módosíthatók a táblázatban.
    
*   **Öntörlés Tilalma:** Az adminisztrátor saját magát nem tudja törölni.
    

### IV. Számla Modul

*   **Számlák Létrehozása és Listázása.**
    
*   **Validáció:** Kliens és szerver oldali validáció a pénzügyi adatok integritásának biztosítására (pl. árkorlátok, logikus dátumkezelés).




