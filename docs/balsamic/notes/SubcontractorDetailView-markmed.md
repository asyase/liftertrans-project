# SubcontractorDetailView.vue - Balsamiq märkmed

Balsamiqu leht: **ALLTÖÖVÕTJA detailvaade**

## Vaate märkmed

```text
Roll: Admin
Failinimi: SubcontractorDetailView.vue
Frontend rada: /subcontractors/:id

Vaatega seotud lisainfo:
Lehe avamisel laetakse alltöövõtja andmed, talle määratud tööd ja tema autod (VEHICLE.subcontractor_id). SUBCONTRACTED töödel LIFTERTRANS juhti ei määra.
Autode tabelis "Vaata" → /vehicles/:id, "Muuda" → /vehicles/:id/edit, "Kustuta" küsib kinnitust ja kustutab auto.
"Kustuta" (alltöövõtja) küsib kinnitust ja suunab /subcontractors; "Muuda" → /subcontractors/:id/edit.
"+ Lisa auto" → /vehicles/new?subcontractorId=:id — sama auto vorm, auto seotakse selle alltöövõtjaga.
```

## API märkmed — GET /api/subcontractors/{subcontractorId}

```text
API: GET /api/subcontractors/{subcontractorId}

SubcontractorDto.java
Response (200):
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
}

API teenuse lisainfo:
Tagastab ühe alltöövõtja andmed ID alusel; kasutatakse detailvaates ja muutmise vormi eeltäitmiseks.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'subcontractorId' väärtusega: 99"
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
Tagastab tööde nimekirja valikuliste query parameetrite järgi (date, from, to, vehicleId, driverId, customerId, subcontractorId, status), järjestatuna plannedStartTime järgi; tulemusi pole → tühi massiiv. Selles vaates saadetakse subcontractorId=<alltöövõtja ID>.
Sama endpointi kasutavad dashboardi tänaste tööde tabel, kalender, tellimuste nimekiri, kliendi/juhi/alltöövõtja detailvaated ja juhi töölaud.
DRIVER rolliga kasutajale tagastatakse alati ainult talle määratud tööd: driverId võetakse autentitud kasutajast (accessToken), mitte query parameetrist.

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
Selles vaates saadetakse subcontractorId=<alltöövõtja ID>. status: ACTIVE / IN_SERVICE / UNAVAILABLE / INACTIVE. subcontractorId = null → LIFTERTRANS-i oma auto.

Veateated: —
```

## API märkmed — DELETE /api/subcontractors/{subcontractorId}

```text
API: DELETE /api/subcontractors/{subcontractorId}

Response (200): NONE

API teenuse lisainfo:
Kustutab alltöövõtja ID alusel pärast kinnitust kinnitusaknas. Kui alltöövõtjaga on seotud töid või autosid, alltöövõtjat ei kustutata.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'subcontractorId' väärtusega: 99"

HTTP: 409
errorCode: RESOURCE_IN_USE
message: "Alltöövõtjat ei saa kustutada, sest temaga on seotud töid või autosid"
```

## API märkmed — DELETE /api/vehicles/{vehicleId}

```text
API: DELETE /api/vehicles/{vehicleId}

Response (200): NONE

API teenuse lisainfo:
Kustutab auto ID alusel pärast kinnitust kinnitusaknas koos auto tõstevõime ridadega (crane_capacity). Kui autoga on seotud töid (job.vehicle_id), autot ei kustutata.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'vehicleId' väärtusega: 99"

HTTP: 409
errorCode: RESOURCE_IN_USE
message: "Autot ei saa kustutada, sest sellega on seotud töid"
```
