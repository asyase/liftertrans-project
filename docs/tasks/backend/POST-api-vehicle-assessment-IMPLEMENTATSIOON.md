# POST /api/vehicle-assessment — implementatsiooni plaan

**Seotud task:** `docs/tasks/backend/POST-api-vehicle-assessment.md`

## Hetkeseis (mis on juba olemas)

- `backend/src/main/java/ee/liftertrans/controller/AiController.java` sisaldab
  `POST /api/ask` endpointi. See delegeerib päringu `NlToSqlService`-ile ega ole
  kliendile mõeldud veosehindamise endpoint.
- `backend/src/main/java/ee/liftertrans/service/NlToSqlService.java` kasutab
  `ChatClient`-i SQL-i loomiseks ja tulemuse kokkuvõtmiseks. Uus funktsioon ei tohiks
  seda SQL-i genereerimise voogu kasutada.
- `backend/src/main/java/ee/liftertrans/dto/AiAskRequestDto.java` valideerib küsimuse
  `@NotBlank` ja `@Size(max = 500)` abil. Seda DTO-d ei pea uue ressursi jaoks jagama,
  sest selle olemasolev kasutus on seotud `/api/ask` lepinguga.
- `backend/src/main/java/ee/liftertrans/persistence/entity/Vehicle.java` sisaldab juba
  sõiduki kaalu-, platvormi- ja staatusevälju (`maxCargoWeightKg`,
  `platformLengthM`, `platformWidthM`, `status`). Uut sõiduki entiteeti ega
  andmebaasitabelit nende reeglite jaoks vaja ei ole.
- `backend/src/main/java/ee/liftertrans/persistence/repository/VehicleRepository.java`
  pärib `JpaRepository<Vehicle, Integer>`-ist, kuid aktiivsete sõidukite leidmiseks
  eraldi meetodit veel ei ole.
- `backend/src/main/java/ee/liftertrans/service/VehicleService.java` tagastab praegu
  kõik sõidukid `VehicleDto` kujul. See DTO ei sisalda kaalu- ega platvormiandmeid,
  mida sobivuse hindamine vajab.
- `backend/src/main/java/ee/liftertrans/mapper/VehicleMapper.java` teisendab sõiduki
  olemasolevaks `VehicleDto`-ks. Uue kliendivastuse jaoks tuleb kasutada eraldi,
  minimaalse väljundiga DTO-d; olemasolevat sõidukite nimekirja lepingut ei tohiks
  muuta.
- `backend/src/main/java/ee/liftertrans/controller/VehicleController.java` ja
  `VehicleService.java` näitavad praegust controller/service mustrit.
- `backend/src/main/java/ee/liftertrans/infrastructure/RestExceptionHandler.java`
  käsitleb `@Valid`-i valideerimisvigu 400 `INCORRECT_INPUT` vastusena. AI või
  andmebaasi ootamatu vea korral ei tohi teenus koostada edukat sobivushinnangut.
- `backend/src/main/resources/application.properties` seadistab Spring AI Gemini
  mudeli ja väljundi JSON-vormingu. `build.gradle` sisaldab Spring AI Gemini
  sõltuvust.
- Kraanainfo on olemas `Vehicle` väljadena ning andmebaasis on ka eraldi
  `crane_capacity` tabel. Kraanaandmed on taski järgi katsetuslikud, seega neid ei
  kasutata tõstevõime otsustamiseks.
- Backendi testides on olemas controller'i valideerimise näide
  `backend/src/test/java/ee/liftertrans/controller/DriverControllerTest.java`.
  Veosehindamise endpointi, teenuse ega ärireeglite teste veel ei ole.

## Puuduv/muudetav

- Uue endpointi request- ja response-DTO-d.
- Küsimusest mõõtude/kaalu eraldav AI kiht, eraldi promptiressurss ning vastuse
struktuuri kontroll.
- Aktiivsete sõidukite repository päring.
- Eraldi veosehindamise teenus, mis arvutab sobivuse deterministlikult ning koostab
  promptist ja andmebaasist saadud info põhjal tulemuse.
- Uus `POST /api/vehicle-assessment` kontrollerimeetod.
- Teenuse ärireeglite, sisendite parsimise, endpointi valideerimise ja vigaste AI
  vastuste testid.
