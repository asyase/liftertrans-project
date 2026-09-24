# LIFTERTRANS — Balsamiq vaadete ja API märkmete lõpparuanne

Kuupäev: 23.09.2026. Märkmed kirjeldavad **tulevast süsteemi**; olemasolu/puudumise info on ainult selles aruandes, mitte Balsamiqu kastides.

## Kokkuvõtlik tabel

| Vaade | Frontend rada | Vaate märkmed | API märkmete arv | Balsamiqu lisatud | Probleemid |
|---|---|---|---|---|---|
| LOGIN (LoginView.vue) | /login | Jah | 1 | Jah | — |
| ADMIN (AdminView.vue) | /dashboard | Jah | 1 | Jah | — |
| KALENDER (CalendarView.vue) | /calendar | Jah | 3 | Jah | — |
| TELLIMUSED (JobsView.vue) | /jobs | Jah | 3 | Jah | — |
| TELLIMUSE DETAILVAADE (JobDetailView.vue) | /jobs/:id | Jah | 6 | Jah | — |
| LISA / MUUDA TELLIMUST (JobCreateEditView.vue) | /jobs/new ja /jobs/:id/edit | Jah | 9 | Jah | — |
| LISA / MUUDA KAUP (CargoCreateEditView.vue) | /jobs/:jobId/cargo/new ja /jobs/:jobId/cargo/:cargoId/edit | Jah | 5 | Jah | — |
| ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST (JobCreateEditView.vue) | /jobs/new ja /jobs/:id/edit | Jah | 0 | Jah | Tellimuse vormi vana variant; API-d samad mis LISA / MUUDA TELLIMUST |
| KLIENDID vaade (CustomersView.vue) | /customers | Jah | 2 | Jah | — |
| KLIENT vaade (CustomerDetailView.vue) | /customers/:id | Jah | 3 | Jah | — |
| LISA KLIENT / MUUDA KLIENTI (CustomerCreateEditView.vue) | /customers/new ja /customers/:id/edit | Jah | 3 | Jah | — |
| AUTOD (VehiclesView.vue) | /vehicles | Jah | 2 | Jah | — |
| AUTO detailvaade (VehicleDetailView.vue) | /vehicles/:id | Jah | 2 | Jah | — |
| LISA AUTO / MUUDA AUTOT (VehicleCreateEditView.vue) | /vehicles/new ja /vehicles/:id/edit | Jah | 3 | Jah | — |
| JUHID (DriversView.vue) | /drivers | Jah | 2 | Jah | — |
| JUHT detailvaade (DriverDetailView.vue) | /drivers/:id | Jah | 3 | Jah | — |
| LISA / MUUDA JUHTI (DriverCreateEditView.vue) | /drivers/new ja /drivers/:id/edit | Jah | 3 | Jah | — |
| ALLTÖÖVÕTJAD (SubcontractorsView.vue) | /subcontractors | Jah | 2 | Jah | — |
| ALLTÖÖVÕTJA detailvaade (SubcontractorDetailView.vue) | /subcontractors/:id | Jah | 5 | Jah | — |
| LISA / MUUDA ALLTÖÖVÕTJAT (SubcontractorCreateEditView.vue) | /subcontractors/new ja /subcontractors/:id/edit | Jah | 3 | Jah | — |
| ALUSTA TÖÖD (JobStartDialog.vue) | Eraldi route puudub (modaalaken) | Jah | 1 | Jah | — |
| LÕPETA TÖÖ (JobFinishView.vue) | /my-jobs/:id/finish | Jah | 3 | Jah | — |
| DRIVER (DriverJobsView.vue) | /my-jobs | Jah | 1 | Jah | — |
| DRIVER detailne sõiduvaade (DriverJobDetailView.vue) | /my-jobs/:id | Jah | 2 | Jah | — |
| SAATELEHT (WaybillView.vue) | /jobs/:id/waybill | Jah | 2 | Jah | — |
| LISA DOKUMENT (DocumentUploadView.vue) | /my-jobs/:id/documents/new | Jah | 2 | Jah | — |

Kokku: **26 vaadet**, **26 vaate märget** (igal lehel täpselt üks), **72 API kasti** (39 unikaalset endpointi).

