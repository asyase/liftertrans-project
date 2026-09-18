# CLAUDE.md

See fail annab juhiseid Claude Code'ile (claude.ai/code) selles repositooriumis töötamiseks.

**Keel:** Kõik uued kanded sellesse faili kirjutatakse eesti keeles.

## Käsud

```bash
# Ehitamine
./gradlew build

# Käivitamine (nõuab lokaalset PostgreSQL-i)
./gradlew bootRun

# Testide käivitamine
./gradlew test

# Ühe testiklassi käivitamine
./gradlew test --tests "ee.minuprojekt.MinuprojektApplicationTests"

# Ainult kompileerimine (käivitab ka MapStructi annotatsiooni töötluse)
./gradlew compileJava
```

## Andmebaasi seadistamine

PostgreSQL peab töötama `localhost`-is järgmiste vaikeseadetega (kõik ülekirjutatavad keskkonnamuutujatega `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` — vt `application.properties`):
- Andmebaas: `vali_it`
- Port: `5432`
- Kasutajanimi: `postgres`
- Parool: `student123`
- Mugav DB url: `jdbc:postgresql://localhost:5432/vali_it`

Kui lokaalne PostgreSQL kuulab teisel pordil, käivita backend nt:
```bash
DB_PORT=5433 ./gradlew bootRun
```

Käivita skriptid järjekorras kaustast `docs/database`, **ühe ja sama psql seansi sees** (nt `psql -f 1_reset_database.sql -f 2_create.sql -f 3_import.sql`), kuna `2_create.sql` ei sea `search_path` uuesti — eraldi seanssidena käivitades tekivad tabelid vale skeemasse (`public`):
1. `1_reset_database.sql` — kustutab ja loob uuesti `liftertrans_project` skeema
2. `2_create.sql` — loob kõik tabelid
3. `3_import.sql` — lisab algandmed

Kõik tabelid asuvad `liftertrans_project` skeemas.

## Arhitektuur

Tegemist on Spring Boot 4.x / Java 21 REST backendiga. Frontend on eraldi Vue 3 SPA (ei ole selles repos).

### Kihtide struktuur

```
controller/       REST endpointid + päringu/vastuse DTOd
service/          Äriloogika
persistence/      JPA entiteedid, repositooriumid, MapStructi mapperid
infrastructure/   Läbivad komponendid: erindi tüübid, veakoodid, globaalne erindite käsitleja
```

Igal domeenialal on oma alampakk `controller/`-is koos DTOdega, teenusklass ja persistence pakk.

### Olulised konventsioonid

**DTOd vs entiteedid** — Kontrollerid näevad ainult DTOsid. MapStructi mapperid (liidesed annotatsiooniga `@Mapper`) teisendavad DTOd JPA entiteetideks ja vastupidi. Genereeritud mapperi implementatsioonid tekivad kausta `src/main/generated/`.

**Veakäsitlus** — Teenustest visatakse kohandatud erindeid (`DataNotFoundException`, `ForbiddenException`, `PrimaryKeyNotFoundException`), mille püüab kinni `RestExceptionHandler` (`@ControllerAdvice`). Kõik äriveateated ja numbrilised veakoodid on koondatud `ErrorResponse` enumi.

**Muutujate nimetamine** — Muutuja nimi peab peegeldama täistüüpi: `EntityDetailDto entityDetailDto`, mitte `EntityDetailDto dto`.

**Meetodi nimetamine** — `getX()` lubab kindlat tagastust. Kui meetod sisaldab tingimislikku loogikat ja muteerib DTO-d, kasuta `handle`-prefiksit ja anna DTO parameeter sisse: `handleAddImageData(EntityDetailDto entityDetailDto, Integer entityId)`.

**Entiteedi otsing ID järgi** — `repository.findById()` kasutamine `orElseThrow`-ga peab olema `public getValid<Entiteet>By(Integer <entiteet>Id)` meetodis vastava service klassi all (nt `getValidEntityBy(Integer entityId)` `EntityService`-s).

**SQL päringud** — Kohandatud päringud on JPQL, kirjutatud otse Spring Data repositooriumi liidesele `@Query` annotatsiooniga. Vajadusel kasutab repositoorium konstruktori avaldist otse DTOsse projekteerimiseks.

**Repositooriumi meetodi nimetamine** — Meetodi nimi peab mainima, mida ta tagastab (subjekti), mitte jääma geneeriliseks: `findFilteredEntitiesBy(...)` tagastab `List<Entity>`, `findUserBy(...)` tagastab `User`. Väldi kujundeid nagu `findFilteredBy(...)`, kust pole näha, mida meetod tagastab.

**Jagatud DTO-d** — DTO klass, mida kasutab rohkem kui üks ressursipakett (nt mitme kontrolleri/mapperi vahel jagatud), ei kuulu ühegi üksiku ressursi `dto/` paketti, vaid paketti `controller/common/dto/` (nt `SharedEntityDto`).

**SQL logimine** — P6Spy on seadistatud (`spy.properties`), nii et täielik parameetritega SQL kuvatakse konsoolis. Selle saab keelata, lülitades `application.properties`-is tagasi kommenteeritud tavaliste PostgreSQL seadetele.

### REST API

Baastee: `/api`. Swagger UI on saadaval aadressil `/swagger-ui.html`.

Üksikute teenuste kirjeldused (URL, sisend, väljund, veaolukorrad, vastuvõtu kriteeriumid) ei ole selles failis — need dokumenteeritakse taski failidena kaustas `docs/tasks/backend/`.
