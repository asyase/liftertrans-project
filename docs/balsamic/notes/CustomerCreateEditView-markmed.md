# CustomerCreateEditView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA KLIENT / MUUDA KLIENTI**

## Vaate märkmed

```text
Roll: Admin
Failinimi: CustomerCreateEditView.vue
Frontend rada: /customers/new ja /customers/:id/edit

Vaatega seotud lisainfo:
/customers/new → pealkiri "Lisa klient", tühjad väljad; /customers/:id/edit → pealkiri "Muuda klienti", väljad eeltäidetakse GET /api/customers/{customerId} abil.
Kohustuslikud on Nimi ja Telefon. Salvesta → POST (uus) või PUT (muutmine), seejärel suunatakse /customers; Tühista → tagasi ilma API kutseta.
Väljad: nimi, ettevõte, registrikood, KMKR, e-post, arve e-post (invoice_email) ja telefon.
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

## API märkmed — POST /api/customers

```text
API: POST /api/customers

CustomerRequestDto.java
Request body:
{
  "name": "Ants Asi",
  "companyName": "Mida Vaja OÜ",
  "companyRegistrationNumber": "12345678",
  "vatNumber": "EE123456789",
  "email": "ants@midavaja.ee",
  "invoiceEmail": "arved@midavaja.ee",
  "phone": "+3725559876"
}

Response (200): NONE

API teenuse lisainfo:
name ja phone on kohustuslikud (DB NOT NULL), ülejäänud valikulised. created_at = praegune aeg.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "name: must not be blank"
```

## API märkmed — PUT /api/customers/{customerId}

```text
API: PUT /api/customers/{customerId}

CustomerRequestDto.java
Request body:
{
  "name": "Ants Asi",
  "companyName": "Mida Vaja OÜ",
  "companyRegistrationNumber": "12345678",
  "vatNumber": "EE123456789",
  "email": "ants@midavaja.ee",
  "invoiceEmail": "arved@midavaja.ee",
  "phone": "+3725559876"
}

Response (200): NONE

API teenuse lisainfo:
Muudab kliendi andmeid; created_at ei muutu.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "name: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'customerId' väärtusega: 99"
```
