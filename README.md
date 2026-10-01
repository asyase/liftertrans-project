# LIFTERTRANS

Vali-IT grupiprojekt: veo- ja kraanateenuse ettevõtte haldusrakendus.
Spring Boot backend + Vue 3 frontend.

ADMIN haldab tellimusi, kliente ja juhte ning näeb ülevaadet töölaual ja kalendris.
DRIVER näeb talle määratud töid ning saab need alustada ja lõpetada.

## Struktuur

```
backend/    Spring Boot 4.x / Java 21 REST API  — vt backend/CLAUDE.md
frontend/   Vue 3 + Vite SPA                     — vt frontend/CLAUDE.md
docs/       Dokumentatsioon ja andmebaasiskriptid
```

Backend ja frontend on eraldi arendatavad ja käivitatavad rakendused.

## Eeldused

- Java 21
- Node.js + npm
- PostgreSQL (kohalik server)

## Kiirstart

### 1. Andmebaas

Loo andmebaas (vaikimisi `vali_it`) ja käivita skriptid **ühe psql seansi sees** (muidu tekivad tabelid valesse skeemi):

```bash
psql -U postgres -c "CREATE DATABASE vali_it;"

psql -U postgres -d vali_it \
  -f docs/database/1_reset_database.sql \
  -f docs/database/2_create.sql \
  -f docs/database/3_import.sql
```

- `1_reset_database.sql` — loob uuesti skeemi `liftertrans_project`
- `2_create.sql` — loob tabelid
- `3_import.sql` — lisab näidisandmed (kliendid, juhid, autod, tellimused jne)

### 2. Backend

Vaikeväärtused: port `5432`, andmebaas `vali_it`, kasutaja `postgres`, parool `student123` (kõik ülekirjutatavad keskkonnamuutujatega).

```bash
cd backend
./gradlew bootRun
# teisel pordil töötav PostgreSQL, nt:
DB_PORT=5433 ./gradlew bootRun
```

- Rakendus: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

- Arendusserver: http://localhost:8081 (suunab `/api` päringud backendile `http://localhost:8080`)

## Testkasutajad

Pärast `3_import.sql` käivitamist on olemas (parool kõigil `123`):

| E-post / kasutajanimi      | Roll   |
|----------------------------|--------|
| `admin@liftertrans.ee`     | ADMIN  |
| `juht@liftertrans.ee`      | DRIVER |

## Funktsionaalsus

- **Sisselogimine** — roll (ADMIN / DRIVER) määrab, mida kasutaja näeb.
- **Töölaud** (`/dashboard`) — tänaste tööde ülevaade ja loendurid (planeeritud, töös, lõpetatud).
- **Kalender** (`/calendar`) — kuu ülevaade; päevale klõpsates avaneb selle päeva tööde nimekiri.
- **Tellimused** (`/jobs`) — nimekiri, filtrid, uue tellimuse loomine ja olemasoleva muutmine.
  - Loomisel tekib tellimus staatuses DRAFT; **Kinnita** viib DRAFT → PLANNED.
  - Tavaline muutmine (PUT) ei muuda staatust.
- **Kliendid** (`/customers`) ja **Juhid** (`/drivers`) — nimekirjad, otsing, lisamine, muutmine, kustutamine.
  - Töödega juhti ei saa kustutada (409).
- **Juhi töölaud** (`/my-jobs`) — DRIVER näeb oma aktiivseid töid ning saab need alustada/lõpetada.

## REST API

Baastee `/api`. Täpsed endpointid ja skeemid: **Swagger UI** (`/swagger-ui.html`).

Olulisemad tellimuste endpointid:

| Meetod | URL | Kirjeldus |
|--------|-----|-----------|
| GET    | `/api/jobs` | Tööde nimekiri (filtritega) |
| GET    | `/api/jobs/{jobId}` | Ühe töö andmed |
| POST   | `/api/jobs` | Uue tellimuse loomine (DRAFT) |
| PUT    | `/api/jobs/{jobId}` | Tellimuse muutmine (staatust ei muuda) |
| PATCH  | `/api/jobs/{jobId}/confirm` | Kinnitamine: DRAFT → PLANNED |

**Lubatud staatused:** `DRAFT · PLANNED · IN_PROGRESS · COMPLETED · CANCELLED`.
Tegelik algus-/lõpuaeg salvestatakse automaatselt, kui juht töö alustab ja lõpetab.

## NL-to-SQL vestlus (valikuline)

`POST /api/ask` võimaldab küsida andmebaasi kohta loomulikus keeles (Spring AI + Google Gemini).
Selleks on vaja keskkonnamuutujat `GOOGLE_GENAI_API_KEY`:

```bash
GOOGLE_GENAI_API_KEY=<võti> DB_PORT=5433 ./gradlew bootRun
```

Ilma võtmeta käivitub rakendus tavapäraselt, kuid `/api/ask` tagastab vea.

## Dokumentatsioon

Täpsem info: `backend/CLAUDE.md`, `frontend/CLAUDE.md` ja kaust `docs/`.
