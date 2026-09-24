# LIFTERTRANS — Balsamiq märkmete lõpparuanne (best practice uuendus)

Kuupäev: 24.09.2026. Märkmed kirjeldavad **tulevast süsteemi**. Realiseerimise seis on ainult selles aruandes, mitte Balsamiqu kastides.

## Kokkuvõte

| Vaade | Frontend rada | Vaate märkmed | API märkmete arv | Balsamiqu lisatud |
|---|---|---|---|---|
| LOGIN (LoginView.vue) | /login | Jah | 1 | Jah |
| ADMIN (AdminView.vue) | /dashboard | Jah | 2 | Jah |
| KALENDER (CalendarView.vue) | /calendar | Jah | 3 | Jah |
| TELLIMUSED (JobsView.vue) | /jobs | Jah | 3 | Jah |
| TELLIMUSE DETAILVAADE (JobDetailView.vue) | /jobs/:id | Jah | 6 | Jah |
| LISA / MUUDA TELLIMUST (JobCreateEditView.vue) | /jobs/new, /jobs/:id/edit | Jah | 9 | Jah |
| LISA / MUUDA KAUP (CargoCreateEditView.vue) | /jobs/:jobId/cargo/new, /jobs/:jobId/cargo/:cargoId/edit | Jah | 5 | Jah |
| ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST (DEPRECATED) | — | Jah | 0 | Jah |
| KLIENDID (CustomersView.vue) | /customers | Jah | 2 | Jah |
| KLIENT (CustomerDetailView.vue) | /customers/:id | Jah | 3 | Jah |
| LISA / MUUDA KLIENTI (CustomerCreateEditView.vue) | /customers/new, /customers/:id/edit | Jah | 3 | Jah |
| AUTOD (VehiclesView.vue) | /vehicles | Jah | 2 | Jah |
| AUTO detailvaade (VehicleDetailView.vue) | /vehicles/:id | Jah | 2 | Jah |
| LISA / MUUDA AUTOT (VehicleCreateEditView.vue) | /vehicles/new, /vehicles/:id/edit | Jah | 3 | Jah |
| JUHID (DriversView.vue) | /drivers | Jah | 2 | Jah |
| JUHT detailvaade (DriverDetailView.vue) | /drivers/:id | Jah | 3 | Jah |
| LISA / MUUDA JUHTI (DriverCreateEditView.vue) | /drivers/new, /drivers/:id/edit | Jah | 3 | Jah |
| ALLTÖÖVÕTJAD (SubcontractorsView.vue) | /subcontractors | Jah | 2 | Jah |
| ALLTÖÖVÕTJA detailvaade (SubcontractorDetailView.vue) | /subcontractors/:id | Jah | 5 | Jah |
| LISA / MUUDA ALLTÖÖVÕTJAT (SubcontractorCreateEditView.vue) | /subcontractors/new, /subcontractors/:id/edit | Jah | 3 | Jah |
| ALUSTA TÖÖD (JobStartDialog.vue) | modaalaken, route puudub | Jah | 1 | Jah |
| LÕPETA TÖÖ (JobFinishView.vue) | /my-jobs/:id/finish | Jah | 3 | Jah |
| DRIVER (DriverJobsView.vue) | /my-jobs | Jah | 1 | Jah |
| DRIVER detailne sõiduvaade (DriverJobDetailView.vue) | /my-jobs/:id | Jah | 2 | Jah |
| SAATELEHT (WaybillView.vue) | /jobs/:id/waybill | Jah | 2 | Jah |
| LISA DOKUMENT (DocumentUploadView.vue) | /my-jobs/:id/documents/new | Jah | 2 | Jah |

Kokku 26 vaate märget ja 73 API kasti (40 eri endpointi). Kontrollisin kõik 26 lehte Balsamiqus üle: iga kast vastab täpselt failile, igal lehel on üks vaate märge, auditi fraase ja vanu eestikeelseid route'e märkmetes pole. Mockupi kujundust ei muudetud; kaste on vajadusel ainult suurendatud, et tekst ei jääks lõigatuks.

## 1. UUENDATUD

Selles voorus muudeti 51 olemasolevat kasti ja lisati 1 uus (kokku 52 muudatust).

