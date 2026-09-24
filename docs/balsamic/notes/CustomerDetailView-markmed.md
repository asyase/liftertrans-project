# CustomerDetailView.vue - Balsamiq märkmed

Balsamiqu leht: **KLIENT vaade**

## Vaate märkmed

```text
Roll: Admin
Failinimi: CustomerDetailView.vue
Frontend rada: /customers/:id

Vaatega seotud lisainfo:
Lehe avamisel laetakse kliendi andmed ja kliendiga seotud tööd (tabel "Kliendi tööd": kuupäev, töö tüüp, staatus).
"Kustuta" küsib kinnitust, eduka kustutamise järel suunatakse /customers.
"Muuda" → /customers/:id/edit, tööde tabeli "Vaata" → /jobs/:id (ainult navigeerimine).
```

## API märkmed — GET /api/customers/{customerId}

```text
API: GET /api/customers/{customerId}

CustomerDetailDto.java
Response (200):
{
  "customerId": 1,
  "name": "Ants Asi",
  "companyName": "Mida Vaja OÜ",
  "companyRegistrationNumber": "12345678",
  "vatNumber": "EE123456789",
  "email": "ants@midavaja.ee",
  "invoiceEmail": "arved@midavaja.ee",
  "phone": "+3725559876",
  "createdAt": "2026-09-16T10:00:00"
}

API teenuse lisainfo:
Tagastab ühe kliendi andmed ID alusel; kasutatakse kliendi detailvaates ja muutmise vormi eeltäitmiseks.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'customerId' väärtusega: 99"
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse customerId=<kliendi ID>.
Sama endpointi kasutavad dashboardi tänaste tööde tabel, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.
DRIVER rolliga kasutajale tagastatakse alati ainult talle määratud tööd: driverId võetakse autentitud kasutajast (accessToken), mitte query parameetrist.

Veateated: —
```

## API märkmed — DELETE /api/customers/{customerId}

```text
API: DELETE /api/customers/{customerId}

Response (200): NONE

API teenuse lisainfo:
Kustutab kliendi ID alusel pärast kasutaja kinnitust kinnitusaknas. Kui kliendiga on seotud töid (job.customer_id), klienti ei kustutata.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'customerId' väärtusega: 99"

HTTP: 409
errorCode: RESOURCE_IN_USE
message: "Klienti ei saa kustutada, sest temaga on seotud töid"
```