Kontroll Balsamiqus (23.09.2026): kõigi 26 lehe kastide tekst võrreldi failide sisuga (räsi) — kõik 98 kasti vastavad täpselt; ühelgi lehel pole enam fraase „KAVANDATUD (PUUDUV API)“, „Endpoint puudub …“, „DTO puudub“, „Vajab tiimi kinnitust“, „Backend pole veel realiseeritud“. Mockupi elemente ega kujundust ei muudetud; märkmekaste on vajadusel ainult nihutatud/suurendatud, et tekst ei jääks lõigatuks ega kataks vaadet.

## 1. Täielikult dokumenteeritud vaated

Kõik 26 Balsamiqu lehte: LOGIN, ADMIN, KALENDER, TELLIMUSED, TELLIMUSE DETAILVAADE, LISA / MUUDA TELLIMUST, LISA / MUUDA KAUP, ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST (vana variant, ainult vaate märge), KLIENDID, KLIENT, LISA / MUUDA KLIENTI, AUTOD, AUTO detailvaade, LISA / MUUDA AUTOT, JUHID, JUHT detailvaade, LISA / MUUDA JUHTI, ALLTÖÖVÕTJAD, ALLTÖÖVÕTJA detailvaade, LISA / MUUDA ALLTÖÖVÕTJAT, ALUSTA TÖÖD (modaal, route puudub), LÕPETA TÖÖ, DRIVER, DRIVER detailne sõiduvaade, SAATELEHT, LISA DOKUMENT.

## 2. Olemasolevad API-d (koodis realiseeritud, haru feature/login-v2)

| Endpoint | Kasutab | Märkus |
|---|---|---|
| `POST /api/auth/login` | LOGIN | AuthController + AuthService; vead 400 `INCORRECT_INPUT` („email: must not be blank“), 401 `INCORRECT_CREDENTIALS` („Vale e-post või parool“) |
| `GET /api/jobs` | ADMIN, KALENDER, TELLIMUSED, KLIENT, JUHT detailvaade, ALLTÖÖVÕTJA detailvaade, DRIVER | Olemas, kuid query parameetreid (date, from, to, vehicleId, driverId, customerId, subcontractorId, status) ei töödelda ja ei järjestata; DTO väli on praegu `id`, märkmetes `jobId` |

## 3. Uued kavandatud API-d (koodis veel puuduvad)

**Tööd:** `GET /api/jobs/{jobId}`, `POST /api/jobs` (loob DRAFT), `PUT /api/jobs/{jobId}` (PLANNED jääb PLANNED), `POST /api/jobs/{jobId}/confirm` (DRAFT → PLANNED), `POST /api/jobs/{jobId}/start` (PLANNED → IN_PROGRESS), `POST /api/jobs/{jobId}/finish` (IN_PROGRESS → COMPLETED), `POST /api/jobs/{jobId}/cancel` (DRAFT/PLANNED → CANCELLED, tellimuse detailvaatest), `GET /api/jobs/{jobId}/status-history`, `GET /api/jobs` query parameetrid.

**Kaup:** `GET /api/jobs/{jobId}/cargo`, `GET /api/jobs/{jobId}/cargo/{cargoId}`, `POST /api/jobs/{jobId}/cargo`, `PUT /api/jobs/{jobId}/cargo/{cargoId}`, `DELETE /api/jobs/{jobId}/cargo/{cargoId}` (kaup ainult pärast jobId olemasolu).

**Dokumendid ja saateleht:** `GET /api/jobs/{jobId}/documents`, `POST /api/jobs/{jobId}/documents`, `GET /api/jobs/{jobId}/waybill`, `POST /api/jobs/{jobId}/waybill/pdf` (genereerib saatelehe PDF-i).

**Kliendid:** `GET /api/customers` (+ `search`), `GET /api/customers/{customerId}`, `POST /api/customers`, `PUT /api/customers/{customerId}`, `DELETE /api/customers/{customerId}`.

**Autod:** `GET /api/vehicles`, `GET /api/vehicles/{vehicleId}`, `POST /api/vehicles`, `PUT /api/vehicles/{vehicleId}`, `DELETE /api/vehicles/{vehicleId}`.

**Juhid:** `GET /api/drivers`, `GET /api/drivers/{driverId}`, `POST /api/drivers`, `PUT /api/drivers/{driverId}`, `DELETE /api/drivers/{driverId}`.

