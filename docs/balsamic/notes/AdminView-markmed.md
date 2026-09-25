# AdminView.vue - Balsamiq märkmed

Balsamiqu leht: **ADMIN**

## Vaate märkmed

```text
Roll: Admin
Failinimi: AdminView.vue
Frontend rada: /dashboard

Vaatega seotud lisainfo:
Avaneb ADMIN kasutajale pärast sisselogimist; vasakul külgmenüü. "Logi välja" kustutab accessTokeni ja kasutaja andmed sessionStorage'ist ning suunab /login (API kutset pole).
Loendurid Tänased tööd / Planeeritud / Töös / Lõpetatud tulevad GET /api/dashboard/summary vastusest; tänaste tööde tabel laetakse GET /api/jobs?date=<tänane kuupäev>.
Tabel on sorteeritud plannedStartTime järgi; mobiilis kuvatakse ainult Kellaaeg, Klient, Staatus ning "+ Lisa uus tellimus" ja "Tänased tööd" on enne kalendrit.
"+ Lisa uus tellimus" → /jobs/new, "Vaata" → /jobs/:id, "Vaata kogu kalendrit" → /calendar (ainult navigeerimine).
```

## API märkmed — GET /api/dashboard/summary

```text
API: GET /api/dashboard/summary

DashboardSummaryDto.java
Response (200):
{
  "todayJobs": 5,
  "planned": 2,
  "inProgress": 1,
  "completed": 2
}

API teenuse lisainfo:
Arvutab backendis tänaste tööde (plannedStartTime kuupäev = tänane) koondarvud: todayJobs = kõik tänased tööd, planned / inProgress / completed = vastava staatusega tänased tööd. Ainult ADMIN rollile.

Veateated: —
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse date=<tänane kuupäev>.
Sama endpointi kasutavad dashboardi tänaste tööde tabel, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.
DRIVER rolliga kasutajale tagastatakse alati ainult talle määratud tööd: driverId võetakse autentitud kasutajast (accessToken), mitte query parameetrist.

Veateated: —
```
