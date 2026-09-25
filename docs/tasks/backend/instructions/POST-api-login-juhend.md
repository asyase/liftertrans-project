# Juhend: POST /api/login

**Taski fail:** `POST-api-login.md`
**Kontroller:** `AuthController.java`
**Implementeerimise voog:** RestController → Service → Repository → Service → Mapper → RestController

---

## Sissejuhatus

Selle endpointi eesmärk on autentida kasutaja (ADMIN või DRIVER) e-posti ja parooli alusel ning tagastada tema roll ja vajadusel seotud juhi ID. 
Harjutuse käigus õpime tundma Spring Booti kontrolleri, teenusekihi, repositooriumi ja MapStruct mapperi omavahelist koostööd, paroolide valideerimist ning veaolukordade käsitlemist.

---

## Samm 1 — RestController

### Mida teha?

Alustame autentimise kontrolleri loomisest või täiendamisest. Kontroller võtab vastu HTTP POST päringu koos sisselogimise andmetega (`AuthRequestDto`) ning delegeerib autentimise teenusekihile.

Kontrolli esmalt, kas vastav kontrolleri klass juba eksisteerib:
- Kaust: `backend/src/main/java/ee/liftertrans/controller/` (nt `ee.liftertrans.controller.auth`)
- Kui **puudub** → loo uus klass IntelliJ'ga (File → New → Java Class)
- Kui **on olemas** → ava see klass ja lisa sinna uus meetod

Vajalikud klassiannotatsioonid:

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KontrolleriKlass {
    // ...
}
```

### Meetodi loomine

Alusta meetodist **ilma mappingannotatsioonideta** — see aitab loogika paika saada:

```java
public void meetodiNimi(SisendDtoTüüp parameetriNimi) {
    // tühi meetod esialgu
}
```

> **Mõtle:** Mis on selle meetodi hea nimi? Nimi peaks kirjeldama tegevust (nt sisselogimine).

Seejärel lisa:
1. **Mappingannotatsioon** — `@PostMapping("/rada")`
2. **Parameetrite annotatsioonid** — `@RequestBody` ja vajadusel `@Valid`
3. **Swagger annotatsioonid** — `@Operation` ja `@ApiResponses` (200 OK, 400 Bad Request, 401 Unauthorized)

### Service klassi ettevalmistus

Kontrolli, kas teenuseklass eksisteerib:
- Kaust: `backend/src/main/java/ee/liftertrans/service/`
- Kui **puudub** → loo uus klass:

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {
    // ...
}
```

Lisa service muutuja kontrolleri klassi:

```java
private final TeenusKlass teenuseMuutuja;
```

Kutsu service meetodit välja:

```java
public void meetodiNimi(SisendDtoTüüp parameetriNimi) {
    teenuseMuutuja.meetodiNimi(parameetriNimi);
}
```

> **IntelliJ vihje:** Kui `teenuseMuutuja.meetodiNimi(...)` on punasega alla joonitud, vajuta **Alt+Enter** punasel joonel → vali **"Create method in TeenusKlass"**.

---

## Samm 2 — Service ja kasutaja leidmine

### Mida teha?

Liigume teenuseklassi. Teenuse meetod saab sisendiks DTO andmed, kontrollib kohustuslike väljade olemasolu ja teeb päringu andmebaasist kasutaja leidmiseks.

### Repository ühenduse loomine

Mõtle: **millisest tabelist** on vaja andmeid otsida? Kasutaja andmed asuvad tabelis `user`.

Kirjuta repositooriumi muutuja nime algus service meetodis:

```java
public void meetodiNimi(SisendDtoTüüp parameetriNimi) {
    entiteetRep  // vajuta Tab muutuja loomiseks
}
```

Tulemus:

```java
@Service
@RequiredArgsConstructor
public class TeenusKlass {

    private final EntiteetRepository entiteetRepository;

    public void meetodiNimi(SisendDtoTüüp parameetriNimi) {
        // ...
    }
}
```

> **Kui repositooriumi interface või meetod puudub:** Kasuta **Alt+Enter** või loo `JpaRepository` interface. Kasutaja otsimiseks e-posti järgi on vaja meetodit, mis tagastab `Optional<User>`.

Kui repository meetod tagastab tulemuse: **pane tulemus kohe muutujasse**!

---

## Samm 3 — DTO klassid

### Mida teha?

Kontrolli ja loo vajalikud DTO klassid:
- `AuthRequestDto` (sisend: email, password)
- `AuthResponseDto` (väljund: userId, email, roleName, driverId)

Kaust: `backend/src/main/java/ee/liftertrans/controller/.../dto/`

DTO klassides kasuta `@Data` annotatsiooni.

---

## Samm 4 — Parooli kontroll ja Mapper

### Mida teha?

Pärast seda, kui kasutaja on andmebaasist leitud, tuleb kontrollida:
1. Kas kasutaja on olemas (kui ei, viska 401 viga)
2. Kas parool klapib räsi väärtusega (kasutades `PasswordEncoder` / `BCryptPasswordEncoder`)
3. Kas roll on lubatud (ADMIN või DRIVER)

Seejärel teisendatakse leitud entity response DTO-ks MapStruct mapperi abil.

### Mapper meetodi loomine

Ava mapper interface:

```java
@Mapper(componentModel = "spring")
public interface EntiteetMapper {

    @Mapping(source = "seos.id", target = "seoseId")
    @Mapping(source = "väärtus", target = "väärtus")
    TagastatavDtoTüüp toDto(EntiteetTüüp entiteet);
}
```

> **IntelliJ vihje:** Kliki `target = ""` jutumärkide vahele ja vajuta **Ctrl+Space**, et näha kõiki DTO välju ja luua mall.

Kutsu mapper välja service meetodis:

```java
public TagastatavDtoTüüp meetodiNimi(SisendDtoTüüp sisend) {
    // 1. Leia kasutaja
    // 2. Kontrolli parool
    // 3. Mapi DTO-ks
    TagastatavDtoTüüp dto = mapperMuutuja.toDto(entiteet);
    return dto;
}
```

---

## Samm 5 — Tagasi RestController'isse

### Mida teha?

Täienda kontrolleri meetodit nii, et see tagastaks teenuse meetodist saadud `AuthResponseDto`:

```java
@PostMapping("/login")
public TagastatavDtoTüüp login(@RequestBody SisendDtoTüüp request) {
    return teenuseMuutuja.meetodiNimi(request);
}
```

---

## Samm 6 — Kood ilusaks (refactor)

### Make it work → Make it beautiful

Kui kood töötab, vaata üle:
- Eralda pikemad valideerimis- ja kontroll-loogikad eraldi privaatsetesse abimeetoditesse (IntelliJ: **Refactor → Extract Method**).
- Meetodite järjekord: `public` meetodid üleval, `private` helper meetodid allpool.

---

## Kokkuvõte ja kontrollnimekiri

- [ ] `AuthController` on olemas ja varustatud annotatsioonidega
- [ ] Endpointid `POST /api/login`, `GET /api/auth/me`, `POST /api/logout` on defineeritud
- [ ] Parooli kontroll toimub turvaliselt (BCrypt)
- [ ] Veaolukorrad tagastavad korrektsed veakoodid ja teated
- [ ] DTO-d ja MapStruct mapper on korrektselt kaardistatud
- [ ] Swagger UI kaudu on endpointid testitavad (`/swagger-ui/index.html`)