- Enne lõplikku DTO/API lahendust tuleb otsustada, kuidas esitada eri sõidukite puhul
  erinev üleulatus ning loa/saateauto vajadus. Praeguses näidisvastuses on need
  väljad tipptasemel, kuigi üleulatus sõltub valitud sõidukist.

## Sammud

1. **Kasuta olemasolevat sõiduki entiteeti; andmebaasi muudatust pole vaja** —
   fail: `backend/src/main/java/ee/liftertrans/persistence/entity/Vehicle.java`
   - Olemasolevad väljad katavad kandevõime, platvormi mõõdud ja staatuse.
   - Kraana võimekuse jaoks uut loogikat ega andmebaasi muudatusi selle taski käigus
     ei lisata.

2. **Lisa aktiivsete sõidukite päring** —
   fail: `backend/src/main/java/ee/liftertrans/persistence/repository/VehicleRepository.java`
   - Lisa Spring Data päring aktiivsete sõidukite leidmiseks, näiteks
     `List<Vehicle> findVehiclesByStatus(String status)`.
   - Meetodi nimi kirjeldagu tagastatavat subjekti vastavalt
     `backend/CLAUDE.md` konventsioonile.
   - Teenus küsib staatusega `ACTIVE`; kontrolli, et päring ei tagasta
     `IN_SERVICE`, `UNAVAILABLE` ega `INACTIVE` sõidukeid.

3. **Loo päringu- ja vastuse DTO-d** —
   failid:
   `backend/src/main/java/ee/liftertrans/dto/VehicleAssessmentRequestDto.java`,
   `backend/src/main/java/ee/liftertrans/dto/VehicleAssessmentResponseDto.java`,
   `backend/src/main/java/ee/liftertrans/dto/VehicleAssessmentVehicleDto.java`
   - Request DTO sisaldab `question` välja ning `@NotBlank` ja `@Size(max = 500)`
     valideerimist.
   - Response DTO modelleerib taskis määratud `status`, `answer`, sõidukid,
     üleulatuse/loa/saateauto hinnangu, kraanahinnangu ning puuduva info.
   - Sõiduki DTO väljastab ainult kliendile vajalikke välju, näiteks
     registreerimisnumbri ja mudeli nime. Kandevõime ja muud siseandmed jäävad
     hindamisloogika kasutusse, kui neid pole kliendile teadlikult vaja näidata.
   - Kuna DTO-d kasutab ainult uus endpoint, sobib olemasoleva koodibaasi järgi
     `ee.liftertrans.dto`. Ära muuda `AiAskRequestDto` ega `AiAskResponseDto` lepingut.

4. **Loo küsimuse väljade eraldamise DTO ja prompt** —
   failid:
   `backend/src/main/java/ee/liftertrans/dto/VehicleAssessmentInputDto.java`,
   `backend/src/main/resources/prompts/vehicle-assessment-system-prompt.md`
   - Sisemine sisendi DTO sisaldab vähemalt kaalu, pikkust, laiust ning seda, kas
     klient soovib kraanaga tõstmist ja millist kõrgust kirjeldas. Tundmatu või
     leidmata väli peab jääma puuduvaks; mudel ei tohi väärtust oletada.
   - Prompt hoiab süsteemi juhised kasutaja küsimusest lahus, kirjeldab fikseeritud
     JSON-struktuuri ning käsitleb kasutaja teksti usaldamatu sisendina.
   - Lisa taskis nõutud näited: mõõtude eraldamine, puuduva info küsimine ning
     tõstevajaduse korral käsitsi kinnitamise märkimine.
   - Hoia prompti sisu Markdown-failis, mitte Java tekstikonstandina. Teenus loeb
     selle classpath'i ressursina.
   - Ära palu mudelilt Chain of Thought arutluskäiku. Küsi ainult parsitud välju ja
     vajadusel lühikest kliendile sobivat selgitust.

5. **Lisa sõiduki väljundi mapper või selge projektsioon** —
   fail: `backend/src/main/java/ee/liftertrans/mapper/VehicleMapper.java`
   - Lisa olemasolevat `VehicleDto`-d muutmata meetod, mis teisendab sobivaks
     tunnistatud `Vehicle`-i minimaalseks `VehicleAssessmentVehicleDto` vastuseks.
   - Ära väljasta JPA entiteeti kontrollerist ega tagasta kliendile kõiki sõiduki
     välju.

