# JobCreateEditView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA / MUUDA TELLIMUST**

## Vaate märkmed

```text
Roll: Admin
Failinimi: JobCreateEditView.vue
Frontend rada: /jobs/new ja /jobs/:id/edit

Vaatega seotud lisainfo:
/jobs/new → pealkiri "Lisa uus tellimus", nupud Salvesta / Tühista; Salvesta loob DRAFT töö, tekib jobId ja vaade avaneb /jobs/:id/edit režiimis. /jobs/:id/edit → väljad eeltäidetakse; DRAFT: Salvesta muudatused / Kinnita (DRAFT → PLANNED) / Tühista; PLANNED: Salvesta muudatused (staatus jääb PLANNED).
Teostamise viis INTERNAL → Auto + Juht; SUBCONTRACTED → Alltöövõtja + (valikuliselt) alltöövõtja auto, Juht on peidetud. Töö tüüp CRANE_ONLY → ainult Töö aadress; TRANSPORT_AND_CRANE → pealevõtu ja kohaletoimetamise aadress.
Rippmenüüd Klient, Auto, Juht, Alltöövõtja laaditakse API-dest. Tegelikku algus-/lõpuaega, km-e ega tunde siin ei sisestata.
"+ Lisa kaup" on aktiivne alles siis, kui jobId on olemas → /jobs/:jobId/cargo/new; Tühista → tagasi /jobs (ilma API kutseta).
```

## API märkmed — GET /api/customers

```text
API: GET /api/customers

CustomerDto.java
Response (200):
[
  {
    "customerId": 1,
    "name": "Ants Asi",
    "companyName": "Mida Vaja OÜ",
    "companyRegistrationNumber": "12345678",
    "vatNumber": "EE123456789",
    "email": "ants@midavaja.ee",
    "invoiceEmail": "arved@midavaja.ee",
    "phone": "+3725559876"
  },
  ...
]

API teenuse lisainfo:
Valikuline query parameeter search filtreerib name või company_name järgi; ilma selleta tagastatakse kõik kliendid. Tulemusi pole → tühi massiiv (mitte 404).

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
Auto rippmenüü andmeallikas. Valikulised filtrid: subcontractorId (SUBCONTRACTED puhul valitud alltöövõtja autod) ja status. status: ACTIVE / IN_SERVICE / UNAVAILABLE / INACTIVE. subcontractorId = null → LIFTERTRANS-i oma auto.

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
Juhi rippmenüü andmeallikas; valikus kuvatakse ainult active = true juhid.

Veateated: —
```

## API märkmed — GET /api/subcontractors

```text
API: GET /api/subcontractors

SubcontractorDto.java
Response (200):
[
  {
    "subcontractorId": 1,
    "companyName": "Partner Transport OÜ",
    "companyRegistrationNumber": "11223344",
    "vatNumber": "EE112233445",
    "contactName": "Karl Saar",
    "phone": "+3725557777",
    "email": "info@partnertransport.ee",
    "notes": "Kasutada vajadusel alltöövõtjana.",
    "active": true
  },
  ...
]

API teenuse lisainfo:
Alltöövõtja rippmenüü andmeallikas (kuvatakse ainult SUBCONTRACTED korral); valitavad active = true alltöövõtjad.

Veateated: —
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

## API märkmed — POST /api/jobs

```text
API: POST /api/jobs

JobCreateRequestDto.java
Request body:
{
  "customerId": 1,
  "jobType": "TRANSPORT_AND_CRANE",
  "executionType": "INTERNAL",
  "vehicleId": null,
  "driverId": null,
  "subcontractorId": null,
  "pickupAddress": "Lasnamäe tee 12, Tallinn",
  "deliveryAddress": "Kesklinna 4, Tallinn",
  "serviceAddress": null,
  "receiverName": "Peeter Kask",
  "receiverPhone": "+3725554444",
  "plannedStartTime": "2026-09-24T09:00:00",
  "plannedEndTime": "2026-09-24T13:00:00",
  "estimatedKm": 65.00,
  "estimatedHours": 4.00,
  "notes": "Auto ja juht tuleb veel määrata."
}

JobCreateResponseDto.java
Response (200):
{
  "jobId": 1,
  "status": "DRAFT"
}

API teenuse lisainfo:
Loob töö staatusega DRAFT ja job_status_history rea (NULL → DRAFT). Aadressid sõltuvad jobType'ist (CRANE_ONLY → serviceAddress; TRANSPORT_AND_CRANE → pickupAddress + deliveryAddress). INTERNAL: subcontractorId = null; SUBCONTRACTED: driverId = null (DB JOB_assignment_ck).

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "jobType: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'customerId' väärtusega: 99"
```

## API märkmed — PUT /api/jobs/{jobId}

```text
API: PUT /api/jobs/{jobId}

JobUpdateRequestDto.java
Request body:
{
  "customerId": 1,
  "jobType": "TRANSPORT_AND_CRANE",
  "executionType": "INTERNAL",
  "vehicleId": null,
  "driverId": null,
  "subcontractorId": null,
  "pickupAddress": "Lasnamäe tee 12, Tallinn",
  "deliveryAddress": "Kesklinna 4, Tallinn",
  "serviceAddress": null,
  "receiverName": "Peeter Kask",
  "receiverPhone": "+3725554444",
  "plannedStartTime": "2026-09-24T09:00:00",
  "plannedEndTime": "2026-09-24T13:00:00",
  "estimatedKm": 65.00,
  "estimatedHours": 4.00,
  "notes": "Auto ja juht tuleb veel määrata."
}

Response (200): NONE

API teenuse lisainfo:
Muudab töö andmeid, staatus EI muutu (PLANNED jääb PLANNED-iks, DRAFT-i tagasi ei viida). Aadressi- ja teostusviisi reeglid samad mis POST /api/jobs.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "jobType: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — POST /api/jobs/{jobId}/confirm

```text
API: POST /api/jobs/{jobId}/confirm

Response (200): NONE

API teenuse lisainfo:
Viib DRAFT töö PLANNED staatusesse ja lisab job_status_history rea (DRAFT → PLANNED). Kinnitamiseks peab INTERNAL tööl olema vehicleId ja driverId, SUBCONTRACTED tööl subcontractorId.
Kinnitamisel luuakse tööle transport_document rida (saatelehe number kujul ST-<aasta>-<järjekorranumber>, saatja = klient, vedaja = LIFTERTRANS või alltöövõtja, loading_date / delivery_date = planeeritud algus / lõpp).

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
