# DriverJobsView.vue - Balsamiq märkmed

Balsamiqu leht: **DRIVER**

## Vaate märkmed

```text
Roll: Juht (DRIVER)
Failinimi: DriverJobsView.vue
Frontend rada: /my-jobs

Vaatega seotud lisainfo:
Avaneb DRIVER kasutajale pärast sisselogimist; näitab sisselogitud juhi (sessionStorage driverId) tänaseid töid ja plokki "Järgmine töö".
UI staatuse sildid: DRAFT → Uus, PLANNED → Planeeritud, IN_PROGRESS → Töös, COMPLETED → Lõpetatud, CANCELLED → Tühistatud.
"Alusta töö" on nähtav ainult PLANNED tööl ja avab modaalakna JobStartDialog.vue. "Vaata" / "Vaata tööd" → /my-jobs/:id (ainult navigeerimine).
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse driverId=<sessionStorage driverId> ja date=<tänane kuupäev>.
Sama endpointi kasutavad dashboard, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.

Veateated: —
```
