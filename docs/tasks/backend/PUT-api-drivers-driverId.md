# Task: GET /api/drivers/{driverId}, PUT /api/drivers/{driverId} — juhi andmete muutmine

## Kirjeldus
ADMIN kasutaja saab muuta olemasoleva juhi andmeid (vaade `DriverCreateEditView.vue`, rada `/drivers/:id/edit`). Vaate avamisel eeltäidetakse vorm juhi andmetega (`GET`), salvestamisel uuendatakse juhti tabelis `driver` (`PUT`).

`active = false` juhti ei pakuta uute tööde juhi valikus. Juhi muutmine ei muuda tema sisselogimiskontot (`user` tabel).

**Vaste balsamic mockupis:** leht "LISA / MUUDA JUHTI", märkmed `docs/balsamic/notes/DriverCreateEditView-markmed.md`

## Endpointid

### 1. `GET /api/drivers/{driverId}`

Request: path parameeter `driverId` (Integer)

`DriverDto.java` (**on juba olemas**) — response (200):
```json
{
  "driverId": 1,
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}
```

**Veateated:**

| HTTP | errorCode | message |
|---|---|---|
| 404 | `PRIMARY_KEY_NOT_FOUND` | "Ei leidnud primary keyd 'driverId' väärtusega: 99" |

### 2. `PUT /api/drivers/{driverId}`

Request: path parameeter `driverId` (Integer)

`DriverRequestDto.java` — request body:
```json
{
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}
```

| Väli | Tüüp | Kohustuslik | Valideerimine |
|---|---|---|---|
| `name` | String | jah | `@NotBlank`, `@Size(max = 150)` |
| `phone` | String | jah | `@NotBlank`, `@Size(max = 30)` |
| `email` | String | ei | `@Email`, `@Size(max = 150)` |
| `active` | Boolean | jah | `@NotNull` |

Response (200): NONE

**Veateated:**

| HTTP | errorCode | message |
|---|---|---|
| 400 | `INCORRECT_INPUT` | "name: must not be blank" (esimese vigase välja nimi + valideerimisteade) |
| 404 | `PRIMARY_KEY_NOT_FOUND` | "Ei leidnud primary keyd 'driverId' väärtusega: 99" |

## Seotud tabelid
- `driver` (`id`, `name`, `phone`, `email`, `active`)

## Implementatsiooni märkused

1. **DTO** — loo `dto/DriverRequestDto.java` (ilma `driverId`-ta), valideerimisannotatsioonidega nagu tabelis ülal. Sama DTO-d saab hiljem kasutada ka juhi lisamisel (`POST /api/drivers`).
2. **Mapper** `DriverMapper.java`:
   - `toDriverDto(Driver)` **on juba olemas** — kasuta GET vastuse jaoks;
   - lisa meetod olemasoleva entity uuendamiseks: `void updateDriver(DriverRequestDto driverRequestDto, @MappingTarget Driver driver)`, `id` ignoreeritakse (`@Mapping(target = "id", ignore = true)`).
3. **Service** `DriverService.java`:
   - `getDriver(Integer driverId)` → `getValidDriverBy(driverId)` (**on juba olemas**, viskab 404) → `driverMapper.toDriverDto(driver)`;
   - `updateDriver(Integer driverId, DriverRequestDto driverRequestDto)` → `getValidDriverBy(driverId)` → `driverMapper.updateDriver(...)` → `driverRepository.save(driver)`.
4. **Controller** `DriverController.java`:
   - `@GetMapping("/drivers/{driverId}")`, parameeter `@PathVariable Integer driverId`;
   - `@PutMapping("/drivers/{driverId}")`, parameetrid `@PathVariable Integer driverId` ja `@RequestBody @Valid DriverRequestDto driverRequestDto`;
   - lisa `@Operation` ja `@ApiResponses` (200, 400, 404) nagu `JobController`-is.
5. Andmebaasi muudatusi ei ole vaja — muutmisel `id` ei genereerita.

## Vastuvõtu kriteeriumid
- [ ] `GET /api/drivers/1` tagastab 200 ja juhi andmed `DriverDto` kujul.
- [ ] `GET /api/drivers/99` (olematu) tagastab 404 `PRIMARY_KEY_NOT_FOUND`.
- [ ] `PUT /api/drivers/1` korrektse body'ga tagastab 200 ja juhi andmed on andmebaasis muudetud; `id` jääb samaks.
- [ ] Muudatus on nähtav `GET /api/drivers` ja `GET /api/drivers/1` vastuses.
- [ ] `PUT` tühja `name` või `phone` korral tagastab 400 `INCORRECT_INPUT` ja andmeid ei muudeta.
- [ ] `PUT` olematu `driverId` korral tagastab 404 `PRIMARY_KEY_NOT_FOUND`.
- [ ] `email` võib olla `null`.
- [ ] Juhi seadmine `active = false` õnnestub ka siis, kui temaga on seotud töid.
- [ ] Mõlemad endpointid on Swaggeris kirjeldatud.
