# Task: GET /api/drivers/{driverId}, GET /api/jobs?driverId=&status= — juhi detailvaade

## Kirjeldus
ADMIN kasutaja avab juhi detailvaate (vaade `DriverDetailView.vue`, rada `/drivers/:id`). Lehe avamisel laetakse:
1. juhi andmed (`GET /api/drivers/{driverId}`);
2. tabel "Planeeritud tööd" — juhile määratud tööd staatusega `PLANNED` (`GET /api/jobs?driverId=<juhi ID>&status=PLANNED`). Tabelis kuvatakse kuupäev, kellaaeg, klient, töö tüüp ja staatus.

Nupp "Kustuta" kasutab endpointi `DELETE /api/drivers/{driverId}` — see on kirjeldatud eraldi taskis `DELETE-api-drivers-driverId.md`. Nupp "Muuda" on ainult navigeerimine (`/drivers/:id/edit`), backendi ei vaja.

**Vaste balsamic mockupis:** leht "JUHT detailvaade", märkmed `docs/balsamic/notes/DriverDetailView-markmed.md`

## Endpointid

### 1. `GET /api/drivers/{driverId}`

> Sama endpoint on kirjeldatud ka taskis `PUT-api-drivers-driverId.md` (juhi muutmise vormi eeltäitmine). Endpoint implementeeritakse **üks kord** — kumb task enne tehakse, see selle lisab.

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

Andmebaasis on juhi nimi ühes väljas `driver.name` (ees- ja perekonnanimi eraldi puuduvad).

**Veateated:**

| HTTP | errorCode | message |
|---|---|---|
| 404 | `PRIMARY_KEY_NOT_FOUND` | "Ei leidnud primary keyd 'driverId' väärtusega: 99" |

### 2. `GET /api/jobs?driverId={driverId}&status={status}`

Olemasolev endpoint `GET /api/jobs` (praegu tagastab **kõik** tööd, `jobRepository.findAll()`). Selles taskis lisatakse sellele kaks **valikulist** query parameetrit:

| Parameeter | Tüüp | Kohustuslik | Tähendus |
|---|---|---|---|
| `driverId` | Integer | ei | ainult sellele juhile määratud tööd (`job.driver_id`) |
| `status` | String | ei | ainult selle staatusega tööd (`DRAFT`, `PLANNED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`) |

Kui parameetrit ei saadeta, selle järgi ei filtreerita — `GET /api/jobs` ilma parameetriteta töötab nagu praegu (tööde nimekirja vaade ei tohi katki minna).

`JobDto.java` (**on juba olemas**) — response (200):
```json
[
  {
    "jobId": 2,
    "plannedStartTime": "2026-09-25T09:00:00Z",
    "customerName": "Mari Mets",
    "jobType": "TRANSPORT_AND_CRANE",
    "pickupAddress": "Pärnu mnt 145, Tallinn",
    "serviceAddress": null,
    "deliveryAddress": "Mustamäe tee 5, Tallinn",
    "vehicleRegistrationNumber": "876HGF",
    "driverName": "Mart Tamm",
    "subcontractorName": null,
    "status": "PLANNED"
  }
]
```

Tulemused on järjestatud `plannedStartTime` järgi kasvavalt. Tulemusi pole → tühi massiiv `[]`.

**Veateated:** —

## Seotud tabelid
- `driver` (`id`, `name`, `phone`, `email`, `active`)
- `job` (`driver_id`, `status`, `planned_start_time`, ...) + seotud `customer`, `vehicle`, `subcontractor` (`JobDto` väljade jaoks)

## Implementatsiooni märkused

### GET /api/drivers/{driverId}
1. **Service** `DriverService.java` — lisa `getDriver(Integer driverId)`:
   - `Driver driver = getValidDriverBy(driverId)` (**on juba olemas**, viskab 404);
   - `return driverMapper.toDriverDto(driver)` (**mapper on juba olemas**).
2. **Controller** `DriverController.java` — `@GetMapping("/drivers/{driverId}")`, parameeter `@PathVariable Integer driverId`; lisa `@Operation` ja `@ApiResponses` (200, 404).

### GET /api/jobs filtrid
3. **Repository** `JobRepository.java` — lisa JPQL päring `@Query` annotatsiooniga, kus `null` parameeter tähendab "ära filtreeri":
   ```java
   @Query("""
           select j from Job j
           where (:driverId is null or j.driver.id = :driverId)
             and (:status is null or j.status = :status)
           order by j.plannedStartTime
           """)
   List<Job> findFilteredJobsBy(Integer driverId, String status);
   ```
4. **Service** `JobService.java` — muuda `getJobs()` → `getJobs(Integer driverId, String status)`, `findAll()` asemel `jobRepository.findFilteredJobsBy(driverId, status)`; mapper `toJobDtos(...)` jääb samaks.
5. **Controller** `JobController.java` — `getJobs()` saab parameetrid:
   ```java
   public List<JobDto> getJobs(@RequestParam(required = false) Integer driverId,
                               @RequestParam(required = false) String status)
   ```
   Uuenda `@Operation` kirjeldust (praegu "Tagastab kõik tööd").
6. Mockup kirjeldab `GET /api/jobs` jaoks ka teisi filtreid (`date`, `from`, `to`, `vehicleId`, `customerId`, `subcontractorId`) ja DRIVER rolli piirangut — need **ei ole selle taski osa**, lisatakse vastavate vaadete taskides. Päring tuleb kirjutada nii, et uute filtrite lisamine oleks lihtne (sama `:param is null or ...` muster).
7. Andmebaasi muudatusi ei ole vaja.

## Vastuvõtu kriteeriumid
- [ ] `GET /api/drivers/1` tagastab 200 ja juhi andmed `DriverDto` kujul.
- [ ] `GET /api/drivers/99` (olematu) tagastab 404 `PRIMARY_KEY_NOT_FOUND`.
- [ ] `GET /api/jobs?driverId=1&status=PLANNED` tagastab ainult juhi 1 tööd staatusega `PLANNED`, järjestatud `plannedStartTime` järgi.
- [ ] `GET /api/jobs?driverId=1` tagastab kõik juhi 1 tööd (kõik staatused).
- [ ] `GET /api/jobs?status=PLANNED` tagastab kõikide juhtide `PLANNED` tööd.
- [ ] `GET /api/jobs` ilma parameetriteta tagastab kõik tööd — tööde nimekirja vaade töötab edasi.
- [ ] Juhi kohta, kellel töid ei ole, tagastab `GET /api/jobs?driverId=...` tühja massiivi `[]`.
- [ ] Mõlemad endpointid on Swaggeris kirjeldatud (sh query parameetrid).
