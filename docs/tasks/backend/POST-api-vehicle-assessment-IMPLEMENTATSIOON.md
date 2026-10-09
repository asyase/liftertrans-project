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
- `backend/src/main/java/ee/liftertrans/persistence/entity/Vehicle.java` sisaldab
  sõiduki kandevõime, platvormi mõõtude ja staatuse välju. Platvormi põhipikkus,
  vajadusel kasutatav pikendus, platvormi laius ning sõiduki üldmõõdud talletatakse
  millimeetrites; kraana tõstekaugus jääb meetrites ja kaalud kilogrammides.
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
  `crane_capacity` tabel. Kraana tõstekauguse mõõtepunktid on pärisandmed ning neid
  võib kasutada ainult täpselt salvestatud kaugusel; vahepealseid väärtusi ei
  interpoleerita ega ekstrapoleerita. Java poolel pole `crane_capacity` tabeli
  entiteeti ega repositooriumi veel loodud.
- Backendi testides on olemas controller'i valideerimise näide
  `backend/src/test/java/ee/liftertrans/controller/DriverControllerTest.java`.
  Veosehindamise endpointi, teenuse ega ärireeglite teste veel ei ole.

## Puuduv/muudetav

- Uue endpointi request- ja response-DTO-d.
- Küsimusest mõõtude/kaalu eraldav AI kiht, eraldi promptiressurss ning vastuse
struktuuri kontroll.
- Aktiivsete sõidukite repository päring.
- Kraana mõõtepunktide lugemiseks `crane_capacity` entiteet ja repositoorium.
- Eraldi veosehindamise teenus, mis arvutab sobivuse deterministlikult ning koostab
  promptist ja andmebaasist saadud info põhjal tulemuse.
- Uus `POST /api/vehicle-assessment` kontrollerimeetod.
- Teenuse ärireeglite, sisendite parsimise, endpointi valideerimise ja vigaste AI
  vastuste testid.
- Platvormi mõõtmeid ületav veos suunatakse käsitsi kontrolli. API ei esita
  üleulatuse suurust eraldi ega otsusta selle põhjal loa või saateauto vajadust.

## Sammud

1. **Uuenda sõiduki mõõtude andmemudelit** —
   failid: `docs/database/2_create.sql`,
   `docs/database/3_import.sql`,
   `backend/src/main/java/ee/liftertrans/persistence/entity/Vehicle.java`
   - Sõiduki üldmõõdud ning platvormi põhipikkus ja laius salvestatakse millimeetrites.
   - Lisa platvormi pikenduse pikkusele eraldi millimeetriväli; pikendus on valikuline
     ega tähenda, et see oleks alati kasutuses.
   - Kraana tõstekaugus jääb meetritesse ning kaalud kilogrammides.
   - Uuenda algandmetes sõidukit 816FTF (Scania R400): kandevõime 8670 kg,
     auto mõõdud 11400 × 2550 × 3900 mm, platvorm 7500 × 2550 mm ja pikendus 1500 mm.
     Kraana mõõtepunktid talleta `crane_capacity` tabelis: 4 m / 13000 kg ja
     18 m / 2700 kg. Need on pärisandmed ning neid kasutatakse täpselt vastava
     tõstekauguse korral.
   - Veose mõõtude tabeli ühikuid see muudatus ei muuda; enne võrdlust tuleb ühikud
     ühtlustada.

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
     kraanahinnangu ning puuduva info.
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
   - Enne teenuse kraanahinnangu osa loomist loo `crane_capacity` tabelile JPA
     entiteet ja repositoorium, kui neid pole. Repositoorium peab võimaldama lugeda
     valitud sõiduki mõõtepunktid.
   - Võta sõltuvustena aktiivsete sõidukite repository, kraana mõõtepunktide
     repository, sõidukite mapper ning küsimuse väljade eraldamise komponent. Ära
     kutsu `NlToSqlService`-it.
   - Töövoog: kontrolli küsimust → eralda mõõdud ja kaal → puuduva info korral
     tagasta `NEED_MORE_INFORMATION` → leia aktiivsed sõidukid → võrdle kaalu ja
     mõõte sõiduki piirangutega → kui veose pikkus või laius ületab platvormi vastavat
     mõõtu või sobivus pole kindel, suuna juhtum `NEEDS_MANUAL_REVIEW` staatusega
     käsitsi kontrolli → koosta vastus.
   - Süsteem ei arvuta ega kirjelda üleulatuse suurust ega otsusta selle põhjal
     eriloa või saateauto vajadust; need asjaolud tuleb käsitsi üle kontrollida.
   - Kaalu ületavad sõidukid ei sobi. Puuduvate või ebaselgete mõõtude korral ära
     oletada sobivust.
   - Kraana tõstevõimet hinnatakse ainult siis, kui soovitud tõstekaugusele leidub
     täpne rida `crane_capacity` tabelis; võrdle selle rea kilogrammivõimet veose
     kaaluga.
   - Kui täpset tõstekauguse rida pole, märgi kraana hinnang käsitsi kinnitatavaks.
     Ära interpoleeri ega ekstrapoleeri tõstevõimet mõõtepunktide vahel või neist
     väljapoole.
   - Sõiduki üldised kraanaväljad ei asenda tõstekauguse kaupa salvestatud mõõtepunkti.
     Isegi sobiv mõõtepunkt annab ainult eelhinnangu, mitte lõpliku töö teostatavuse
     kinnituse.
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
    - Katta aktiivse sõiduki valik, kaalu sobivus, platvormi pikkuse või laiuse
      ületamisel käsitsi kontroll, puuduva sisendi küsimine, kraana täpse
      mõõtepunkti kaaluvõrdlus nii mõõdetud piiri sees kui sellest üle, mõõtepunktide
      vahele jääva ja mõõdetud vahemikust väljas oleva tõstekauguse korral käsitsi
      kinnitamine.
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
- Platvormi mõõtmeid ületav veos suunatakse käsitsi kontrolli; API ei kirjelda
  üleulatuse suurust eraldi ega otsusta selle põhjal loa või saateauto vajadust.
- Kui veose pikkuse ja laiuse suunda võib platvormile sobitamisel pöörata, tuleb
  ebaselge sobivus käsitsi kontrollida.
- Kui andmebaasis aktiivse sõiduki kaalu- või platvormiväärtus on `null`, tuleb
  kinnitada, kas seda sõidukit eirata või käsitsi kontrolli suunata. Puuduvat
  sõidukiandmestikku ei tohi tõlgendada piiranguteta võimekusena.
- `docs/backend/projekti-struktuur.md` kirjeldab näidisena paketti
  `ee.minuprojekt` ja ressursipõhiseid controller'i alampakette. Tegelik backend
  kasutab paketti `ee.liftertrans`, controller'id on otse `controller` paketis,
  DTO-d otse `dto` paketis ning mapperid eraldi `mapper` paketis. Uued failid tuleb
  paigutada tegeliku koodibaasi mustri järgi; struktuuridokument on selles osas
  koodibaasiga vastuolus.
