# SubcontractorsView.vue - Balsamiq märkmed

Balsamiqu leht: **ALLTÖÖVÕTJAD**

## Vaate märkmed

```text
Roll: Admin
Failinimi: SubcontractorsView.vue
Frontend rada: /subcontractors

Vaatega seotud lisainfo:
Lehe avamisel laetakse kõik alltöövõtjad; otsinguriba filtreerib ettevõtte nime järgi.
"Kustuta" avab kinnitusakna ja kustutab alltöövõtja. "+ Lisa alltöövõtja" → /subcontractors/new, "Vaata" → /subcontractors/:id, "Muuda" → /subcontractors/:id/edit.
```

## API märkmed — GET /api/subcontractors

```text
API: GET /api/subcontractors

SubcontractorDto.java
Response (200):
[
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
  },
  ...
]

API teenuse lisainfo:
Valikuline query parameeter search filtreerib ettevõtte nime järgi; ilma selleta tagastatakse kõik alltöövõtjad.

Veateated: —
```

## API märkmed — DELETE /api/subcontractors/{subcontractorId}

```text
API: DELETE /api/subcontractors/{subcontractorId}

Response (200): NONE

API teenuse lisainfo:
Kustutab alltöövõtja ID alusel pärast kinnitust kinnitusaknas.

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'subcontractorId' väärtusega: 99"
```