**Vaate märkmed**
- **LOGIN:** sessionStorage'isse salvestatakse accessToken; kõik päringud saadavad selle päises `Authorization: Bearer`.
- **ADMIN:** loendurid tulevad `GET /api/dashboard/summary` vastusest, tabel `GET /api/jobs?date=`. „Logi välja“ kustutab tokeni frontendis (API kutset pole).
- **TELLIMUSED:** nupp „Kaup“ (ühemõttelisem nimi „Vaata kaupa“) → `/jobs/:id` vahekaart Kaup.
- **LISA / MUUDA KAUP, LISA DOKUMENT:** failide whitelist ja 10 MB piirang (kontroll frontendis ja backendis).
- **LISA / MUUDA AUTOT:** `/vehicles/new?subcontractorId=:id` lisab päringusse subcontractorId; ilma parameetrita → oma auto.
- **ALLTÖÖVÕTJA detailvaade:** „+ Lisa auto“ → `/vehicles/new?subcontractorId=:id` (sama auto vorm).
- **DRIVER:** juhi tööd valib backend accessTokeni järgi, mitte sessionStorage'i driverId järgi.
- **DRIVER detailne sõiduvaade:** juht avab ainult oma töö (muidu 403). Soovitus asendada nupp „Muuda saatelehte“ nupuga „Lisa saatelehe foto“ (WAYBILL_PHOTO).
- **ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST:** märgitud **DEPRECATED — mitte realiseerida**.
- Mockupi enda abimärkmetes (TELLIMUSE DETAILVAADE 7-66, DRIVER 7-121) asendati staatus NEW staatusega DRAFT.

**API märkmed**
- **`POST /api/auth/login`:** vastusesse lisati `accessToken` (JWT); lisainfo kirjeldab, kuidas backend sellest rolli ja driverId määrab.
- **`GET /api/jobs`** (7 vaates): DRIVER rolliga kasutajale tagastatakse alati ainult tema tööd; driverId võetakse autentimisest, mitte query parameetrist.
- **403 `ACCESS_DENIED`** lisati kõigile töö API-dele, mida juht kasutab:
  - `GET /api/jobs/{jobId}`, `/cargo`, `/documents`, `/waybill`;
  - `POST /api/jobs/{jobId}/start`, `/finish`, `/documents`, `/waybill/pdf`.
- **409 `INVALID_STATUS_TRANSITION`:** confirm (ainult DRAFT), start (ainult PLANNED), finish (ainult IN_PROGRESS), cancel (ainult DRAFT/PLANNED).
- **409 `RESOURCE_IN_USE`:** kliendi, auto, juhi ja alltöövõtja kustutamine, kui temaga on seotud töid (alltöövõtja puhul ka autosid).
- **409 `DUPLICATE_RESOURCE`:** auto lisamine ja muutmine, kui sama registreerimisnumber on juba olemas.
- **400 `INCORRECT_INPUT`:**
  - SUBCONTRACTED töö auto peab kuuluma valitud alltöövõtjale (POST ja PUT `/api/jobs`);
  - INTERNAL töö kinnitamisel peab juht olema määratud;
  - lubamatu failitüüp või -suurus.
- **INTERNAL/SUBCONTRACTED reegel** on `POST /api/jobs` lisainfos täpsustatud: INTERNAL → oma auto; SUBCONTRACTED → subcontractorId kohustuslik, driverId = null, auto kuulub samale alltöövõtjale.
- **Töö lõpetamise kommentaar** läheb COMPLETED staatuse ajaloo reale (`job_status_history.comment`); `job.notes` ei muutu.
- **`GET /api/jobs/{jobId}/documents`:** väli `jobDocumentId` → `documentId`.

## 2. UUS API

Selles voorus lisandus:
- **`GET /api/dashboard/summary`** → `DashboardSummaryDto` (`todayJobs`, `planned`, `inProgress`, `completed`). Koondarvud arvutab backend; ainult ADMIN.

Varem projekteeritud ja nüüd best practice reeglitega täiendatud:
- **Tööd:** `POST /api/jobs`, `PUT /api/jobs/{jobId}`, `GET /api/jobs/{jobId}`, `POST .../confirm`, `.../start`, `.../finish`, `.../cancel`, `GET .../status-history`.
- **Kaup:** `GET /api/jobs/{jobId}/cargo`, `GET .../cargo/{cargoId}`, `POST .../cargo`, `PUT .../cargo/{cargoId}`, `DELETE .../cargo/{cargoId}`.
- **Dokumendid ja saateleht:** `GET` ja `POST /api/jobs/{jobId}/documents`, `GET .../waybill`, `POST .../waybill/pdf`.
- **Kliendid, autod, juhid, alltöövõtjad:** igaühel `GET` (nimekiri), `GET` (üks), `POST`, `PUT` ja `DELETE`.

Koodis on praegu olemas ainult `POST /api/auth/login` ja `GET /api/jobs` (ilma filtriteta).

