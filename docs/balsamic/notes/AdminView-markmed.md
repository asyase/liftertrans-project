# AdminView.vue - Balsamiq märkmed

Balsamiqu leht: **ADMIN**

## Vaate märkmed

```text
Roll: Admin
Failinimi: AdminView.vue
Frontend rada: /dashboard

Vaatega seotud lisainfo:
Avaneb ADMIN kasutajale pärast sisselogimist; vasakul külgmenüü (Esileht, Kalender, Tellimused, Kliendid, Autod, Juhid, Alltöövõtjad, Logi välja).
Lehe avamisel laetakse tänased tööd (GET /api/jobs, date = tänane kuupäev). Loendurid arvutab frontend samast nimekirjast: Tänased tööd = kõik (sh DRAFT), Planeeritud = PLANNED, Töös = IN_PROGRESS, Lõpetatud = COMPLETED.
Tabel on sorteeritud plannedStartTime järgi; mobiilis kuvatakse ainult Kellaaeg, Klient, Staatus ning "+ Lisa uus tellimus" ja "Tänased tööd" on enne kalendrit.
"+ Lisa uus tellimus" → /jobs/new, "Vaata" → /jobs/:id, "Vaata kogu kalendrit" → /calendar (ainult navigeerimine).
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
Sama endpointi kasutavad dashboard, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.

Veateated: —
```
