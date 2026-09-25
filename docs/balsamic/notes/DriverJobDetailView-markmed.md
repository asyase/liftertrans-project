# DriverJobDetailView.vue - Balsamiq märkmed

Balsamiqu leht: **DRIVER detailne sõiduvaade**

## Vaate märkmed

```text
Roll: Juht (DRIVER)
Failinimi: DriverJobDetailView.vue
Frontend rada: /my-jobs/:id

Vaatega seotud lisainfo:
Lehe avamisel laetakse töö andmed ja kaubaread (kirjeldus, kogus, kaal, mõõdud, foto); juht saab avada ainult talle määratud töö (muul juhul 403).
Nupud sõltuvad staatusest: PLANNED → "Alusta töö" (avab JobStartDialog.vue), IN_PROGRESS → "Lõpeta töö" (→ /my-jobs/:id/finish).
"Ava saateleht" (→ /jobs/:id/waybill, ainult vaatamine) ja "Lisa dokument" (→ /my-jobs/:id/documents/new) on alati nähtavad.
Nupp "Muuda saatelehte" tuleb asendada nupuga "Lisa saatelehe foto" (→ /my-jobs/:id/documents/new, tüüp WAYBILL_PHOTO) — juht saatelehe andmeid ei muuda.
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
Tagastab ühe töö koos kliendi, auto, juhi ja alltöövõtja andmetega. actual* väljad täituvad töö alustamisel ja lõpetamisel. Sama vastust kasutavad admini detailvaade, tellimuse ja kauba vorm ning juhi vaated. DRIVER saab avada ainult talle määratud töö (job.driver_id = autentitud kasutaja driverId).

Veateated:
HTTP: 403
errorCode: ACCESS_DENIED
message: "Sul puudub õigus selle töö andmetele"

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
HTTP: 403
errorCode: ACCESS_DENIED
message: "Sul puudub õigus selle töö andmetele"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```
