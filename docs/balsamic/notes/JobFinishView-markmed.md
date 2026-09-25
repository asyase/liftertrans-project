# JobFinishView.vue - Balsamiq märkmed

Balsamiqu leht: **LÕPETA TÖÖ**

## Vaate märkmed

```text
Roll: Juht (DRIVER)
Failinimi: JobFinishView.vue
Frontend rada: /my-jobs/:id/finish

Vaatega seotud lisainfo:
Avaneb DRIVER detailvaatest nupust "Lõpeta töö" (ainult IN_PROGRESS töö). Kuvatakse tegelik algusaeg ja marsruut; lõpetamise aeg salvestatakse automaatselt.
Juht sisestab tegeliku kilometraaži, valikulise märkuse ning saab lisada kohaletoimetamise foto ja saatelehe foto (üleslaadimine POST /api/jobs/{jobId}/documents).
"Lõpeta töö" → staatus COMPLETED, actual_hours arvutatakse automaatselt; seejärel suunatakse /my-jobs.
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

## API märkmed — POST /api/jobs/{jobId}/documents

```text
API: POST /api/jobs/{jobId}/documents

JobDocumentCreateRequestDto.java
Request body:
{
  "documentType": "DELIVERY_PHOTO",
  "fileName": "delivery-job-4.jpg",
  "fileData": "BASE64-file-data"
}

Response (200): NONE

API teenuse lisainfo:
Frontend saadab faili Base64 kujul (fileData). Backend salvestab faili serveri kausta uploads/jobs/{jobId}/documents/ ja loob job_document rea (file_name, file_url = /uploads/jobs/{jobId}/documents/<failinimi>, uploaded_at = praegune aeg).
Lubatud on application/pdf, image/jpeg ja image/png kuni 10 MB (backend kontrollib sisu tüüpi ja suurust). Juht saab lisada faile ainult talle määratud tööle; failid serveeritakse aadressilt /uploads/**.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "documentType: must not be blank"

HTTP: 400
errorCode: INCORRECT_INPUT
message: "fileData: lubatud on PDF-, JPEG- või PNG-fail kuni 10 MB"

HTTP: 403
errorCode: ACCESS_DENIED
message: "Sul puudub õigus selle töö andmetele"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — POST /api/jobs/{jobId}/finish

```text
API: POST /api/jobs/{jobId}/finish

JobFinishRequestDto.java
Request body:
{
  "actualKm": 62.00,
  "comment": "Kliendi esindaja võttis kauba vastu"
}

Response (200): NONE

API teenuse lisainfo:
Lubatud ainult tööle määratud juhile ja ainult IN_PROGRESS tööl: actual_finish_time = praegune aeg, actual_hours arvutatakse actual_start_time ja actual_finish_time vahest, staatus → COMPLETED. comment salvestatakse COMPLETED staatuse ajaloo reale (job_status_history.comment); job.notes (admini märkused) ei muutu.

Veateated:
HTTP: 403
errorCode: ACCESS_DENIED
message: "Sul puudub õigus selle töö andmetele"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"

HTTP: 409
errorCode: INVALID_STATUS_TRANSITION
message: "Tööd saab lõpetada ainult IN_PROGRESS staatuses"
```
