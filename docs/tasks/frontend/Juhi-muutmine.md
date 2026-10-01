# Juhi andmete muutmine

**Vaade:** `DriverCreateEditView.vue`, route `/drivers/:id/edit` (`driver-edit`)

**Roll:** ADMIN

**Vaste balsamic mockupis:** "LIFTERTRANS mock-up (3).pdf", leht "LISA / MUUDA JUHTI", märkmed `docs/balsamic/notes/DriverCreateEditView-markmed.md`

**Backend task:** `docs/tasks/backend/PUT-api-drivers-driverId.md`

## Kasutajavoog

ADMIN vajutab juhtide nimekirjas (`/drivers`) juhi real nuppu "Muuda" ja jõuab vaatele `/drivers/:id/edit`. Lehel on pealkiri "Muuda juhti" ja vorm, mis eeltäidetakse juhi andmetega `GET /api/drivers/{driverId}` abil. ADMIN muudab väljad ja vajutab "Salvesta" — frontend valideerib vormi, saadab `PUT /api/drivers/{driverId}` päringu ja õnnestumise korral suunab tagasi `/drivers`, kus muudetud andmed on nähtavad. "Tühista" viib tagasi `/drivers` ilma API kutseta.

> Sama vaadet `DriverCreateEditView.vue` kasutatakse ka juhi lisamiseks (`/drivers/new`, `POST /api/drivers`) — see on eraldi task. Muutmise režiimi tunneb vaade ära route parameetri `id` järgi (`this.$route.params.id` olemas → muutmine). Ehita komponent nii, et lisamise saaks hiljem lihtsalt juurde lisada.

## Kasutajaliidese elemendid

| Element | Tüüp | Kirjeldus/käitumine |
|---|---|---|
| "Muuda juhti" | Pealkiri | Vormi pealkiri. |
| Veateade | Alert (`alert alert-danger`) | Kuvatakse ainult siis, kui `errorMessage` ei ole tühi. |
| Eesnimi | Tekstisisend | Kohustuslik. Eeltäidetakse. |
| Perekonnanimi | Tekstisisend | Kohustuslik. Eeltäidetakse. |
| Telefon | Tekstisisend (`type="tel"`) | Kohustuslik. Eeltäidetakse. |
| E-post | Tekstisisend (`type="email"`) | Valikuline. Eeltäidetakse (`null` → tühi väli). |
| Staatus | Raadionupud / select: Aktiivne / Mitteaktiivne | Eeltäidetakse väljast `active` (boolean). |
| "Salvesta" | Nupp | Valideerib ja saadab `PUT` päringu. Päringu ajal `disabled`. |
| "Tühista" | Nupp | Tagasi `/drivers`, API kutset ei tehta. |

## Käitumine ja valideerimine

1. Lehe avamisel kontrollitakse rolli (`SessionStorageService.userIsAdmin()`) — see on vaates juba olemas. Mitte-ADMIN → `navigateToNotAuthorizedView()` ja andmeid ei laeta.
2. Võetakse `driverId` route'ist (`this.$route.params.id`) ja saadetakse `GET /api/drivers/{driverId}`.
3. **Nime jagamine:** backend tagastab ühe välja `name` (andmebaasis on ainult `driver.name`). Vormi eeltäitmisel jagatakse see **esimese tühiku kohalt**: kõik enne esimest tühikut → eesnimi, ülejäänu → perekonnanimi (nt "Mari Ann Kask" → "Mari" + "Ann Kask"). Kui tühikut pole, läheb kogu nimi eesnimeks ja perekonnanimi jääb tühjaks.
4. GET vea korral:
   - 404 `PRIMARY_KEY_NOT_FOUND` → kuvatakse backendi `message`, vormi ei saa salvestada (või suunatakse tagasi `/drivers` — täpsusta enne implementeerimist);
   - muu viga → `NavigationService.navigateToErrorView()`.
5. "Salvesta" vajutamisel tühjendatakse eelmine veateade ja kontrollitakse:
   - eesnimi tühi → "Eesnimi on kohustuslik";
   - perekonnanimi tühi → "Perekonnanimi on kohustuslik";
   - telefon tühi → "Telefon on kohustuslik".
   Vea korral päringut ei saadeta.
