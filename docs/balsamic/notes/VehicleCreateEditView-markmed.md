# VehicleCreateEditView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA AUTO / MUUDA AUTOT**

## Vaate märkmed

```text
Roll: Admin
Failinimi: VehicleCreateEditView.vue
Frontend rada: /vehicles/new ja /vehicles/:id/edit

Vaatega seotud lisainfo:
/vehicles/new → "Lisa auto" (tühi vorm); /vehicles/:id/edit → "Muuda autot", väljad ja tõstevõime read eeltäidetakse GET /api/vehicles/{vehicleId} abil.
Tõstevõime ridu lisatakse ("+ Lisa tõstevõime" → Lisa) ja kustutatakse vormis; need salvestatakse koos autoga (VEHICLE 1:N CRANE_CAPACITY), vehicle.crane_capacity_kg / crane_reach_m välju ei kasutata.
Salvesta → POST või PUT, seejärel tagasi /vehicles; Tühista → tagasi ilma API kutseta.
/vehicles/new?subcontractorId=:id (avatakse alltöövõtja detailvaatest) lisab POST päringusse subcontractorId; /vehicles/new ilma parameetrita → oma auto (subcontractorId = null).
```

## API märkmed — GET /api/vehicles/{vehicleId}

```text
API: GET /api/vehicles/{vehicleId}

VehicleDetailDto.java
Response (200):
{
  "vehicleId": 1,
  "registrationNumber": "876HGF",
  "name": "MAN TGS 35.480",
  "maxCargoWeightKg": 18000.00,
  "platformLengthM": 8.50,
  "platformWidthM": 2.50,
  "vehicleLengthM": 9.60,
  "vehicleWidthM": 2.55,
  "vehicleHeightM": 3.90,
  "status": "ACTIVE",
  "subcontractorId": null,
  "subcontractorName": null,
  "craneCapacities": [
    {
      "craneCapacityId": 1,
      "reachM": 5.00,
      "maxWeightKg": 8000.00
    },
    ...
  ]
}

API teenuse lisainfo:
craneCapacities tuleb tabelist crane_capacity (VEHICLE 1:N), järjestatud reach_m järgi. Mõõdud on andmebaasis meetrites.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'vehicleId' väärtusega: 99"
```

## API märkmed — POST /api/vehicles

```text
API: POST /api/vehicles

VehicleRequestDto.java
Request body:
{
  "registrationNumber": "876HGF",
  "name": "MAN TGS 35.480",
  "maxCargoWeightKg": 18000.00,
  "platformLengthM": 8.50,
  "platformWidthM": 2.50,
  "vehicleLengthM": 9.60,
  "vehicleWidthM": 2.55,
  "vehicleHeightM": 3.90,
  "status": "ACTIVE",
  "subcontractorId": null,
  "craneCapacities": [
    {
      "reachM": 5.00,
      "maxWeightKg": 8000.00
    },
    ...
  ]
}

Response (200): NONE

API teenuse lisainfo:
registrationNumber ja status on kohustuslikud, registrationNumber on unikaalne (DB). craneCapacities read salvestatakse tabelisse crane_capacity; vehicle.crane_capacity_kg / crane_reach_m välju ei kasutata.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "registrationNumber: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'subcontractorId' väärtusega: 5"

HTTP: 409
errorCode: DUPLICATE_RESOURCE
message: "Sama registreerimisnumbriga auto on juba olemas"
```

## API märkmed — PUT /api/vehicles/{vehicleId}

```text
API: PUT /api/vehicles/{vehicleId}

VehicleRequestDto.java
Request body:
{
  "registrationNumber": "876HGF",
  "name": "MAN TGS 35.480",
  "maxCargoWeightKg": 18000.00,
  "platformLengthM": 8.50,
  "platformWidthM": 2.50,
  "vehicleLengthM": 9.60,
  "vehicleWidthM": 2.55,
  "vehicleHeightM": 3.90,
  "status": "ACTIVE",
  "subcontractorId": null,
  "craneCapacities": [
    {
      "reachM": 5.00,
      "maxWeightKg": 8000.00
    },
    ...
  ]
}

Response (200): NONE

API teenuse lisainfo:
craneCapacities massiiv asendab auto olemasolevad tõstevõime read täielikult. Iga (vehicle_id, reach_m) paar peab olema unikaalne.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "registrationNumber: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'vehicleId' väärtusega: 99"

HTTP: 409
errorCode: DUPLICATE_RESOURCE
message: "Sama registreerimisnumbriga auto on juba olemas"
```
