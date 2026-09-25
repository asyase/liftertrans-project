# LoginView.vue - Balsamiq märkmed

Balsamiqu leht: **LOGIN**

## Vaate märkmed

```text
Roll: Külastaja (pole sisse logitud) — sisse saavad logida ADMIN ja DRIVER rolliga kasutajad
Failinimi: LoginView.vue
Frontend rada: /login

Vaatega seotud lisainfo:
Leht on esimene vaade sisselogimisel, menüüd lehel ei ole. Väli "Kasutajanimi / e-post" saadetakse backendile email väljana — kasutajanimega sisse logida ei saa.
Enne saatmist kontrollitakse, kas väljad on täidetud — kui ei ole, kuvatakse veateade "E-post on kohustuslik" või "Parool on kohustuslik". Kui backend vastab errorCode'ga INCORRECT_CREDENTIALS, kuvatakse backendi message väli ("Vale e-post või parool").
Eduka sisselogimise korral salvestatakse accessToken, roleName ja driverId sessionStorage'isse ning kasutaja suunatakse rolli järgi: ADMIN → /dashboard, DRIVER → /my-jobs. Kõik järgmised päringud saadavad accessTokeni päises Authorization: Bearer.
```

## API märkmed — POST /api/auth/login

```text
API: POST /api/auth/login

AuthRequestDto.java
Request body:
{
  "email": "admin@liftertrans.ee",
  "password": "admin123"
}

AuthResponseDto.java
Response (200):
{
  "userId": 1,
  "email": "admin@liftertrans.ee",
  "roleName": "ADMIN",
  "driverId": null,
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}

API teenuse lisainfo:
Kasutaja otsitakse email järgi ja parooli kontrollitakse BCrypt räsiga (user tabeli password_hash). roleName on "ADMIN" või "DRIVER". driverId on ADMIN kasutajal null, DRIVER kasutajal täidetud (nt mart.tamm@liftertrans.ee → "driverId": 1). accessToken (allkirjastatud JWT, piiratud kehtivusega) saadetakse kõigis järgmistes päringutes päises Authorization: Bearer <token>; backend määrab sellest kasutaja rolli ja driverId ning kontrollib õigusi.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "email: must not be blank"

HTTP: 401
errorCode: INCORRECT_CREDENTIALS
message: "Vale e-post või parool"
```