6. **Loo eraldi veosehindamise teenus** —
   fail: `backend/src/main/java/ee/liftertrans/service/VehicleAssessmentService.java`
   - Võta sõltuvustena aktiivsete sõidukite repository, sõidukite mapper ning
     küsimuse väljade eraldamise komponent. Ära kutsu `NlToSqlService`-it.
   - Töövoog: kontrolli küsimust → eralda mõõdud ja kaal → puuduva info korral
     tagasta `NEED_MORE_INFORMATION` → leia aktiivsed sõidukid → rakenda kaalu,
     platvormi ja üleulatuse reegleid deterministlikult → koosta vastus.
   - Üleulatus on sõidukipõhine: `max(0, cargoLength - platformLength)`. Reeglid:
     0 m = selle reegli alusel luba/saateautot pole; üle 0 kuni 2 m = eriluba;
     üle 2 kuni 5 m = üks saateauto; üle 5 m = käsitsi kontroll.
   - Kaalu ületavad sõidukid ei sobi. Laiuse ületamist ei teisendata pikkuse
     üleulatuse reegliks; suuna määratlemata mõõtude paigutus käsitsi kontrolli.
   - Kraana tabeli või sõiduki kraanaväljade põhjal ei kinnitata tõstevõimet.
     Tõstevajaduse korral märgi kraana hinnang käsitsi kinnitatavaks.
   - Kui mudeli vastust ei saa sisemise DTO järgi parsida või vajalikud väljad on
     vigased, lõpeta päring veaga; ära tagasta edukat asendusotsust ega väljamõeldud
     väärtusi.

7. **Loo väljade eraldamise komponent** —
   fail: `backend/src/main/java/ee/liftertrans/service/VehicleAssessmentInputService.java`
   - Kasuta olemasolevat Spring AI `ChatClient` mustrit süsteemi- ja kasutajarolli
     promptidega ning struktureeritud DTO vastusega.
   - Sea mudeli valikud teadlikult: vastuse pikkuse piirang ja madal varieeruvus.
     Thinking/reasoning seade lisa ainult siis, kui kasutatav Spring AI Gemini
     integratsioon seda toetab.
   - Hoia API võti konfiguratsioonis/keskkonnas; ära lisa seda prompti ega lähtekoodi.
   - Ära renderda ega logi tarbetult kliendi vabateksti või mudeli täielikku
     sisemist vastust.

8. **Lisa uus controller'i endpoint** —
   fail: `backend/src/main/java/ee/liftertrans/controller/VehicleAssessmentController.java`
   - Järgi olemasoleva controller'i mustrit: `@RestController`,
     `@RequestMapping("/api")`, `@RequiredArgsConstructor`.
   - Lisa `@PostMapping("/vehicle-assessment")`, `@RequestBody @Valid` request DTO
     ning delegeeri töö teenusele.
   - Kirjelda endpointi Swagger annotatsioonidega. Vastusesse ei lisa SQL-i ega
     prompti.
   - Hoia see kontroller `AiController`-ist eraldi, kuna `/api/ask` on eraldiseisev
     NL-to-SQL funktsioon.

9. **Määra endpointi ligipääsupoliitika enne väliskasutust** —
   fail: `backend/src/main/java/ee/liftertrans/controller/VehicleAssessmentController.java`
   - Task ei otsusta, kas endpoint on avalik või autentimist nõudev. Enne päris
     kliendile avamist tuleb otsus teha ning vastav ligipääsukontroll rakendada.
   - Kui endpoint tehakse avalikuks, hinda eraldi päringute piiramise vajadust
     (rate limit) ja AI teenuse kulu kuritarvitamise riski. Ära eelda, et
     frontendi piirang kaitseb API-t.

10. **Lisa teenuse ühiktestid** —
    fail: `backend/src/test/java/ee/liftertrans/service/VehicleAssessmentServiceTest.java`
    - Testi ärireegleid eraldi AI-st ja päris andmebaasist, kasutades mockitud
      väljade eraldajat ja repository tulemust.
    - Katta aktiivse sõiduki valik, kaalu sobivus, mõõtude kontroll ning üleulatuse
      piirid: 0 m, üle 0 m, täpselt 2 m, üle 2 m, täpselt 5 m ja üle 5 m.
    - Katta laiuse ületamise käsitsi kontroll, puuduva sisendi küsimine ning
      kraanatõste käsitsi kinnitamine.
    - Kontrolli, et mudeli vigane või puudulik väljund ei tekita edukat hinnangut.

