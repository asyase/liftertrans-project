# CargoCreateEditView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA / MUUDA KAUP**

## Vaate märkmed

```text
Roll: Admin
Failinimi: CargoCreateEditView.vue
Frontend rada: /jobs/:jobId/cargo/new ja /jobs/:jobId/cargo/:cargoId/edit

Vaatega seotud lisainfo:
Avaneb tellimuse vormist ("+ Lisa kaup", loomise režiim) või töö detailvaate Kaup vahekaardilt ("Muuda", muutmise režiim, väljad eeltäidetud). Päises kuvatakse tellimuse number ja staatus (GET /api/jobs/{jobId}).
Loomise režiim: Salvesta / Tühista. Muutmise režiim: Salvesta muudatused / Kustuta kaup (küsib kinnitust) / Tühista.
Kõik nupud viivad tagasi tellimuse juurde; kauba lisamine, muutmine või kustutamine ei muuda töö staatust (cargo.job_id = job.id). Foto on valikuline.
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

## API märkmed — GET /api/jobs/{jobId}/cargo/{cargoId}

```text
API: GET /api/jobs/{jobId}/cargo/{cargoId}

CargoDto.java
Response (200):
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
}

API teenuse lisainfo:
Kasutatakse kauba muutmise vormi eeltäitmiseks.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'cargoId' väärtusega: 99"
```

## API märkmed — POST /api/jobs/{jobId}/cargo

```text
API: POST /api/jobs/{jobId}/cargo

CargoRequestDto.java
Request body:
{
  "description": "Betoonplaadid",
  "weightKg": 4200.00,
  "lengthM": 3.00,
  "widthM": 1.20,
  "heightM": 0.80,
  "quantity": 6,
  "cargoPhotoData": "BASE64-image-data",
  "notes": null
}

Response (200): NONE

API teenuse lisainfo:
Lisab kauba olemasolevale tellimusele (cargo.job_id = jobId); töö staatus EI muutu. cargoPhotoData on tühi string, kui fotot ei lisata; foto korral salvestab backend faili serveri kausta uploads/jobs/{jobId}/cargo/ ja kirjutab välja cargo_photo_url aadressi /uploads/jobs/{jobId}/cargo/<failinimi>.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "description: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — PUT /api/jobs/{jobId}/cargo/{cargoId}

```text
API: PUT /api/jobs/{jobId}/cargo/{cargoId}

CargoRequestDto.java
Request body:
{
  "description": "Betoonplaadid",
  "weightKg": 4200.00,
  "lengthM": 3.00,
  "widthM": 1.20,
  "heightM": 0.80,
  "quantity": 6,
  "cargoPhotoData": "BASE64-image-data",
  "notes": null
}

Response (200): NONE

API teenuse lisainfo:
Muudab kaubarida; töö staatus EI muutu. cargoPhotoData tühi string → olemasolev foto jääb alles; uus foto salvestatakse kausta uploads/jobs/{jobId}/cargo/ ja asendab cargo_photo_url väärtuse.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "description: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'cargoId' väärtusega: 99"
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
