# DriversView.vue - Balsamiq märkmed

Balsamiqu leht: **JUHID**

## Vaate märkmed

```text
Roll: Admin
Failinimi: DriversView.vue
Frontend rada: /drivers

Vaatega seotud lisainfo:
Lehe avamisel laetakse kõik juhid; otsinguriba filtreerib nime järgi. Staatus: active = true → "Aktiivne", false → "Mitteaktiivne".
"Kustuta" avab kinnitusakna ja kustutab juhi. "+ Lisa juht" → /drivers/new, "Vaata" → /drivers/:id, "Muuda" → /drivers/:id/edit.
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
Valikuline query parameeter search filtreerib nime järgi; ilma selleta tagastatakse kõik juhid.

Veateated: —
```

## API märkmed — DELETE /api/drivers/{driverId}

```text
API: DELETE /api/drivers/{driverId}

Response (200): NONE

API teenuse lisainfo:
Kustutab juhi ID alusel pärast kinnitust kinnitusaknas. Kui juhiga on seotud töid (job.driver_id), juhti ei kustutata — mitteaktiivseks muutmiseks kasutatakse active = false.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'driverId' väärtusega: 99"

HTTP: 409
errorCode: RESOURCE_IN_USE
message: "Juhti ei saa kustutada, sest temaga on seotud töid"
```
