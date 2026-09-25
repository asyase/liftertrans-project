# DriverJobsView.vue - Balsamiq märkmed

Balsamiqu leht: **DRIVER**

## Vaate märkmed

```text
Roll: Juht (DRIVER)
Failinimi: DriverJobsView.vue
Frontend rada: /my-jobs

Vaatega seotud lisainfo:
Avaneb DRIVER kasutajale pärast sisselogimist; näitab sisselogitud juhi tänaseid töid (backend määrab juhi accessTokenist, teiste juhtide töid ei tagastata) ja plokki "Järgmine töö".
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse ainult date=<tänane kuupäev>; juhi tööd valib backend accessTokeni järgi.
Sama endpointi kasutavad dashboardi tänaste tööde tabel, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.
DRIVER rolliga kasutajale tagastatakse alati ainult talle määratud tööd: driverId võetakse autentitud kasutajast (accessToken), mitte query parameetrist.

Veateated: —
```
