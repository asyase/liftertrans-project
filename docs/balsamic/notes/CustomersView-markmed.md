# CustomersView.vue - Balsamiq märkmed

Balsamiqu leht: **KLIENDID vaade**

## Vaate märkmed

```text
Roll: Admin
Failinimi: CustomersView.vue
Frontend rada: /customers

Vaatega seotud lisainfo:
Lehe avamisel laetakse kõik kliendid; otsinguriba saadab search parameetri (nimi või ettevõtte nimi) ja laadib nimekirja uuesti.
"Kustuta" avab kinnitusakna, kinnitamisel kustutatakse klient ja nimekiri laetakse uuesti.
"+ Lisa klient" → /customers/new, "Vaata" → /customers/:id, "Muuda" → /customers/:id/edit (ainult navigeerimine).
```

## API märkmed — GET /api/customers

```text
API: GET /api/customers

CustomerDto.java
Response (200):
[
  {
    "customerId": 1,
    "name": "Ants Asi",
    "companyName": "Mida Vaja OÜ",
    "companyRegistrationNumber": "12345678",
    "vatNumber": "EE123456789",
    "email": "ants@midavaja.ee",
    "invoiceEmail": "arved@midavaja.ee",
    "phone": "+3725559876"
  },
  ...
]

API teenuse lisainfo:
Valikuline query parameeter search filtreerib name või company_name järgi; ilma selleta tagastatakse kõik kliendid. Tulemusi pole → tühi massiiv (mitte 404).

Veateated: —
```

## API märkmed — DELETE /api/customers/{customerId}

```text
API: DELETE /api/customers/{customerId}

Response (200): NONE

API teenuse lisainfo:
Kustutab kliendi ID alusel pärast kasutaja kinnitust kinnitusaknas.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'customerId' väärtusega: 99"
```
