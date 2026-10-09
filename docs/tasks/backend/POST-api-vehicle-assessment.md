# Task: POST /api/vehicle-assessment — veose ja tõste sobivuse eelhinnang

## Kirjeldus

Luua väliskliendile mõeldud vestluslik päring, mille kaudu klient kirjeldab kauba kaalu
ja mõõtmeid ning soovitud tõstekõrgust. Süsteem kasutab küsimusest vajalike andmete
leidmiseks AI-d ja võrdleb veoseandmeid andmebaasis olevate sõidukite andmetega. Vastus
peab arusaadavalt ütlema, kas andmete põhjal leidub sobiv sõiduk, kas kehtib eriloa või
saateauto reegel või vajab juhtum käsitsi kontrolli.

Selles taskis käsitletakse andmebaasi sõidukiandmeid pärisandmetena. Andmebaasi kraana
võimekuse andmed on praegu katsetuslikud ega sobi tõstevõime kinnitamiseks. Seetõttu ei
tohi süsteem kraana võimekuse või soovitud tõstekõrguse põhjal anda kindlat lubadust, et
tõstmine on võimalik. Sellisel juhul peab vastus selgelt ütlema, et tõstevõime vajab
käsitsi kinnitamist.

See on sobivuse **eelhinnang**, mitte lõplik hinnapakkumine, transpordiluba ega töö
teostatavuse kinnitus.

## Endpoint

Soovituslik uus endpoint: `POST /api/vehicle-assessment`

Olemasolev `POST /api/ask` teeb üldotstarbelise loomuliku keele päringu teisendamise
SQL-iks ning tagastab andmebaasi kokkuvõtte. Kliendile mõeldud sobivushinnang peab olema
eraldi lepinguga ega tohi avaldada genereeritud SQL-i või muid sisemisi tehnilisi
detaile.

### Request

Request sisaldab kliendi loomulikus keeles küsimust. Esialgne pikkuse ülempiir on 500
tähemärki, kooskõlas olemasoleva `AiAskRequestDto` piiranguga.

```json
{
  "question": "2,4 m pikk, 1,2 m lai ja 800 kg kaup; vaja tõsta 10 m kõrgusele."
}
```

Küsimus on kohustuslik ega tohi olla ainult tühikutest. Kui AI ei leia hindamiseks
vajalikke andmeid, tuleb need märkida puuduvaks ning kliendilt täpsustust küsida;
väärtusi ei tohi oletada.

### Response (200)

Vastuse struktuur peab olema fikseeritud ja API DTO-ga kirjeldatud. Järgnev on
soovituslik kuju:

```json
{
  "status": "NEEDS_MANUAL_REVIEW",
  "answer": "Veos võib sobida, kuid kraanaga tõstmine vajab käsitsi kinnitamist.",
  "vehicles": [
    {
      "registrationNumber": "123ABC",
      "name": "Sõiduki mudel"
    }
  ],
  "overhangM": 0.0,
  "specialPermitRequired": false,
  "escortVehiclesRequired": 0,
  "craneAssessment": "MANUAL_CONFIRMATION",
  "missingInformation": []
}
```

Võimalikud `status` väärtused:

| Väärtus | Tähendus |
|---|---|
| `POSSIBLE` | Sõiduki andmed vastavad hinnatavate transpordipiirangute tingimustele; kraanaga tõstmine ei ole selle staatusega automaatselt kinnitatud. |
| `NOT_POSSIBLE` | Ükski aktiivne sõiduk ei vasta andmebaasis olevate transpordipiirangute alusel veose kaalule või platvormi mõõtudele. |
| `NEED_MORE_INFORMATION` | Küsimusest puudub üks või mitu vajalikku sisendandmetest ning kliendilt tuleb neid küsida. |
| `NEEDS_MANUAL_REVIEW` | Juhtum ületab määratud automaatse hindamise piire või nõuab kraana/tõstekõrguse käsitsi kinnitamist. |

`vehicles` sisaldab ainult hindamise seisukohast sobivaid aktiivseid sõidukeid. Kui
sobivaid sõidukeid ei leita, on väärtuseks tühi massiiv. `answer` on kliendile mõeldud
eestikeelne selgitus. Vastuses ei väljastata SQL-i.

### Valideerimis- ja veateated

| HTTP | Olukord |
|---|---|
| 400 | Puuduv, tühi või üle lubatud pikkuse küsimus; kasutada projekti tavapärast valideerimise veavormingut. |
| 500 | AI teenuse või andmebaasi ootamatu rike; tagastada projekti tavapärane veavorming, mitte väljamõeldud sobivushinnang. |

Kui küsimus on sisult arusaamatu või vajalikud mõõdud/kaal puuduvad, on see edukas
vestlusvastus `NEED_MORE_INFORMATION`, mitte serveri viga.

## Hindamise ärireeglid

1. Sõiduki valikul arvestatakse ainult `ACTIVE` staatusega sõidukeid.
2. Veose kaal ei tohi ületada sõiduki `max_cargo_weight_kg` väärtust.
3. Veose mõõte võrreldakse sõiduki platvormi pikkuse ja laiusega (`platform_length_m`,
   `platform_width_m`). Hindamine peab kasutama andmebaasist saadud väärtusi ega tohi
   lasta AI-l sõidukiandmeid välja mõelda.
