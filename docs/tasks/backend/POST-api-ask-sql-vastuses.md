# Task: POST /api/ask — genereeritud SQL päring vastusesse

## Kirjeldus
`POST /api/ask` tagastab praegu ainult AI kokkuvõtte (`answer`). Lisada vastusesse ka AI genereeritud SQL päring (`sql`), et ADMIN näeks frontendis, millise päringu põhjal vastus tehti, ja saaks kontrollida, kas AI sai küsimusest õigesti aru.

Frontendi osa: `docs/tasks/frontend/AI-vestlus-NL-to-SQL.md` (jaotis "SQL päringu kuvamine").

## Endpoint

`POST /api/ask` — request ei muutu.

`AiAskRequestDto.java` — request body (**muutmata**):
```json
{
  "question": "Millised juhid on aktiivsed?"
}
```

`AiAskResponseDto.java` — response (200), **uus väli `sql`**:
```json
{
  "answer": "Aktiivsed on kaks juhti: Mart Tamm ja Jaan Kask.",
  "sql": "SELECT name FROM liftertrans_project.driver WHERE active = true"
}
```

**Veateated:** ei muutu (400 `CANNOT_ANSWER`, `FORBIDDEN_SQL`, `INCORRECT_INPUT`). Vea korral SQL-i vastusesse ei lisata.

## Implementatsiooni märkused

1. **Eralda AI mudeli DTO API vastuse DTO-st.** Praegu loeb `callLlm()` AI mudeli JSON vastuse (`{"answer": "..."}`) otse `AiAskResponseDto`-sse. Spring AI `.entity(...)` genereerib klassi väljadest mudelile JSON skeemi — kui lisada `AiAskResponseDto`-sse väli `sql`, hakkab mudel ka kokkuvõtte sammus `sql` välja täitma või sellega segadusse minema. Seepärast:
   - loo uus DTO AI mudeli vastuse jaoks, nt `dto/LlmAnswerDto.java` (ainult väli `answer`, `@NoArgsConstructor` + setterid);
   - `callLlm()` tagastab `LlmAnswerDto`;
   - `AiAskResponseDto` jääb ainult API vastuseks, lisa väli `private String sql;`.
2. **Service** `NlToSqlService.ask()` — pärast kokkuvõtte saamist pane vastusesse nii `answer` kui juba olemasolev muutuja `generatedSql`:
   ```java
   return AiAskResponseDto.builder()
           .answer(summary)
           .sql(generatedSql)
           .build();
   ```
3. **Controller** `AiController.java` — muutusi ei ole, uuenda ainult `@Operation` kirjeldust / lisa vastuse näide Swaggerisse.
4. **Turvalisus:** SQL-i näitamine avaldab andmebaasi tabelite ja veergude nimed. See on lubatud, sest vaade on ainult ADMIN-ile — kuid `/api/ask` ei kontrolli praegu backendis rolli (vt FE taski turvalisuse märkust). Tabel `user` ja `password_hash` on SQL valideerimisega juba keelatud, need ei jõua vastusesse.

## Seotud failid
- `dto/AiAskResponseDto.java`
- `dto/LlmAnswerDto.java` (uus)
- `service/NlToSqlService.java`
- `controller/AiController.java`

## Vastuvõtu kriteeriumid
- [ ] `POST /api/ask` küsimusega "Millised juhid on aktiivsed?" tagastab 200 ning vastuses on nii `answer` kui `sql`.
- [ ] `sql` on täpselt see päring, mis andmebaasis käivitati.
- [ ] `answer` on endiselt tavakeelne kokkuvõte (AI ei pane sinna SQL-i ega JSON-it).
- [ ] Vea korral (400 `CANNOT_ANSWER`, `FORBIDDEN_SQL`) vastus ei muutu, SQL-i ei tagastata.
- [ ] Swaggeris on vastuse näide koos `sql` väljaga.
