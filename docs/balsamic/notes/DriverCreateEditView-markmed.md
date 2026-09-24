# DriverCreateEditView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA / MUUDA JUHTI**

## Vaate märkmed

```text
Roll: Admin
Failinimi: DriverCreateEditView.vue
Frontend rada: /drivers/new ja /drivers/:id/edit

Vaatega seotud lisainfo:
/drivers/new → "Lisa juht"; /drivers/:id/edit → "Muuda juhti", väljad eeltäidetakse GET /api/drivers/{driverId} abil.
Staatus Aktiivne / Mitteaktiivne salvestatakse väljana active (boolean). Salvesta → POST või PUT, seejärel /drivers; Tühista → tagasi ilma API kutseta.
Eesnimi ja perekonnanimi liidetakse backendile saatmisel üheks väljaks name (driver.name).
Juhi lisamine ei loo sisselogimiskontot: DRIVER kontod (e-post ja parool) on user tabelis eelnevalt määratud ja seotud juhiga välja user.driver_id kaudu.
```

## API märkmed — GET /api/drivers/{driverId}

```text
API: GET /api/drivers/{driverId}

DriverDto.java
Response (200):
{
  "driverId": 1,
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}

API teenuse lisainfo:
Andmebaasis on juhi nimi ühes väljas driver.name (ees- ja perekonnanimi eraldi puuduvad).

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'driverId' väärtusega: 99"
```

## API märkmed — POST /api/drivers

```text
API: POST /api/drivers

DriverRequestDto.java
Request body:
{
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}

Response (200): NONE

API teenuse lisainfo:
Loob juhi tabelisse driver; name ja phone on kohustuslikud.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "name: must not be blank"
```

## API märkmed — PUT /api/drivers/{driverId}

```text
API: PUT /api/drivers/{driverId}

DriverRequestDto.java
Request body:
{
  "name": "Mart Tamm",
  "phone": "+3725551111",
  "email": "mart.tamm@liftertrans.ee",
  "active": true
}

Response (200): NONE

API teenuse lisainfo:
Muudab juhi andmeid; active = false juhti ei pakuta uute tööde juhi valikus.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "name: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'driverId' väärtusega: 99"
```
