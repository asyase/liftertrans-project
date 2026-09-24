# JobsView.vue - Balsamiq märkmed

Balsamiqu leht: **TELLIMUSED**

## Vaate märkmed

```text
Roll: Admin
Failinimi: JobsView.vue
Frontend rada: /jobs

Vaatega seotud lisainfo:
Lehe avamisel laetakse kõigi tööde nimekiri. Kiirfiltrid PLANNED / IN_PROGRESS / COMPLETED ja rippmenüüd Kuupäev, Auto, Juht, Staatus laadivad nimekirja uuesti valitud filtritega.
Juht/alltöövõtja veerus kuvatakse driverName või SUBCONTRACTED töö korral subcontractorName; aadressiveerus CRANE_ONLY töö puhul serviceAddress.
"+ Lisa uus tellimus" → /jobs/new, "Vaata" → /jobs/:id, "Muuda" → /jobs/:id/edit (ainult navigeerimine).
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse valikulised date, vehicleId, driverId, status.
Sama endpointi kasutavad dashboard, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.

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
Auto-filtri rippmenüü andmeallikas; kuvatakse registrationNumber. status: ACTIVE / IN_SERVICE / UNAVAILABLE / INACTIVE. subcontractorId = null → LIFTERTRANS-i oma auto.

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
Juht-filtri rippmenüü andmeallikas; kuvatakse name.

Veateated: —
```
