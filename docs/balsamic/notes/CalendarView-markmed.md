# CalendarView.vue - Balsamiq märkmed

Balsamiqu leht: **KALENDER**

## Vaate märkmed

```text
Roll: Admin
Failinimi: CalendarView.vue
Frontend rada: /calendar

Vaatega seotud lisainfo:
Kuvab töid päeva, nädala või kuu vaates; vaate vahetamisel laetakse valitud perioodi tööd uuesti.
Filtrid Auto, Juht ja Staatus (vaikimisi "kõik") laaditakse GET /api/vehicles ja GET /api/drivers kaudu; staatuse valikud on DRAFT, PLANNED, IN_PROGRESS, COMPLETED, CANCELLED.
SUBCONTRACTED töö juures kuvatakse auto/juhi asemel alltöövõtja nimi.
Tööle klikkides avaneb töö detailvaade /jobs/:id (ilma API kutseta).
```

## API märkmed — GET /api/jobs

```text
API: GET /api/jobs

JobListDto.java
Response (200):
[
  {
    "jobId": 2,
    "plannedStartTime": "2026-09-25T09:00:00",
    "customerName": "Mari Mets",
    "jobType": "TRANSPORT_AND_CRANE",
    "pickupAddress": "Pärnu mnt 145, Tallinn",
    "serviceAddress": null,
    "deliveryAddress": "Mustamäe tee 5, Tallinn",
    "vehicleRegistrationNumber": "876HGF",
    "driverName": "Mart Tamm",
    "subcontractorName": null,
    "status": "PLANNED"
  },
  ...
]

API teenuse lisainfo:
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse from ja to (valitud päev/nädal/kuu) ning valikulised vehicleId, driverId, status.
Sama endpointi kasutavad dashboardi tänaste tööde tabel, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.
DRIVER rolliga kasutajale tagastatakse alati ainult talle määratud tööd: driverId võetakse autentitud kasutajast (accessToken), mitte query parameetrist.

Veateated: —
```

## API märkmed — GET /api/vehicles

```text
API: GET /api/vehicles

VehicleListDto.java
Response (200):
[
  {
    "vehicleId": 1,
    "registrationNumber": "876HGF",
    "name": "MAN TGS 35.480",
    "maxCargoWeightKg": 18000.00,
    "platformLengthM": 8.50,
    "platformWidthM": 2.50,
    "status": "ACTIVE",
    "subcontractorId": null,
    "subcontractorName": null
  },
  ...
]

API teenuse lisainfo:
Kalendri Auto-filtri rippmenüü andmeallikas; kuvatakse registrationNumber. status: ACTIVE / IN_SERVICE / UNAVAILABLE / INACTIVE. subcontractorId = null → LIFTERTRANS-i oma auto.

Veateated: —
```

## API märkmed — GET /api/drivers

```text
API: GET /api/drivers

DriverDto.java
Response (200):
[
  {
    "driverId": 1,
    "name": "Mart Tamm",
    "phone": "+3725551111",
    "email": "mart.tamm@liftertrans.ee",
    "active": true
  },
  ...
]

API teenuse lisainfo:
Kalendri Juht-filtri rippmenüü andmeallikas; kuvatakse name.

Veateated: —
```
