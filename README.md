# liftertrans-project

Vali-IT grupiprojekti toorik: Spring Boot backend + Vue 3 frontend.

## Struktuur

```
backend/    Spring Boot 4.x / Java 21 REST API
frontend/   Vue 3 + Vite SPA
docs/       Dokumentatsioon ja andmebaasiskriptid
```

Backend ja frontend on eraldi arendatavad ja käivitatavad rakendused. Ehitus-, käivitus- ja testikäskude ning arhitektuuri kohta vaata `backend/CLAUDE.md` ja `frontend/CLAUDE.md`.

## Kiirstart (backend)

Eeldused: Java 21, PostgreSQL (kohalik server käib).

1. Loo andmebaas (nimi vastavalt oma valikule, vaikimisi `vali_it`):
   ```bash
   psql -U postgres -c "CREATE DATABASE vali_it;"
   ```
2. Loo skeem, tabelid ja algandmed **ühe psql seansi sees** (kaustast `docs/database`):
   ```bash
   psql -U postgres -d vali_it \
     -f docs/database/1_reset_database.sql \
     -f docs/database/2_create.sql \
     -f docs/database/3_import.sql
   ```
3. Käivita backend, määrates vajadusel oma pordi/kasutaja (vaikeväärtused: port `5432`, andmebaas `vali_it`, kasutaja `postgres`, parool `student123`):
   ```bash
   cd backend
   DB_PORT=5432 DB_NAME=vali_it DB_USER=postgres DB_PASSWORD=student123 ./gradlew bootRun
   ```
4. Kontrolli, et rakendus töötab:
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health check: http://localhost:8080/actuator/health