4. Pikisuunaline üleulatus arvutatakse veose pikkuse ja sõiduki platvormi pikkuse
   vahena; kui veos platvormist pikem ei ole, on üleulatus 0 m. Käesoleva taski
   üleulatuse reeglid kehtivad pikkusele, mitte laiusele.
5. Pikisuunalise üleulatuse reeglid:

| Üleulatus | Tulemus |
|---|---|
| 0 m | Eriloa ega saateauto nõuet selle reegli alusel ei teki. |
| Üle 0 m kuni 2 m (kaasa arvatud) | `specialPermitRequired = true`; saateautode arv on 0. |
| Üle 2 m kuni 5 m (kaasa arvatud) | `escortVehiclesRequired = 1`; eriloa vajadust ei tuletata selle reegli põhjal automaatselt. |
| Üle 5 m | Automaatset sobivusotsust ei anta; `NEEDS_MANUAL_REVIEW`. |

6. Kui veose laius ületab platvormi laiuse või mõõtmeid ei saa kindlalt platvormile
   sobitada, ei tohi pikkuse üleulatuse reegleid laiusele üle kanda. Juhtum märgitakse
   käsitsi kontrollitavaks, kuni laiuse käsitlemise reeglid on kinnitatud.
7. Kraana tabeli `reach_m` ja `max_weight_kg` väärtused on katsetuslikud. Neid ei
   kasutata tõstevõime lubaduse andmiseks. Kui klient vajab tõstmist, sh kirjeldab
   soovitud tõstekõrgust, märgitakse `craneAssessment = "MANUAL_CONFIRMATION"` ja
   vastuses öeldakse selgelt, et tõstevõime tuleb üle kontrollida.
8. Sõiduki transpordisobivus ja kraanaga tõstmise sobivus on vastuses eristatavad.
   Sobiv veok ei tähenda automaatselt, et kraanaga tõstmine on teostatav.

## AI ja prompti nõuded

1. AI kasutab andmebaasist saadud sõidukiandmeid ehk grounding'ut; mudel ei tohi ise
   sõiduki omadusi, hindu, lube ega ohutustingimusi juurde mõelda.
2. Ärireeglite rakendamine ja mõõtude/kaalu võrdlus peavad olema kontrollitavad. AI
   sobib kliendi küsimusest sisendite eraldamiseks ja tulemuse loomulikus keeles
   selgitamiseks, mitte ainsaks sobivusotsuse tegijaks.
3. Kasutada tuleb eraldi süsteemi- ja kasutajaprompti rolle. Kasutaja küsimust
   käsitletakse usaldamatu sisendina, mitte süsteemijuhiste muutmise käsuna.
4. Prompt tuleb hoida eraldi eestikeelses Markdown-failis, näiteks
   `backend/src/main/resources/prompts/vehicle-assessment-system-prompt.md`, mitte Java
   koodis tekstikonstandina.
5. Prompt peab sisaldama vähemalt ühte näidet ehk one-shot/few-shot näiteid, sealhulgas
   sobiva sõiduki juhtumit ning üle 5 m üleulatuse või tõstmist vajava juhtumi käsitsi
   kontrolli.
6. Mudelilt küsitakse ainult lühikest kasutajale sobivat põhjendust; sisemist Chain of
   Thought arutluskäiku ei väljastata ega salvestata API vastusena.
7. API vastus on struktureeritud ning valideeritakse enne kliendile väljastamist.
   Mudeli vigane või puudulik väljund ei tohi muutuda edukaks sobivushinnanguks.
8. Sisendile rakendatakse pikkuse ja tühja väärtuse kontrolle. Prompti ja mudeli
   vastust ei renderdata frontendis usaldatud HTML-ina. Vastusesse ei lisata SQL-i,
   sisemist prompti ega tundlikke andmeid.
9. Mudeli seadistused, sh sampling-parameetrid ja teenuse toetatud thinking/reasoning
   tase, peavad olema teadlikult valitud ning konfiguratsioonist juhitavad, kui
   kasutatav Spring AI mudel neid toetab. Kehtestada piirang mudeli vastuse pikkusele.
10. LLM-as-a-judge on boonus, mitte sobivuse ärireeglite asendus ega automaatse otsuse
    eeltingimus. Kui see lisatakse, võib see hinnata vastuse selgust ja reeglitele
    vastavust; ärireeglite õigsuse määravad testid.

## Seotud olemasolevad failid

- `backend/src/main/java/ee/liftertrans/controller/AiController.java` — olemasolev
  `POST /api/ask`; see task lisab soovituslikult eraldi kliendipäringu endpointi.
- `backend/src/main/java/ee/liftertrans/service/NlToSqlService.java` — olemasolev
  NL-to-SQL teenus; seda ei tohi muuta nii, et kliendipäring saaks ligipääsu üldisele
  SQL-i genereerimise voole.