**Alltöövõtjad:** `GET /api/subcontractors`, `GET /api/subcontractors/{subcontractorId}`, `POST /api/subcontractors`, `PUT /api/subcontractors/{subcontractorId}`, `DELETE /api/subcontractors/{subcontractorId}`.

Kavandatud DTO-d (tuletatud `2_create.sql`-ist ja olemasolevast nimestiilist): JobDetailDto, JobCreateRequestDto, JobCreateResponseDto, JobUpdateRequestDto, JobFinishRequestDto, JobStatusHistoryDto, CargoDto, CargoRequestDto, JobDocumentDto, JobDocumentCreateRequestDto, WaybillDto, WaybillPdfDto, CustomerDto, CustomerDetailDto, CustomerRequestDto, VehicleListDto, VehicleDetailDto, VehicleRequestDto, DriverDto, DriverRequestDto, SubcontractorDto, SubcontractorRequestDto, SubcontractorCreateResponseDto.

## 4. Äriloogika küsimused

**Lahendatud (tiimi otsused 23.09.2026, märkmetesse sisse viidud):**
- Tühistamine: tellimuse detailvaatest nupuga „Tühista tellimus“ → `POST /api/jobs/{jobId}/cancel`. Eeldus (minu ettepanek, palun kinnitage): lubatud ainult DRAFT ja PLANNED tööl; IN_PROGRESS ja COMPLETED tööd tühistada ei saa. TELLIMUSE DETAILVAADE mockupil nuppu „Tühista tellimus“ ei ole (kontrollitud mockupi PDF-ekspordist) — tiim peab selle lisama; kujundust ma ei muutnud.
- Juhi konto: DRIVER kontod (e-post ja parool) on `user` tabelis eelnevalt määratud ja seotud `user.driver_id` kaudu; juhi lisamine kontot ei loo (lisatud LISA / MUUDA JUHTI vaate märkmesse).
- Failid (ettepanek): frontend saadab faili Base64 kujul; backend salvestab selle serveri kausta `uploads/jobs/{jobId}/documents/` (dokumendid) või `uploads/jobs/{jobId}/cargo/` (kauba fotod) ning andmebaasi kirjutatakse suhteline aadress (`file_url`, `cargo_photo_url`). Spring serveerib kausta aadressilt `/uploads/**`, seega „Vaata“ avab faili otse ilma eraldi API-ta. Andmebaasi skeemi muuta pole vaja.
- Saatelehe PDF (ettepanek): `transport_document` rida luuakse tellimuse kinnitamisel (DRAFT → PLANNED); „Laadi PDF“ kutsub `POST /api/jobs/{jobId}/waybill/pdf`, mis genereerib PDF-i hetkeandmetest, salvestab `uploads/waybills/<documentNumber>.pdf` ja uuendab `generated_file_url`. Nii ei jää PDF kunagi vananenuks.
- Tüüp TRANSPORT ja staatus NEW: ei kasutata (märkmetes TRANSPORT_AND_CRANE / CRANE_ONLY ja DRAFT).

**Veel lahtised:**
1. Äriloogika vigade errorCode/message (vale staatuse üleminek, puudulik tellimus kinnitamisel, kustutamine seotud andmete korral, topelt registreerimisnumber) — märkmetes „Veateated: —“.
2. Dashboardi loendurid arvutab frontend `GET /api/jobs?date=` vastusest (Balsamiqus oli `GET /api/dashboard/summary`) — kas jääb nii?
3. Kliendi/juhi/alltöövõtja tööd ühtse `GET /api/jobs` + filtriga (customerId, driverId, subcontractorId).
4. Alltöövõtja auto lisamine: kuidas `subcontractorId` auto vormi jõuab.
5. Rollikontroll: backendis pole Spring Securityt; juht peaks nägema ainult oma töid.
6. `JobFinishRequestDto.comment` → `job_status_history.comment`.
7. „Logi välja“: kas on vaja `POST /api/logout`?
8. TELLIMUSED tabeli nupp „Kaup“ ja DRIVER detailvaate nupp „Muuda saatelehte“ — eesmärk kirjeldamata.
9. Leht „ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST“ (vana variant) — kustutada või ümber nimetada?
10. Faili suurus ja lubatud failitüübid üleslaadimisel.

