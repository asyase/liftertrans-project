# DocumentUploadView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA DOKUMENT**

## Vaate märkmed

```text
Roll: Juht (DRIVER)
Failinimi: DocumentUploadView.vue
Frontend rada: /my-jobs/:id/documents/new

Vaatega seotud lisainfo:
Lehe avamisel laetakse tööga juba seotud failid (tabel "Üleslaaditud failid").
Juht valib dokumendi tüübi (Kohaletoimetamise foto → DELIVERY_PHOTO, Saatelehe foto → WAYBILL_PHOTO, Kauba foto → CARGO_PHOTO, Muu dokument → OTHER) ja faili (PDF, JPEG või PNG kuni 10 MB — frontend kontrollib enne saatmist); "Laadi üles" salvestab faili ning nimekiri laetakse uuesti.
Rea "Vaata" avab faili fileUrl kaudu ilma API kutseta. Dokumendid on seotud konkreetse tööga (job_id).
```

## API märkmed — GET /api/jobs/{jobId}/documents

```text
API: GET /api/jobs/{jobId}/documents

JobDocumentDto.java
Response (200):
[
  {
    "documentId": 1,
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