- `backend/src/main/java/ee/liftertrans/dto/AiAskRequestDto.java` — olemasolev küsimuse
  valideerimise näide (`@NotBlank`, kuni 500 tähemärki).
- `backend/src/main/java/ee/liftertrans/dto/AiAskResponseDto.java` — olemasolev üldise
  AI päringu vastuse DTO; uue endpointi vastus vajab sobivushinnangut kirjeldavat
  fikseeritud struktuuri.
- `docs/tasks/backend/POST-api-ask-sql-vastuses.md` — olemasolev `/api/ask` SQL-välja
  task; see on eraldi funktsionaalsus.

## Vastuvõtu kriteeriumid

- [ ] Väliskliendi jaoks on olemas dokumenteeritud sobivushinnangu endpoint; üldise
  `/api/ask` SQL-vastuse leping jääb sellest eraldi.
- [ ] Requesti küsimus on kohustuslik ning kuni 500 tähemärki; vigase sisendi korral
  tagastub 400 projekti tavapärases vormingus.
- [ ] Kui kaal, pikkus või laius puudub, ei oletata puuduvat väärtust; vastus küsib
  täpsustust ning tagastab `NEED_MORE_INFORMATION`.
- [ ] Sõiduki otsus kasutab andmebaasi sõidukiandmeid ja valib ainult `ACTIVE`
  sõidukeid.
- [ ] Sõidukit ei märgita transpordiks sobivaks, kui veose kaal ületab selle
  kandevõimet või mõõdud ei mahu hinnatavate platvormipiiride sisse, arvestades allpool
  kirjeldatud üleulatuse juhtumeid.
- [ ] Üle 0 m kuni 2 m (kaasa arvatud) pikisuunalise üleulatuse korral märgitakse
  eriloa vajadus.
- [ ] Üle 2 m kuni 5 m (kaasa arvatud) pikisuunalise üleulatuse korral märgitakse ühe
  saateauto vajadus.
- [ ] Täpselt 2 m üleulatus kuulub eriloa vahemikku; täpselt 5 m kuulub ühe saateauto
  vahemikku.
- [ ] Üle 5 m üleulatuse korral tagastatakse `NEEDS_MANUAL_REVIEW`, mitte automaatne
  positiivne hinnang.
- [ ] Laiuse ületamisele ei rakendata pikkuse üleulatuse reegleid; määratlemata juhtum
  suunatakse käsitsi kontrolli.
- [ ] Kraana katsetusandmete põhjal ei anta tõstmise kohta kindlat „saab“ lubadust;
  tõstmist vajava päringu vastuses märgitakse käsitsi kinnitamise vajadus.
- [ ] Vastus sisaldab fikseeritud struktuuriga staatust, kliendile mõeldud selgitust,
  sobivaid sõidukeid, üleulatuse/loa/saateauto hinnangut, kraana hinnangu staatust ja
  vajadusel puuduvaid andmeid.
- [ ] AI ei tagasta SQL-i, süsteemiprompti ega andmebaasist küsimusega mitteseotud
  andmeid.
- [ ] Prompt asub Markdown-failis, sisaldab süsteemi- ja kasutajarolli juhiseid ning
  näiteid.
- [ ] Prompti injection'i katse ei muuda ärireegleid ega anna välja keelatud andmeid.
- [ ] AI või andmebaasi tõrke korral tagastub veavastus; süsteem ei anna väljamõeldud
  edukat hinnangut.
- [ ] Automaat- ja/või integratsioonitestid katavad puuduva sisendi, kaalu- ja
  platvormipiirid, aktiivse sõiduki valiku, üleulatuse piirid (0 m, 2 m, üle 2 m, 5 m,
  üle 5 m), käsitsi kontrolli ning AI vigase vastuse.

## Avatud küsimused

- Kas uus kliendipäring jääb eraldi endpointiks `POST /api/vehicle-assessment` või
  soovitakse muuta olemasolevat `POST /api/ask`? Eraldi endpoint on soovituslik, sest
  praegune `/api/ask` genereerib SQL-i ning selle kasutajavoog on ADMIN-i andmepäring.
- Kas endpoint peab olema avalik või nõuab autentimist/rate limit'it? Praegune
  `/api/ask` ei kontrolli backendis ADMIN rolli; uue väliskliendi endpointi ligipääs
  tuleb turvaliselt määratleda enne avalikku kasutust.
- Kas veose mõõte võib platvormile paigutamisel pöörata? Task eeldab, et sobivust
  kontrollitakse pikkuse ja laiuse mõistliku orientatsiooniga, kuid täpne
  pööramise/koorma paigutamise reegel tuleb enne lõplikku rakendamist kinnitada.
- Milline on ametlik eriloa käsitlus laiuse ületamisel? Käesolev ülesanne suunab
  määratlemata laiusejuhtumid käsitsi kontrolli.
- Kas klient sisestab küsimuse vabatekstina või peaks tulevikus kasutajaliides mõõdud
  ja kaalu eraldi väljadena küsima? Käesolev endpoint kasutab vabatekstilist küsimust.
