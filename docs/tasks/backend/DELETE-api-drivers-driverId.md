# Task: DELETE /api/drivers/{driverId} — juhi kustutamine

## Kirjeldus
ADMIN kasutaja saab kustutada juhi juhtide nimekirjast (vaade `DriversView.vue`, rada `/drivers`) või juhi detailvaatest (`DriverDetailView.vue`, rada `/drivers/:id`). Nupp "Kustuta" avab kinnitusakna, pärast kinnitust saadetakse päring `DELETE /api/drivers/{driverId}`.

Juht kustutatakse tabelist `driver` ainult siis, kui temaga ei ole seotud ühtegi tööd (`job.driver_id`). Kui töid on, tagastatakse viga 409 ja juhti ei kustutata — sellisel juhul tuleb juht muuta mitteaktiivseks (`active = false`) juhi muutmise vaates (`PUT /api/drivers/{driverId}`, vt `PUT-api-drivers-driverId.md`).

**Vaste balsamic mockupis:** lehed "JUHID" ja "JUHI DETAILVAADE", märkmed `docs/balsamic/notes/DriversView-markmed.md` ja `docs/balsamic/notes/DriverDetailView-markmed.md`

**Frontend on juba olemas:** `DriversView.vue` meetod `deleteDriver(driverId)` saadab `axios.delete('/api/drivers/' + driverId)` ja värskendab seejärel tabelit. Praegu tagastab backend 404 ("No static resource api/drivers/2"), sest endpointi ei ole.

## Endpoint

### `DELETE /api/drivers/{driverId}`

Request: path parameeter `driverId` (Integer)

Response (200): NONE

**Veateated:**

| HTTP | errorCode | message |
|---|---|---|
| 404 | `PRIMARY_KEY_NOT_FOUND` | "Ei leidnud primary keyd 'driverId' väärtusega: 99" |
| 409 | `RESOURCE_IN_USE` | "Juhti ei saa kustutada, sest temaga on seotud töid" |

## Seotud tabelid
- `driver` (`id`, `name`, `phone`, `email`, `active`) — kustutatav rida
- `job` (`driver_id` → `driver.id`, FK `JOB_DRIVER_fk`) — kontrollitakse, kas juhiga on seotud töid
- `user` (`driver_id` → `driver.id`, FK `USER_DRIVER_fk`) — juhi sisselogimiskonto (vt avatud küsimus allpool)

## Implementatsiooni märkused

1. **Veakood** `ErrorCode.java` — lisa uus väärtus:
   ```java
   RESOURCE_IN_USE(HttpStatus.CONFLICT, "Juhti ei saa kustutada, sest temaga on seotud töid")
   ```
   Viga visatakse kujul `throw new BusinessException(ErrorCode.RESOURCE_IN_USE)` — `GlobalExceptionHandler` käsitleb `BusinessException`-it juba.
2. **Repository** `JobRepository.java` — lisa meetod, mis kontrollib, kas juhiga on seotud töid:
   ```java
   boolean existsJobByDriverId(Integer driverId);
   ```
   (`Job` entity väli on `driver`, seega Spring Data tuletab päringu `job.driver.id` järgi.)
3. **Service** `DriverService.java` — lisa meetod `deleteDriver(Integer driverId)`:
   - `Driver driver = getValidDriverBy(driverId)` (**on juba olemas**, viskab 404);
   - kui `jobRepository.existsJobByDriverId(driverId)` on `true` → `throw new BusinessException(ErrorCode.RESOURCE_IN_USE)`;
   - muul juhul `driverRepository.delete(driver)`.
   - Kontroll ja kustutamine on eraldi meetodid (nt `validateDriverHasNoJobs(Integer driverId)`), et `deleteDriver` jääks loetavaks.
4. **Controller** `DriverController.java`:
   - `@DeleteMapping("/drivers/{driverId}")`, parameeter `@PathVariable Integer driverId`;
   - lisa `@Operation` ja `@ApiResponses` (200, 404, 409) nagu `JobController`-is.
5. **Frontend** (`DriversView.vue`) — `catch` plokis näita 409 korral backendi teadet (`error.response.data.message`), mitte üldist "Kustutamine ebaõnnestus". See on eraldi frontend muudatus, mitte selle taski osa.
6. Andmebaasi muudatusi ei ole vaja.

## Avatud küsimus — juhi kasutajakonto
Tabelis `user` on veerg `driver_id`, mis viitab juhile. Kui juhil on sisselogimiskonto, kukub `DELETE` andmebaasi FK vea tõttu (500). Mockup seda olukorda ei kirjelda. Variandid:
- **a)** käsitleda samamoodi nagu töid — kontrollida `userRepository.existsUserByDriverId(driverId)` ja tagastada 409 (eraldi veateatega, nt "Juhti ei saa kustutada, sest tal on kasutajakonto");
- **b)** kustutada koos juhiga ka tema kasutajakonto.

Otsus tuleb teha enne implementeerimist.

## Vastuvõtu kriteeriumid
- [ ] `DELETE /api/drivers/{driverId}` juhi kohta, kellel ei ole töid, tagastab 200 ja juht on tabelist `driver` kustutatud.
- [ ] Pärast kustutamist ei ole juhti `GET /api/drivers` vastuses.
- [ ] `DELETE` juhi kohta, kellega on seotud vähemalt üks töö, tagastab 409 `RESOURCE_IN_USE` ja juht jääb andmebaasi alles.
- [ ] `DELETE /api/drivers/99` (olematu) tagastab 404 `PRIMARY_KEY_NOT_FOUND`.
- [ ] Juhi kasutajakonto olukord on lahendatud vastavalt avatud küsimuse otsusele (ei tagasta 500).
- [ ] `DriversView.vue` nupp "Kustuta" töötab: pärast kinnitust juht kaob tabelist.
- [ ] Endpoint on Swaggeris kirjeldatud.
