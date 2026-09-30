# AI vestlus — küsimused andmete kohta loomulikus keeles (NL-to-SQL)

**Vaade:** `AiChatView.vue`, route `/ai-chat` (`aiChatRoute`)

**Roll:** ADMIN

**Vaste balsamic mockupis:** puudub — vaade on mockupist väljaspool, kujundus järgib olemasolevate vaadete stiili (Bootstrap, punane põhivärv `btn-danger`).

**Backend:** on juba implementeeritud — `AiController.java`, `NlToSqlService.java`, `AiAskRequestDto.java`, `AiAskResponseDto.java` (commit "NL-to-SQL chat: POST /api/ask with Spring AI and Google Gemini"). Eraldi backend taski faili ei ole, see task järgib koodi.

**Backend task (SQL päring vastuses):** `docs/tasks/backend/POST-api-ask-sql-vastuses.md` — vajalik jaotise "SQL päringu kuvamine" jaoks.

## Kasutajavoog

ADMIN avab menüüst lingi "Küsi AI-lt" ja jõuab vaatele `/ai-chat`. Lehel on vestlusaken ja selle all tekstiväli koos nupuga "Küsi". ADMIN kirjutab küsimuse tavakeeles (nt "Mitu tööd on praegu planeeritud?") ja vajutab "Küsi" või Enter. Küsimus ilmub kohe vestlusesse paremale, AI vastus ilmub mõne sekundi pärast vasakule. Vestlus jätkub järgmiste küsimustega samal lehel.

Backend teeb küsimusest AI abil SQL `SELECT` päringu, käivitab selle ainult-loetavas ühenduses ja laseb AI-l tulemuse 1–3 lausega kokku võtta. Frontend kuvab kokkuvõtte ja selle all kokkupandava lingi "Näita SQL päringut", millega ADMIN saab vaadata, millise päringu põhjal vastus tehti.

## Kasutajaliidese elemendid

| Element | Tüüp | Kirjeldus/käitumine |
|---|---|---|
| "Küsi andmete kohta" | Pealkiri | Lehe pealkiri. |
| Veateade | Alert (`alert alert-danger`) | Kuvatakse ainult siis, kui `errorMessage` ei ole tühi (frontendi valideerimine või ootamatu viga). |
| Vestlusaken | Kast (`border rounded`) | Kuvab sõnumid järjekorras. Tühja vestluse korral näidatakse näidisküsimusi hallis tekstis. |
| Kasutaja sõnum | Mull, paremal (`bg-danger text-white`) | Kasutaja küsimus. |
| AI sõnum | Mull, vasakul (`bg-light`) | AI vastus või backendi 400 veateade. |
| "Näita SQL päringut" / "Peida SQL päring" | Link (`btn btn-link btn-sm`) AI sõnumi all | Kuvatakse ainult siis, kui sõnumil on `sql`. Avab/sulgeb SQL ploki. |
| SQL plokk | `<pre><code>` (`bg-dark text-light small`) | Vaikimisi peidetud. Näitab päringut muutmata kujul, reavahetused säilivad. |
| Laadimisindikaator | Spinner + "AI mõtleb..." | Kuvatakse vestlusakna lõpus ainult päringu ajal (`isLoading === true`). |
| Küsimuse väli | Tekstisisend, `maxlength="500"` | Kohustuslik. Päringu ajal `disabled`. |
| "Küsi" | Nupp (`type="submit"`) | Saadab küsimuse. Päringu ajal `disabled`. Enter vormis teeb sama (`@submit.prevent`). |

## Käitumine ja valideerimine

1. Lehe avamisel kontrollitakse rolli (`SessionStorageService.userIsAdmin()`). Mitte-ADMIN → `NavigationService.navigateToNotAuthorizedView()`.
2. Vestlus on tühi, kuvatakse näidisküsimused (nt "Mitu tööd on praegu planeeritud?", "Millised juhid on aktiivsed?").
3. "Küsi" vajutamisel tühjendatakse eelmine veateade ja kontrollitakse:
   - küsimus tühi (ka ainult tühikud) → "Küsimus on kohustuslik", päringut ei saadeta.
4. Kui küsimus on täidetud:
   - lisatakse küsimus kohe vestlusesse (`{ role: 'user', text }`);
   - küsimuse väli tühjendatakse;
   - `isLoading = true`, saadetakse `POST /api/ask` body'ga `{ question }` (`trim()`-itud).
5. Õnnestumise korral (200) lisatakse vestlusesse AI vastus (`{ role: 'ai', text: response.data.answer, sql: response.data.sql, showSql: false }`).
6. Vea korral:
   - 400 (`CANNOT_ANSWER`, `FORBIDDEN_SQL`, `INCORRECT_INPUT`) → backendi `message` lisatakse vestlusesse **AI sõnumina**, sest see on kasutajale mõeldud selgitus, mitte süsteemi rike (nt "Sellele küsimusele ei saa andmebaasi põhjal vastata");
   - muu viga (500, AI teenus ei vasta, backend maas) → `errorMessage = 'AI vastust ei õnnestunud saada. Proovi uuesti.'`, kasutaja jääb lehele. **Ei suunata `/error` vaatele**, et vestlus ei kaoks.
7. Päringu lõppedes seatakse `isLoading = false` (`.finally()`).
8. Vestluse ajalugu hoitakse ainult komponendi `data()`-s — lehe värskendamisel see kaob (esimeses versioonis on see lubatud).

## SQL päringu kuvamine