## 5. Vastuolud Balsamiq / Jira / OpenAPI / andmebaas

1. OpenAPI faili (`docs/stoplight_io_openAPI.json`) ja `docs/jira-updates/` projektis pole — endpointe ei saanud nende järgi kinnitada.
2. Töö tüüp „Transport“ / `TRANSPORT` ja staatus NEW esinevad veel mockupi kujunduses; tiimi otsusel neid ei kasutata (märkmetes `TRANSPORT_AND_CRANE` / `CRANE_ONLY` ja DRAFT).
3. TELLIMUSE DETAILVAADE mockupil puudub nupp „Tühista tellimus“, kuigi tühistamine toimub sealt.
4. Login: task `POST-api-login.md` — `POST /api/login` ja `REQUIRED_FIELDS_MISSING`; kood — `POST /api/auth/login` ja `INCORRECT_INPUT`. Validaatori tekst on inglise keeles („must not be blank“), sest `ValidationMessages_et` puudub.
5. Tellimuse loomine: Balsamiqus `POST /api/v1/jobs`, mujal `/api/...` ilma versioonita (märkmetes `POST /api/jobs`).
6. `JobListDto`: kood tagastab `id`, märkmed `jobId` (DTO ja `JobsTable.vue` vajavad ümbernimetamist). `JobMapper` võtab `customer.name` (kontaktisik), mockup näitab ettevõtte nime.
7. Juhi nimi: mockupis Eesnimi + Perekonnanimi, DB-s üks väli `driver.name`.
8. Auto: mockupis mõõdud mm ja „Aktiivne/Mitteaktiivne“; DB-s meetrid ja `ACTIVE / IN_SERVICE / UNAVAILABLE / INACTIVE`.
9. Kliendi vormil puudub „Arve e-post“ (`invoice_email`), kuigi tabelis on.
10. Staatuse ajalugu: mockup mainib `created_at`, `updated_at`, `user_role_name`; DB-s `old_status`, `new_status`, `changed_at`, `changed_by`, `comment`.
11. Mockupi vanad failinimed/route'id (`/kalender`, `/minu-tood/:id/dokument`, `CalenderView.vue`, `CustomerView.vue`, `VehicleCreateView.vue`/`VehicleEditView.vue` jne) — Balsamiqu vaate märkmetes on nüüd ainult kokkulepitud ingliskeelsed route'id.
12. Veakoodid mockupis, mida koodis pole: `UNAUTHORIZED`, `ACCESS_DENIED`, `INVALID_DATE`, `CUSTOMER_NOT_FOUND`, `CUSTOMER_ALREADY_EXISTS`, `CUSTOMER_HAS_JOBS`, `BAD_REQUEST`, `FORBIDDEN` jt; 409 käsitlejat pole.
13. LÕPETA TÖÖ: mockupil väli „Tegelik tundide arv“, märge ütleb, et `actual_hours` arvutatakse automaatselt (märkmed järgivad automaatset).
14. SAATELEHT: väli „Isikukood“ DB-s puudub; `transport_document` hoiab ühte kaubakirjeldust, mockup mitut rida; route `/jobs/:id/waybill` on admini teel, kuid kasutab ka juht.

## 6. Märkmed, mida Balsamiqu ei lisatud

Puuduvad — kõik 26 vaate märget ja 72 API kasti on Balsamiqus. Lehel „ALLTÖÖVÕTJAD: LISA / MUUDA TELLIMUST“ on teadlikult ainult vaate märge (API kastid oleks LISA / MUUDA TELLIMUST lehe duplikaadid).

## 7. Failide asukohad

- Märkmefailid: `docs/balsamic/notes/<ComponentName>-markmed.md` (26 faili), nt `LoginView-markmed.md`, `JobCreateEditView-markmed.md`, `JobCreateEditView-vana-alltoovotja-variant-markmed.md`.
- See aruanne: `docs/balsamic/notes/markmete-lopparuanne.md`.
- Struktuuri alus: `docs/balsamic/notes/balsamiq-markmete-struktuur.md` (muutmata).
- Java/Vue koodi, andmebaasi skeemi ega Jirat ei muudetud.
