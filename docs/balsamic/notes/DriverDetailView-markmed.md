# DriverDetailView.vue - Balsamiq märkmed

Balsamiqu leht: **JUHT detailvaade**

## Vaate märkmed

```text
Roll: Admin
Failinimi: DriverDetailView.vue
Frontend rada: /drivers/:id

Vaatega seotud lisainfo:
Lehe avamisel laetakse juhi andmed ja tabel "Planeeritud tööd" (kuupäev, kellaaeg, klient, töö tüüp, staatus).
"Kustuta" küsib kinnitust, eduka kustutamise järel suunatakse /drivers. "Muuda" → /drivers/:id/edit (ainult navigeerimine).
```

## API märkmed — GET /api/drivers/{driverId}

```text
API: GET /api/drivers/{driverId}

DriverDto.java
Response (200):
{
  "driverId": 1,
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}

API teenuse lisainfo:
Andmebaasis on juhi nimi ühes väljas driver.name (ees- ja perekonnanimi eraldi puuduvad).

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'driverId' väärtusega: 99"
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse driverId=<juhi ID> ja status=PLANNED.
Sama endpointi kasutavad dashboard, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.

Veateated: —
```

## API märkmed — DELETE /api/drivers/{driverId}

```text
API: DELETE /api/drivers/{driverId}

Response (200): NONE

API teenuse lisainfo:
Kustutab juhi ID alusel pärast kinnitust kinnitusaknas.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'driverId' väärtusega: 99"
```