11. **Lisa controller'i testid** —
    fail: `backend/src/test/java/ee/liftertrans/controller/VehicleAssessmentControllerTest.java`
    - Järgi `DriverControllerTest.java` MockMvc ja `RestExceptionHandler` kasutamise
      mustrit.
    - Kontrolli korrektse päringu JSON-vastust, tühja küsimuse 400 vastust ning üle
      500 tähemärgi küsimuse tagasilükkamist.
    - Kontrolli, et controller ei väljasta vastuses SQL-i ega sisemist prompti.

## Veakäsitlus

- Tühi või üle 500 tähemärgi küsimus valideeritakse controller'i `@Valid` kaudu.
  `RestExceptionHandler` tagastab 400 `INCORRECT_INPUT` vastuse.
- Puuduva kaalu või mõõtme korral ei visata erindit: tagastatakse edukas
  struktureeritud vastus staatusega `NEED_MORE_INFORMATION`.
- AI vastuse struktuurivea või teenuse/andmebaasi ootamatu vea korral ei tagastata
  edukat sobivushinnangut. Lase ootamatul veal minna projekti tavapärasesse
  serverivea käsitlusse; ära püüa laia `Exception`-it kinni ega asenda tulemust
  vaikimisi vastusega.
- Task ei nõua uue ärilise veakoodi loomist. Kui hiljem otsustatakse AI teenuse vea
  jaoks kliendile eraldi veavastus lisada, tuleb see kooskõlastada olemasoleva
  `RestExceptionHandler` ja API veavorminguga.

## Testid

- Teenuse ühiktestid katavad kõik ärireeglite piirväärtused ja puuduva sisendi juhud.
- Repository test või integratsioonitest kontrollib, et ainult `ACTIVE` staatusega
  sõidukid jõuavad hindamisse.
- AI väljade eraldamise test kontrollib struktureeritud vastuse kasutamist ja vigase
  mudeliväljundi käsitlemist ilma päris mudelikutseta.
- Controller'i MockMvc testid kontrollivad endpointi rada, valideerimist,
  vastuse kuju ning tavapärast 400 veavormingut.
- Kui AI integratsioonitest lisatakse, peab see olema eraldi märgistatud ning vajama
  selgesõnalist API võtit; tavapärased testid ei tohi sõltuda välisest mudeliteenusest.

## Avatud küsimused

- Eraldi endpoint `POST /api/vehicle-assessment` on kasutajaga kokku lepitud;
  olemasolevat `/api/ask` endpointi selleks ei muudeta.
- Endpointi autentimine või avalik kasutus ning avaliku endpointi päringupiirangud
  vajavad endiselt otsust enne päris klientidele avamist.
- Taski näidisvastuses on `overhangM`, eriloa vajadus ja saateautode arv
  tipptasemel väljad. Need väärtused sõltuvad valitud sõidukist ning sobivaid
  sõidukeid võib olla mitu. Soovitatav on viia need väljad iga sõiduki hinnangu
  juurde või tagastada üks sõiduk korraga; API kuju tuleb enne DTO loomist kinnitada.
- Task ei määratle, kas veose pikkuse ja laiuse suunda tohib platvormile sobitamisel
  pöörata. Samuti pole määratletud, kuidas jaotub üleulatus platvormi esi- ja
  tagaotsa vahel. Need asjaolud mõjutavad sõidukipõhist hinnangut.
- Kui andmebaasis aktiivse sõiduki kaalu- või platvormiväärtus on `null`, tuleb
  kinnitada, kas seda sõidukit eirata või käsitsi kontrolli suunata. Puuduvat
  sõidukiandmestikku ei tohi tõlgendada piiranguteta võimekusena.
- `docs/backend/projekti-struktuur.md` kirjeldab näidisena paketti
  `ee.minuprojekt` ja ressursipõhiseid controller'i alampakette. Tegelik backend
  kasutab paketti `ee.liftertrans`, controller'id on otse `controller` paketis,
  DTO-d otse `dto` paketis ning mapperid eraldi `mapper` paketis. Uued failid tuleb
  paigutada tegeliku koodibaasi mustri järgi; struktuuridokument on selles osas
  koodibaasiga vastuolus.
