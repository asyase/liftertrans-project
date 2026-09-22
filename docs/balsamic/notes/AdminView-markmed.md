# AdminView.vue - Balsamiq märkmed

## Vaate märkmed

Roll: Admin

Failinimi: AdminView.vue

Frontend rada:
/dashboard

Vaatega seotud lisainfo:

Admin dashboard avaneb ADMIN rolliga kasutajale kohe pärast edukat sisselogimist.

Desktop-vaates asub vasakul külgmenüü:
- Esileht
- Kalender
- Tellimused
- Kliendid
- Autod
- Juhid
- Alltöövõtjad
- Logi välja

Mobiilivaates kasutatakse kompaktsemat navigatsiooni.

Dashboardi ülaosas kuvatakse tänase päeva tööde loendurid:
- Tänased tööd
- Planeeritud
- Töös
- Lõpetatud

"Tänased tööd" loendur sisaldab kõiki töid, mille planned_start_time kuupäev on tänane kuupäev, sõltumata staatusest.

"Planeeritud" loendur sisaldab ainult PLANNED staatusega tänaseid töid.

"Töös" loendur sisaldab ainult IN_PROGRESS staatusega tänaseid töid.

"Lõpetatud" loendur sisaldab ainult COMPLETED staatusega tänaseid töid.

DRAFT staatusega töö kuulub "Tänased tööd" koguarvu sisse, kuid sellele eraldi loendurikaarti ei kuvata.

Tänaste tööde tabel kuvab ainult töid, mille planned_start_time kuupäev on tänane kuupäev.

Tööd kuvatakse planned_start_time järgi kasvavas järjekorras.

Desktop-vaates kuvatakse tabelis:
- Kellaaeg
- Klient
- Töö tüüp
- Auto
- Juht
- Staatus
- Tegevus

Mobiilivaates kuvatakse lihtsustatud tabel:
- Kellaaeg
- Klient
- Staatus

Frontend kuvab API enum väärtused kasutajasõbralike nimetustega:

Töö staatus:
- DRAFT -> Uus
- PLANNED -> Planeeritud
- IN_PROGRESS -> Töös
- COMPLETED -> Lõpetatud

Töö tüüp:
- TRANSPORT_AND_CRANE -> Transport + kraanatöö
- CRANE_ONLY -> Kraanatöö

Eraldiseisvat "Transport" töö tüüpi süsteemis ei ole.

Nupp "+ Lisa uus tellimus" suunab uue tellimuse lisamise vormile.

Tabeli rea tegevus "Vaata" suunab valitud töö detailvaatesse.

"Vaata kogu kalendrit" suunab Kalender vaatele.

Kalendris kuvatakse töödega kuupäevad ning kuupäevale vajutades kuvatakse valitud päeva tööd.