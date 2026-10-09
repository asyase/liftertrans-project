# Task: POST /api/vehicle-assessment — veose ja tõste sobivuse eelhinnang

## Kirjeldus

Luua väliskliendile mõeldud vestluslik päring, mille kaudu klient kirjeldab kauba kaalu
ja mõõtmeid ning soovitud tõstekõrgust. Süsteem kasutab küsimusest vajalike andmete
leidmiseks AI-d ja võrdleb veoseandmeid andmebaasis olevate sõidukite andmetega. Vastus
peab arusaadavalt ütlema, kas andmete põhjal leidub sobiv sõiduk, kas kehtib eriloa või
kas mõõtude, kraana või muu asjaolu tõttu vajab juhtum käsitsi kontrolli.

Selles taskis käsitletakse andmebaasi sõiduki- ja kraanaandmeid pärisandmetena. Kraana
tõstevõime andmed on mõõdetud kindlatel tõstekaugustel; automaatses eelhinnangus võib
kasutada ainult andmebaasis täpselt olemasolevale tõstekaugusele vastavat mõõtepunkti.
Vahepealsetele või andmebaasis puudu olevatele tõstekaugustele ei tohi võimekust
interpoleerida ega ekstrapoleerida — need tuleb käsitsi kinnitada. Eelhinnang ei ole
siiski lõplik töö teostatavuse kinnitus.

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
  "craneAssessment": "MANUAL_CONFIRMATION",
  "missingInformation": []
}
```

Võimalikud `status` väärtused:

| Väärtus | Tähendus |
|---|---|
| `POSSIBLE` | Sõiduki andmed vastavad automaatselt hinnatavatele transporditingimustele; kraana tulemust kirjeldab eraldi `craneAssessment`. |
| `NOT_POSSIBLE` | Ükski aktiivne sõiduk ei vasta andmebaasis olevate transpordipiirangute alusel veose kaalule või platvormi mõõtudele. |
| `NEED_MORE_INFORMATION` | Küsimusest puudub üks või mitu vajalikku sisendandmetest ning kliendilt tuleb neid küsida. |
| `NEEDS_MANUAL_REVIEW` | Juhtum ületab määratud automaatse hindamise piire või nõuab kraana/tõstekõrguse käsitsi kinnitamist. |

`craneAssessment` kirjeldab tõsteosa transpordihinnangust eraldi. Selle väärtused on
`NOT_REQUIRED` (tõstmist pole vaja), `WITHIN_MEASURED_CAPACITY` (täpsel mõõdetud
tõstekaugusel jääb veose kaal mõõdetud võimekuse sisse), `EXCEEDS_MEASURED_CAPACITY`
(täpsel mõõdetud tõstekaugusel ületab kaal mõõdetud võimekuse) ja
`MANUAL_CONFIRMATION` (täpset tõstekauguse mõõtepunkti pole või tõsteosa vajab muud
käsitsi kontrolli). Mõõtepunkti tulemus on eelhinnang, mitte töö lõplik kinnitus.

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
3. Veose mõõdud teisendatakse enne võrdlemist sõiduki mõõtudega samasse ühikusse.
   Sõiduki platvormi põhipikkus, vajadusel kasutatav pikendus ja laius on andmebaasis
   millimeetrites (`platform_length_mm`, `platform_extension_mm`,
   `platform_width_mm`); sõiduki üldmõõdud on samuti millimeetrites. Hindamine peab
   kasutama andmebaasist saadud väärtusi ega tohi lasta AI-l sõidukiandmeid välja
   mõelda.
4. Kui veose pikkus või laius ületab platvormi vastavat mõõtu või mõõtude sobivust ei
   saa kindlalt hinnata, suunatakse juhtum käsitsi kontrolli
   (`NEEDS_MANUAL_REVIEW`). Süsteem ei arvuta ega kirjelda üleulatuse suurust ega
   järelda sellest eriloa või saateauto vajadust; need asjaolud tuleb käsitsi üle
   kontrollida.
5. Kraana tabeli `crane_capacity` tõstekaugus `reach_m` on meetrites ja
   `max_weight_kg` kilogrammides. Kui kliendi soovitud tõstekaugus vastab täpselt
   andmebaasis olevale mõõtepunktile, võrdle veose kaalu selle punkti tõstevõimega ning
   väljasta vastav kraanahinnang. Kui täpset mõõtepunkti pole, märgi
   `craneAssessment = "MANUAL_CONFIRMATION"` ja ütle, et tõstevõime tuleb käsitsi üle
   kontrollida. Ära interpoleeri ega ekstrapoleeri mõõtepunktide vahel ega väljapoole
   mõõdetud vahemikku. Ka mõõtepunktiga sobiv tulemus on eelhinnang, mitte lõplik
   teostatavuse kinnitus.
6. Sõiduki transpordisobivus ja kraanaga tõstmise sobivus on vastuses eristatavad.
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
   mõõtude eraldamist ning tõstmist vajava juhtumi käsitsi kinnitamise vajadust.
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
- [ ] Kaalupiirangut ületavat sõidukit ei märgita sobivaks.
- [ ] Kui veose pikkus või laius ületab platvormi vastavat mõõtu või mõõtude sobivust
  ei saa kindlalt hinnata, suunatakse juhtum käsitsi kontrolli
  (`NEEDS_MANUAL_REVIEW`).
- [ ] Süsteem ei arvuta ega kirjelda üleulatuse suurust ega järelda sellest eriloa või
  saateauto vajadust; need asjaolud tuleb käsitsi üle kontrollida.
- [ ] Täpselt mõõdetud kraana tõstekaugusel kasutatakse vastava andmebaasirea
  tõstevõimet veose kaaluga võrdlemiseks.
- [ ] Vahepealse, puuduva või mõõdetud vahemikust väljapoole jääva tõstekauguse korral
  märgitakse kraana hinnang käsitsi kinnitatavaks; väärtusi ei interpoleerita ega
  ekstrapoleerita.
- [ ] Kraanahinnang jääb eelhinnanguks ega anna lõplikku töö teostatavuse lubadust.
- [ ] Vastus sisaldab fikseeritud struktuuriga staatust, kliendile mõeldud selgitust,
  sobivaid sõidukeid, kraana hinnangu staatust ja vajadusel puuduvaid andmeid.
- [ ] AI ei tagasta SQL-i, süsteemiprompti ega andmebaasist küsimusega mitteseotud
  andmeid.
- [ ] Prompt asub Markdown-failis, sisaldab süsteemi- ja kasutajarolli juhiseid ning
  näiteid.
- [ ] Prompti injection'i katse ei muuda ärireegleid ega anna välja keelatud andmeid.
- [ ] AI või andmebaasi tõrke korral tagastub veavastus; süsteem ei anna väljamõeldud
  edukat hinnangut.
- [ ] Automaat- ja/või integratsioonitestid katavad puuduva sisendi, kaalu- ja
  platvormipiirid, aktiivse sõiduki valiku, platvormi mõõtude ületamisel käsitsi
  kontrolli, täpsete kraana mõõtepunktide kaaluvõrdluse nii sobiva kui ületatud kaalu
  korral, vahepealse ja mõõdetud vahemikust väljas oleva tõstekauguse käsitsi
  kinnitamise ning AI vigase vastuse.

## Avatud küsimused

- Kas uus kliendipäring jääb eraldi endpointiks `POST /api/vehicle-assessment` või
  soovitakse muuta olemasolevat `POST /api/ask`? Eraldi endpoint on soovituslik, sest
  praegune `/api/ask` genereerib SQL-i ning selle kasutajavoog on ADMIN-i andmepäring.
- Kas endpoint peab olema avalik või nõuab autentimist/rate limit'it? Praegune
  `/api/ask` ei kontrolli backendis ADMIN rolli; uue väliskliendi endpointi ligipääs
  tuleb turvaliselt määratleda enne avalikku kasutust.
- Kui veose pikkuse ja laiuse suunda võib platvormile sobitamisel pöörata, tuleb see
  käsitsi kontrollida; süsteem ei arvuta ega kirjelda üleulatuse suurust.
- Kas klient sisestab küsimuse vabatekstina või peaks tulevikus kasutajaliides mõõdud
  ja kaalu eraldi väljadena küsima? Käesolev endpoint kasutab vabatekstilist küsimust.
