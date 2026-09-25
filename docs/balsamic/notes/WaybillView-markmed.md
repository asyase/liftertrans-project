# WaybillView.vue - Balsamiq märkmed

Balsamiqu leht: **SAATELEHT**

## Vaate märkmed

```text
Roll: Admin ja Juht (DRIVER)
Failinimi: WaybillView.vue
Frontend rada: /jobs/:id/waybill

Vaatega seotud lisainfo:
Saateleht (transpordidokument) koostatakse olemasolevatest süsteemi andmetest — see ei ole sisestusvorm.
Kuvatakse dokumendi number, saatja, saaja, vedaja, kaubaread, juht, auto ning allkirjaväljad.
"Laadi PDF" genereerib PDF-i töö hetkeandmetest (POST /api/jobs/{jobId}/waybill/pdf) ja avab tagastatud generatedFileUrl aadressi uues aknas.
Saateleht on olemas alates tellimuse kinnitamisest (PLANNED); DRAFT tellimusel saatelehte pole.
```

## API märkmed — GET /api/jobs/{jobId}/waybill

```text
API: GET /api/jobs/{jobId}/waybill

WaybillDto.java
Response (200):
{
  "transportDocumentId": 1,
  "jobId": 4,
  "documentNumber": "ST-2026-0001",
  "senderName": "Mida Vaja OÜ",
  "senderAddress": "Peterburi tee 47, Tallinn",
  "receiverName": "Ants Asi",
  "receiverAddress": "Paldiski mnt 96, Tallinn",
  "carrierName": "LIFTERTRANS",
  "driverName": "Mart Tamm",
  "vehicleRegistrationNumber": "876HGF",
  "loadingDate": "2026-09-21T09:00:00",
  "deliveryDate": "2026-09-21T13:15:00",
  "generatedFileUrl": "/demo/waybills/ST-2026-0001.pdf",
  "cargo": [
    {
      "description": "Metallkonstruktsioonid",
      "quantity": 2,
      "weightKg": 5600.00
    },
    ...
  ]
}

API teenuse lisainfo:
Saatelehe päis (number, saatja, saaja, vedaja, kuupäevad, PDF-i aadress) tuleb tabelist transport_document (üks rida töö kohta, luuakse tellimuse kinnitamisel); juht, auto ja kaubaread töö seostest. generatedFileUrl on null, kuni PDF-i pole veel genereeritud.

Veateated:
HTTP: 403
errorCode: ACCESS_DENIED
message: "Sul puudub õigus selle töö andmetele"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```

## API märkmed — POST /api/jobs/{jobId}/waybill/pdf

```text
API: POST /api/jobs/{jobId}/waybill/pdf

WaybillPdfDto.java
Response (200):
{
  "generatedFileUrl": "/demo/waybills/ST-2026-0001.pdf"
}

API teenuse lisainfo:
Genereerib saatelehe PDF-i töö hetkeandmetest (transport_document, klient, juht, auto, kaubaread), salvestab selle kausta uploads/waybills/<documentNumber>.pdf (vana fail kirjutatakse üle) ja uuendab transport_document.generated_file_url väärtust.

Veateated:
HTTP: 403
errorCode: ACCESS_DENIED
message: "Sul puudub õigus selle töö andmetele"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```
