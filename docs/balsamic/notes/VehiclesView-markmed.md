# VehiclesView.vue - Balsamiq märkmed

Balsamiqu leht: **AUTOD**

## Vaate märkmed

```text
Roll: Admin
Failinimi: VehiclesView.vue
Frontend rada: /vehicles

Vaatega seotud lisainfo:
Lehe avamisel laetakse kõik autod (oma ja alltöövõtjate omad); otsinguriba filtreerib registreerimisnumbri või nime järgi.
Veerg "Auto omanik" = subcontractorName või "Liftertrans", kui subcontractorId puudub. Mõõdud tulevad andmebaasist meetrites.
"Kustuta" avab kinnitusakna ja kustutab auto. "+ Lisa auto" → /vehicles/new, "Vaata" → /vehicles/:id, "Muuda" → /vehicles/:id/edit.
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
Valikuline query parameeter search filtreerib registrationNumber või name järgi; valikuline subcontractorId filtreerib alltöövõtja autod. status: ACTIVE / IN_SERVICE / UNAVAILABLE / INACTIVE. subcontractorId = null → LIFTERTRANS-i oma auto.

Veateated: —
```

## API märkmed — DELETE /api/vehicles/{vehicleId}

```text
API: DELETE /api/vehicles/{vehicleId}

Response (200): NONE

API teenuse lisainfo:
Kustutab auto ID alusel pärast kinnitust kinnitusaknas koos auto tõstevõime ridadega (crane_capacity).

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'vehicleId' väärtusega: 99"
```
