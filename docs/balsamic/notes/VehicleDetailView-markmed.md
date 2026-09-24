# VehicleDetailView.vue - Balsamiq märkmed

Balsamiqu leht: **AUTO detailvaade**

## Vaate märkmed

```text
Roll: Admin
Failinimi: VehicleDetailView.vue
Frontend rada: /vehicles/:id

Vaatega seotud lisainfo:
Lehe avamisel laetakse auto andmed koos kraana tõstevõime tabeliga (tõste ulatus m → maksimaalne kaal kg); tõste ulatus on horisontaalne kaugus tõstetavast objektist.
"Kustuta" küsib kinnitust, eduka kustutamise järel suunatakse /vehicles. "Muuda" → /vehicles/:id/edit (ainult navigeerimine).
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