1. Iga AI sõnumi juures hoitakse `sql` (backendist) ja `showSql` (vaikimisi `false`).
2. Kui `sql` on olemas, kuvatakse sõnumi all link "Näita SQL päringut". Vajutus → `showSql = !showSql`, lingi tekst muutub "Peida SQL päring".
3. SQL kuvatakse `<pre><code>` plokis süntaksi esiletõstmiseta — **ainult tekstina** (`{{ message.sql }}`), mitte `v-html`-iga.
4. 400 vea sõnumitel (`CANNOT_ANSWER`, `FORBIDDEN_SQL`) `sql` puudub, seega linki ei kuvata.
5. Kuni backend task pole tehtud, on `response.data.sql` `undefined` — link lihtsalt ei ilmu, vaade töötab ka ilma.

## API kutsed

### `POST /api/ask`

**Backend allikas:** `AiController.java`, `NlToSqlService.java`

`AiAskRequestDto.java` — request body:
```json
{
  "question": "Mitu tööd on praegu planeeritud?"
}
```

`AiAskResponseDto.java` — response (200):
```json
{
  "answer": "Praegu on planeeritud 4 tööd.",
  "sql": "SELECT COUNT(*) FROM liftertrans_project.job WHERE status = 'PLANNED'"
}
```

**Veateated:**

| Status code | errorCode | message | Frontend käitumine |
|---|---|---|---|
| 400 | `CANNOT_ANSWER` | "Sellele küsimusele ei saa andmebaasi põhjal vastata" | `message` AI sõnumina vestlusesse. |
| 400 | `FORBIDDEN_SQL` | "Lubatud on ainult SELECT päringud" / "Päring sisaldab keelatud SQL märksõnu" / "Kasutajate andmeid ei saa küsida" | `message` AI sõnumina vestlusesse. |
| 400 | `INCORRECT_INPUT` | "question: küsimus ei tohi olla tühi" / "question: küsimus võib olla kuni 500 tähemärki" | `message` AI sõnumina vestlusesse. Frontendi valideerimine ja `maxlength` peaksid selle ära hoidma. |
| muu | — | — | `errorMessage` alert'is, kasutaja jääb lehele. |

> **Märkus:** vastus võib võtta mitu sekundit, sest backend kutsub AI mudelit (Google Gemini) kaks korda — kord SQL-i genereerimiseks ja kord tulemuse kokkuvõtteks. Seetõttu on laadimisindikaator ja nupu keelamine kohustuslikud.
>
> **Märkus (turvalisus):** backend ei kontrolli praegu, kas `/api/ask` kutsuja on sisse loginud ADMIN — rollikontroll on ainult frontendis. See ei ole selle taski osa, kuid tasub lisada eraldi backend taskina.

## Komponendid ja failistruktuur

- `frontend/src/services/AiService.js` — **uus fail**, meetod `postAskRequest(aiAskRequestDto)` → `axios.post('/api/ask', aiAskRequestDto)`.
- `frontend/src/views/AiChatView.vue` — **uus fail**. `data()`: `question`, `messages` (massiiv `{ role, text, sql, showSql }`), `isLoading`, `errorMessage`. Meetodid: `askQuestion`, `handleAskResponse`, `handleAskError`, `toggleSql(message)`.
- `frontend/src/router/index.js` — lisa rada `/ai-chat` (`aiChatRoute`, komponent `AiChatView`).
- `frontend/src/App.vue` — lisa ADMIN-i menüüsse link `<RouterLink class="nav-link" to="/ai-chat">Küsi AI-lt</RouterLink>`.
- Stiil: Options API vastavalt `docs/frontend/vue-komponendi-struktuur.md`-le — API päring `.then()/.catch()/.finally()` ahelana, vastus ja viga eraldi `handle...`-meetodites.

## Vastuvõtu kriteeriumid

- [ ] ADMIN-i menüüs on link "Küsi AI-lt", mis avab `/ai-chat`.
- [ ] Mitte-ADMIN kasutaja suunatakse "õigused puuduvad" lehele.
- [ ] Tühja vestluse korral kuvatakse näidisküsimused.
- [ ] Tühja küsimuse korral kuvatakse "Küsimus on kohustuslik" ja päringut ei saadeta.
- [ ] Küsimus ilmub vestlusesse kohe pärast saatmist ja väli tühjeneb.
- [ ] Saadetakse `POST /api/ask` body'ga `{ "question" }` läbi `AiService.js`.
- [ ] Päringu ajal on nupp ja väli keelatud ning "AI mõtleb..." spinner nähtav.
- [ ] Küsimusele "Millised juhid on aktiivsed?" ilmub vestlusesse AI tekstivastus.
- [ ] Küsimusele, millele andmebaas vastata ei saa (nt "Mis ilm homme on?"), ilmub vestlusesse "Sellele küsimusele ei saa andmebaasi põhjal vastata".
- [ ] Küsimusele kasutajate paroolide kohta ilmub vestlusesse keelduv vastus, mitte andmed.
- [ ] Backendi või AI rikke korral kuvatakse alert "AI vastust ei õnnestunud saada. Proovi uuesti." ja senine vestlus jääb alles.
- [ ] Mitu küsimust järjest kuvatakse vestluses õiges järjekorras.
- [ ] AI vastuse all on link "Näita SQL päringut"; vajutades kuvatakse päring, uuesti vajutades see peidetakse.
- [ ] SQL plokk on vaikimisi peidetud ja iga sõnumi SQL avaneb eraldi.
- [ ] 400 veasõnumite all SQL linki ei ole.
