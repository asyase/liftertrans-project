# JobDetailView.vue - Balsamiq märkmed

Balsamiqu leht: **TELLIMUSE DETAILVAADE**

## Vaate märkmed

```text
Roll: Admin
Failinimi: JobDetailView.vue
Frontend rada: /jobs/:id

Vaatega seotud lisainfo:
Lehe avamisel laetakse töö andmed; vahekaardid Ülevaade / Kaup / Dokumendid / Staatuse ajalugu on sama lehe osad ja laadivad oma andmed eraldi API-dega.
Plokk "Tegelik" (tegelik algus, lõpp, km, tunnid) kuvatakse ainult COMPLETED staatusega tööl; SUBCONTRACTED töö puhul kuvatakse Alltöövõtja ning Juht on tühi.
Kauba real "Muuda" → /jobs/:jobId/cargo/:cargoId/edit, "Kustuta" küsib kinnitust ja kustutab kaubarea; töö staatus ei muutu.
"Muuda" → /jobs/:id/edit, "Ava saateleht" → /jobs/:id/waybill (navigeerimine); "Tühista tellimus" (ainult DRAFT ja PLANNED) küsib kinnitust, muudab staatuse CANCELLED ja laeb andmed uuesti.
```

## API märkmed — GET /api/jobs/{jobId}

```text
API: GET /api/jobs/{jobId}

JobDetailDto.java
Response (200):
{
  "jobId": 2,
  "status": "PLANNED",
  "jobType": "TRANSPORT_AND_CRANE",
  "executionType": "INTERNAL",
  "customerId": 2,
  "customerName": "Mari Mets",
  "customerCompanyName": "Ehitus AS",
  "customerPhone": "+3725551234",
  "customerEmail": "mari@ehitus.ee",
  "vehicleId": 1,
  "vehicleRegistrationNumber": "876HGF",
  "vehicleName": "MAN TGS 35.480",
  "driverId": 1,
  "driverName": "Mart Tamm",
  "subcontractorId": null,
  "subcontractorName": null,
  "pickupAddress": "Pärnu mnt 145, Tallinn",
  "deliveryAddress": "Mustamäe tee 5, Tallinn",
  "serviceAddress": null,
  "receiverName": "Mari Mets",
  "receiverPhone": "+3725551234",
  "plannedStartTime": "2026-09-25T09:00:00",
  "plannedEndTime": "2026-09-25T12:00:00",
  "actualStartTime": null,
  "actualFinishTime": null,
  "estimatedKm": 42.00,
  "actualKm": null,
  "estimatedHours": 3.00,
  "actualHours": null,
  "notes": "Planeeritud töö oma ressursiga."
}

API teenuse lisainfo:
Tagastab ühe töö koos kliendi, auto, juhi ja alltöövõtja andmetega. actual* väljad täituvad töö alustamisel ja lõpetamisel. Sama vastust kasutavad admini detailvaade, tellimuse ja kauba vorm ning juhi vaated.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — GET /api/jobs/{jobId}/cargo

```text
API: GET /api/jobs/{jobId}/cargo

CargoDto.java
Response (200):
[
  {
    "cargoId": 2,
    "jobId": 2,
    "description": "Ehitusmaterjalid",
    "weightKg": 2500.00,
    "lengthM": 2.50,
    "widthM": 1.50,
    "heightM": 1.20,
    "quantity": 4,
    "cargoPhotoUrl": "/demo/cargo/ehitusmaterjalid.jpg",
    "notes": "Hoida kuivana."
  },
  ...
]

API teenuse lisainfo:
Tagastab tellimuse kõik kaubaread (cargo.job_id = jobId). Kaupu pole → tühi massiiv.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — DELETE /api/jobs/{jobId}/cargo/{cargoId}

```text
API: DELETE /api/jobs/{jobId}/cargo/{cargoId}

Response (200): NONE

API teenuse lisainfo:
Kustutab kaubarea pärast kasutaja kinnitust; töö staatus EI muutu.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'cargoId' väärtusega: 99"
```

## API märkmed — GET /api/jobs/{jobId}/documents

```text
API: GET /api/jobs/{jobId}/documents

JobDocumentDto.java
Response (200):
[
  {
    "jobDocumentId": 1,
    "documentType": "DELIVERY_PHOTO",
    "fileName": "delivery-job-4.jpg",
    "fileUrl": "/demo/documents/delivery-job-4.jpg",
    "uploadedAt": "2026-09-21T10:00:00"
  },
  ...
]

API teenuse lisainfo:
documentType: DELIVERY_PHOTO / WAYBILL_PHOTO / CARGO_PHOTO / OTHER (DB JOB_DOCUMENT_type_ck). "Vaata" avab fileUrl-i otse, eraldi API kutset pole.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — GET /api/jobs/{jobId}/status-history

```text
API: GET /api/jobs/{jobId}/status-history

JobStatusHistoryDto.java
Response (200):
[
  {
    "jobStatusHistoryId": 3,
    "oldStatus": "DRAFT",
    "newStatus": "PLANNED",
    "changedAt": "2026-09-23T10:00:00",
    "changedBy": "admin@liftertrans.ee",
    "comment": "Tellimus kinnitatud ja planeeritud"
  },
  ...
]

API teenuse lisainfo:
Read tabelist job_status_history, järjestatud changed_at järgi. Esimesel real on oldStatus = null.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — POST /api/jobs/{jobId}/cancel

```text
API: POST /api/jobs/{jobId}/cancel

Response (200): NONE

API teenuse lisainfo:
Tühistab tellimuse: DRAFT või PLANNED → CANCELLED ja lisab job_status_history rea (changed_by = admini e-post). IN_PROGRESS, COMPLETED ja CANCELLED staatusega tööd tühistada ei saa.
Tühistatud töö jääb nimekirja ja kalendrisse CANCELLED staatusega; kaubaread ja dokumendid säilivad.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```
