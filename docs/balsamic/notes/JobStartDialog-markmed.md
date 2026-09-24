# JobStartDialog.vue - Balsamiq märkmed

Balsamiqu leht: **ALUSTA TÖÖD**

## Vaate märkmed

```text
Roll: Juht (DRIVER)
Failinimi: JobStartDialog.vue
Frontend rada: Eraldi route puudub (modaalaken)

Vaatega seotud lisainfo:
Modaalaken avaneb "Alusta töö" nupust vaadetes /my-jobs ja /my-jobs/:id; kuvab juba laetud töö andmeid (klient, töö, pealevõtu aadress, planeeritud algus) ilma eraldi GET kutseta.
"Alusta töö" → töö staatus IN_PROGRESS ja actual_start_time salvestatakse automaatselt; aken sulgub ja taustavaate andmed laetakse uuesti. "Tühista" sulgeb akna ilma API kutseta.
```

## API märkmed — POST /api/jobs/{jobId}/start

```text
API: POST /api/jobs/{jobId}/start

Response (200): NONE

API teenuse lisainfo:
Lubatud ainult PLANNED tööl: salvestab actual_start_time = praegune aeg, staatus → IN_PROGRESS ja job_status_history rea (changed_by = juhi e-post).

Veateated:
HTTP: 404
errorCode: PRIMARY_KEY_NOT_FOUND
message: "Ei leidnud primary keyd 'jobId' väärtusega: 99"
```
