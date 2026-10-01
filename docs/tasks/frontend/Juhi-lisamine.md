# Uue juhi lisamine

**Vaade:** `DriverCreateEditView.vue`, route `/drivers/new` (`driver-create`)

**Roll:** ADMIN

**Vaste balsamic mockupis:** "LIFTERTRANS mock-up (3).pdf", leht "LISA / MUUDA JUHTI", märkmed `docs/balsamic/notes/DriverCreateEditView-markmed.md`

**Backend task:** `docs/tasks/backend/POST-api-drivers.md`

**Seotud task:** `docs/tasks/frontend/Juhi-muutmine.md` — sama vaade `/drivers/:id/edit` režiimis.

## Kasutajavoog

ADMIN vajutab juhtide nimekirjas (`/drivers`) nuppu "+ Lisa juht" ja jõuab vaatele `/drivers/new`. Lehel on pealkiri "Lisa juht" ja vorm juhi andmetega. ADMIN täidab väljad ja vajutab "Salvesta" — frontend valideerib vormi, saadab `POST /api/drivers` päringu ja õnnestumise korral suunab tagasi `/drivers`, kus uus juht on nimekirjas. "Tühista" viib tagasi `/drivers` ilma API kutseta.

> Selles taskis tehakse ainult **lisamine**. Muutmine on eraldi task (`Juhi-muutmine.md`), kuid vaade on sama — ehita komponent nii, et muutmise saaks lihtsalt juurde lisada (režiimi tunneb ära route parameetri `id` järgi: puudub → lisamine).

## Kasutajaliidese elemendid

| Element | Tüüp | Kirjeldus/käitumine |
|---|---|---|
| "Lisa juht" | Pealkiri | Vormi pealkiri. |
| Veateade | Alert (`alert alert-danger`) | Kuvatakse ainult siis, kui `errorMessage` ei ole tühi. |
| Eesnimi | Tekstisisend | Kohustuslik. |
| Perekonnanimi | Tekstisisend | Kohustuslik. |
| Telefon | Tekstisisend (`type="tel"`) | Kohustuslik. |
| E-post | Tekstisisend (`type="email"`) | Valikuline. |
| Staatus | Raadionupud / select: Aktiivne / Mitteaktiivne | Vaikimisi "Aktiivne". Saadetakse väljana `active` (boolean). |
| "Salvesta" | Nupp (`type="submit"`) | Valideerib ja saadab päringu. Päringu ajal `disabled`. |
| "Tühista" | `RouterLink` (`btn btn-outline-secondary`) | Tagasi `/drivers` (`{ name: 'driversRoute' }`), API kutset ei tehta. Üleminek teisele lehele → `RouterLink`, mitte nupp. |

## Käitumine ja valideerimine

1. Lehe avamisel kontrollitakse rolli (`SessionStorageService.userIsAdmin()`) — see on vaates juba olemas. Mitte-ADMIN → `navigateToNotAuthorizedView()`.
2. Väljad on tühjad, staatus "Aktiivne".
3. "Salvesta" vajutamisel tühjendatakse eelmine veateade ja kontrollitakse:
   - eesnimi tühi → "Eesnimi on kohustuslik";
   - perekonnanimi tühi → "Perekonnanimi on kohustuslik";
   - telefon tühi → "Telefon on kohustuslik".
   Vea korral päringut ei saadeta.
4. **Eesnimi ja perekonnanimi liidetakse üheks väljaks `name`** (`firstName.trim() + ' ' + lastName.trim()`), sest andmebaasis on ainult `driver.name`.
5. Tühi e-post saadetakse `null`-ina (mitte tühja stringina), et backendi `@Email` valideerimine ei ebaõnnestuks.
6. Õnnestumise korral (200) → `this.$router.push({ name: 'driversRoute' })` (üleminek pärast tegevust, seega koodist, mitte `RouterLink`).
7. Vea korral:
   - 400 `INCORRECT_INPUT` → kuvatakse backendi `message`;
   - muu viga → `NavigationService.navigateToErrorView()`.
8. Päringu lõppedes seatakse `isLoading = false` (`.finally()`).

## API kutsed

### `POST /api/drivers`

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
| muu | — | — | `/error` vaatele. |

> **Märkus:** 400 vea `message` tekst tuleb Springi valideerimise vaiketeatest ja võib sõltuda keelest. Frontend ei tohiks selle täpsele sõnastusele toetuda.

## Komponendid ja failistruktuur

- `frontend/src/views/DriverCreateEditView.vue` — **on olemas**, aga sisaldab ainult rollikontrolli. Lisa `data()` (vormi väljad `firstName`, `lastName`, `phone`, `email`, `active`, lisaks `errorMessage`, `isLoading`), meetodid ja template.
- `frontend/src/services/DriverService.js` — **on olemas** (`getDriversRequest`). Lisa meetod `postDriverRequest(driverRequestDto)` → `axios.post('/api/drivers', driverRequestDto)`.
- `frontend/src/router/index.js` — rada `/drivers/new` (`driver-create`) **on olemas**.
- `frontend/src/views/DriversView.vue` — "+ Lisa juht" on juba `RouterLink` `{ name: 'driver-create' }`.
- Stiil: Options API vastavalt `docs/frontend/vue-komponendi-struktuur.md`-le — `.then()/.catch()/.finally()` ahel, vastus ja viga eraldi meetodites (nt `handleAddDriverResponse`, `handleAddDriverError`).

## Vastuvõtu kriteeriumid

- [ ] `/drivers/new` lehel on pealkiri "Lisa juht", väljad eesnimi, perekonnanimi, telefon, e-post, staatus ning "Salvesta" ja "Tühista".
- [ ] Mitte-ADMIN kasutaja suunatakse "õigused puuduvad" lehele.
- [ ] Tühja kohustusliku välja korral kuvatakse veateade ja päringut ei saadeta.
- [ ] Salvestamisel saadetakse `POST /api/drivers`, kus `name` = eesnimi + tühik + perekonnanimi ja `active` on boolean.
- [ ] Tühi e-post saadetakse `null`-ina.
- [ ] Õnnestumise järel suunatakse `/drivers` ja uus juht on nimekirjas.
- [ ] "Tühista" viib `/drivers` ilma päringuta (ja on `RouterLink`, avaneb ka ⌘+klikiga uues vahekaardis).
- [ ] Päringu ajal on "Salvesta" nupp keelatud.