**Job filtering:** projekt kasutab ühtset `GET /api/jobs` endpointi filtritega (`date`, `from`, `to`, `vehicleId`, `driverId`, `customerId`, `subcontractorId`, `status`). Nested endpointe (`/api/customers/{id}/jobs` jms) märkmetes ei ole, seega dubleerimist ei esine. Balsamiqus kunagi pakutud `/api/customers/{id}/jobs` asendati ühtse filtriga.

## 3. BEST PRACTICE PARANDUSED (koodis teha)

1. **Autentimine ja õigused:** backendis pole praegu Spring Securityt ega tokenit. `AuthResponseDto` tagastab ainult `userId`, `email`, `roleName` ja `driverId`, seega backend ei tea järgmistes päringutes, kes kasutaja on.
   - Lisada Spring Security ja JWT; `AuthResponseDto`-sse väli `accessToken`.
   - Kõigi admin API-de kaitse (ainult ADMIN) ja juhi omanikukontroll: `job.driver_id` = tokeni driverId.
2. **Error infrastruktuur:** errorCode'id on praegu vabad stringid ja 409 käsitlejat pole.
   - Lisada error enum koodidega `INCORRECT_INPUT`, `INCORRECT_CREDENTIALS`, `PRIMARY_KEY_NOT_FOUND`, `ACCESS_DENIED`, `INVALID_STATUS_TRANSITION`, `RESOURCE_IN_USE`, `DUPLICATE_RESOURCE`.
   - Lisada `ConflictException` (409) ja selle handler `RestExceptionHandler`-isse; ForbiddenException (403) kasutada ainult õiguste puudumisel.
3. **`GET /api/jobs`:** realiseerida query filtrid ja järjestus `plannedStartTime` järgi. `JobListDto` väli `id` → `jobId` (muudab ka `JobsTable.vue`). `JobMapper` võtab praegu kontaktisiku nime, tabel eeldab ettevõtte nime.
4. **Logout:** mudel on stateless JWT, seega backendi logout endpointi ei ole vaja; frontend eemaldab tokeni ja kasutaja andmed ning suunab `/login`. Kui hiljem lisatakse refresh token või HttpOnly cookie, tuleb lisada `POST /api/auth/logout`.
5. **Failid:** whitelist (kauba foto: image/jpeg, image/png; dokumendid: application/pdf, image/jpeg, image/png), max 10 MB. Backend kontrollib sisu tüüpi ja suurust, mitte ainult laiendit; salvestus kausta `uploads/…` ja serveerimine aadressilt `/uploads/**`.
6. **Mockupi nupud (tiim muudab kujunduses):**
   - „Kaup“ → „Vaata kaupa“;
   - DRIVER detailvaate „Muuda saatelehte“ → „Lisa saatelehe foto“;
   - TELLIMUSE DETAILVAADE-sse lisada nupp „Tühista tellimus“;
   - ALLTÖÖVÕTJA detailvaatesse „+ Lisa auto“, kui seda seal veel pole;
   - leht „ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST“ mockupist eemaldada.
7. **Mockupi UI tekstid:** mõnes vaates on tüüp „Transport“ ja staatuse silt „Uus“. Märkmetes kasutatakse `TRANSPORT_AND_CRANE` / `CRANE_ONLY` ja DRAFT (UI silt „Uus“ = DRAFT).

## 4. PÄRISELT LAHTISED KÜSIMUSED

1. **Tühistamise piir:** märkmetes saab tühistada ainult DRAFT ja PLANNED tellimust. Kas IN_PROGRESS tööd peab erandjuhul (nt rike teel) saama tühistada?
2. **Kas juht näeb DRAFT töid?** DRAFT tellimusele võib olla juht juba määratud, kuid tellimus pole kinnitatud. Juhi töölaua staatuse siltide hulgas on praegu ka „Uus“ (DRAFT); äriloogika järgi peaks juht tõenäoliselt nägema alles PLANNED ja hilisemaid töid.
3. **Saatelehe isikukood:** SAATELEHT mockupil on väli „Isikukood“, andmebaasis sellist välja pole. Kas see on vajalik? Isikukoodi hoidmine on isikuandmete küsimus.
4. **Mitu kaubarida saatelehel:** `transport_document` hoiab ühte `cargo_description` / `cargo_weight_kg`, mockup näitab mitut kaubarida. Märkmetes võetakse kaubaread tabelist `cargo`; kas `transport_document` kaubaväljad jäävad kasutamata?

## Failide asukohad

- `docs/balsamic/notes/<ComponentName>-markmed.md` — 26 märkmefaili (kõik uuendatud).
- `docs/balsamic/notes/markmete-lopparuanne.md` — see aruanne.
- `docs/balsamic/notes/balsamiq-markmete-struktuur.md` — muutmata.
- Java/Vue koodi, andmebaasi skeemi ega Jirat ei muudetud.
