# SubcontractorCreateEditView.vue - Balsamiq märkmed

Balsamiqu leht: **LISA / MUUDA ALLTÖÖVÕTJAT**

## Vaate märkmed

```text
Roll: Admin
Failinimi: SubcontractorCreateEditView.vue
Frontend rada: /subcontractors/new ja /subcontractors/:id/edit

Vaatega seotud lisainfo:
/subcontractors/new → "Lisa alltöövõtja"; /subcontractors/:id/edit → "Muuda alltöövõtjat", väljad eeltäidetakse GET /api/subcontractors/{subcontractorId} abil.
Salvestamisel tekib subcontractorId; alles seejärel saab "+ Lisa auto" kaudu alltöövõtjale autosid lisada (auto vormis seotakse auto alltöövõtjaga välja subcontractorId kaudu).
Salvesta → POST või PUT, seejärel /subcontractors; Tühista → tagasi ilma API kutseta.
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

## API märkmed — POST /api/subcontractors

```text
API: POST /api/subcontractors

SubcontractorRequestDto.java
Request body:
{
  "companyName": "Partner Transport OÜ",
  "companyRegistrationNumber": "11223344",
  "vatNumber": "EE112233445",
  "contactName": "Karl Saar",
  "phone": "+3725557777",
  "email": "info@partnertransport.ee",
  "notes": "Kasutada vajadusel alltöövõtjana.",
  "active": true
}

SubcontractorCreateResponseDto.java
Response (200):
{
  "subcontractorId": 1
}

API teenuse lisainfo:
companyName on kohustuslik (DB NOT NULL). subcontractorId tagastatakse, et pärast salvestamist saaks alltöövõtjale autosid lisada.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "companyName: must not be blank"
```

## API märkmed — PUT /api/subcontractors/{subcontractorId}

```text
API: PUT /api/subcontractors/{subcontractorId}

SubcontractorRequestDto.java
Request body:
{
  "companyName": "Partner Transport OÜ",
  "companyRegistrationNumber": "11223344",
  "vatNumber": "EE112233445",
  "contactName": "Karl Saar",
  "phone": "+3725557777",
  "email": "info@partnertransport.ee",
  "notes": "Kasutada vajadusel alltöövõtjana.",
  "active": true
}

Response (200): NONE

API teenuse lisainfo:
Muudab alltöövõtja andmeid; companyName on kohustuslik. active = false alltöövõtjat ei pakuta uute tööde valikus.

Veateated:
HTTP: 400
errorCode: INCORRECT_INPUT
message: "companyName: must not be blank"

HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'subcontractorId' väärtusega: 99"
```