6. Saatmisel liidetakse eesnimi ja perekonnanimi üheks väljaks `name` (`firstName.trim() + ' ' + lastName.trim()`).
7. Tühi e-post saadetakse `null`-ina (mitte tühja stringina), et backendi `@Email` valideerimine ei ebaõnnestuks.
8. PUT õnnestumise korral (200) → `this.$router.push('/drivers')`.
9. PUT vea korral:
   - 400 `INCORRECT_INPUT` → kuvatakse backendi `message`;
   - 404 `PRIMARY_KEY_NOT_FOUND` → kuvatakse backendi `message` (juht on vahepeal kustutatud);
   - muu viga → `NavigationService.navigateToErrorView()`.

## API kutsed

### `GET /api/drivers/{driverId}`

`DriverDto.java` — response (200):
```json
{
  "driverId": 1,
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}
```

| Status code | errorCode | message | Frontend käitumine |
|---|---|---|---|
| 404 | `PRIMARY_KEY_NOT_FOUND` | "Ei leidnud primary keyd 'driverId' väärtusega: 99" | Kuvatakse `message` alert'is. |
| muu | — | — | `/error` vaatele. |

### `PUT /api/drivers/{driverId}`

`DriverRequestDto.java` — request body:
```json
{
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}
```

Response (200): NONE

| Status code | errorCode | message | Frontend käitumine |
|---|---|---|---|
| 400 | `INCORRECT_INPUT` | "name: must not be blank" | Kuvatakse `message` alert'is. Frontendi valideerimine peaks selle ära hoidma. |
| 404 | `PRIMARY_KEY_NOT_FOUND` | "Ei leidnud primary keyd 'driverId' väärtusega: 99" | Kuvatakse `message` alert'is. |
| muu | — | — | `/error` vaatele. |

> **Märkus:** 400 vea `message` tekst tuleb Springi valideerimise vaiketeatest ja võib sõltuda keelest. Frontend ei tohiks selle täpsele sõnastusele toetuda.

## Komponendid ja failistruktuur

- `frontend/src/views/DriverCreateEditView.vue` — **on olemas**, aga sisaldab ainult rollikontrolli. Lisa `data()` (vormi väljad `firstName`, `lastName`, `phone`, `email`, `active`, lisaks `errorMessage`, `isLoading`), meetodid ja template.
- `frontend/src/services/DriverService.js` — **on olemas** (`getDriversRequest`). Lisa meetodid:
  - `getDriverRequest(driverId)` → `axios.get('/api/drivers/' + driverId)`;
  - `putDriverRequest(driverId, driverRequestDto)` → `axios.put('/api/drivers/' + driverId, driverRequestDto)`.
- `frontend/src/router/index.js` — rada `/drivers/:id/edit` (`driver-edit`) **on olemas**.
- `frontend/src/views/DriversView.vue` — nupp "Muuda" (`goToEditDriver`) **on olemas**.
- Stiil: Options API vastavalt `docs/frontend/vue-komponendi-struktuur.md`-le — `.then()/.catch()/.finally()` ahel, vastus ja viga eraldi meetodites (nt `handleGetDriverResponse`, `handleGetDriverError`, `handleUpdateDriverResponse`, `handleUpdateDriverError`).

## Vastuvõtu kriteeriumid

- [ ] `/drivers/1/edit` lehel on pealkiri "Muuda juhti" ning väljad on eeltäidetud juhi andmetega.
- [ ] Nimi "Mart Tamm" jagatakse väljadeks eesnimi "Mart" ja perekonnanimi "Tamm".
- [ ] Mitte-ADMIN kasutaja suunatakse "õigused puuduvad" lehele ja andmeid ei laeta.
- [ ] Olematu juhi (`/drivers/99/edit`) korral kuvatakse veateade.
- [ ] Tühja kohustusliku välja korral kuvatakse veateade ja päringut ei saadeta.
- [ ] Salvestamisel saadetakse `PUT /api/drivers/{driverId}`, kus `name` = eesnimi + tühik + perekonnanimi ja `active` on boolean.
- [ ] Tühi e-post saadetakse `null`-ina.
- [ ] Õnnestumise järel suunatakse `/drivers` ja muudetud andmed on nimekirjas nähtavad.
- [ ] Staatuse muutmine "Mitteaktiivne"-ks kuvatakse nimekirjas "Mitteaktiivne".
- [ ] "Tühista" viib `/drivers` ilma päringuta.
- [ ] Päringu ajal on "Salvesta" nupp keelatud.
