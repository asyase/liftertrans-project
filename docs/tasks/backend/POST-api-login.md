# Task: POST /api/login, GET /api/auth/me, POST /api/logout

## Kirjeldus
Luua LIFTERTRANS backend autentimise API ADMIN ja DRIVER kasutajatele.

## Endpointid
1. `POST /api/login`
   - Request: `AuthRequestDto` (email, password)
   - Response: `AuthResponseDto` (userId, email, roleName, driverId)
   - Veaolukorrad:
     - 400 Bad Request: `REQUIRED_FIELDS_MISSING` ("Täida kõik väljad")
     - 401 Unauthorized: `INCORRECT_CREDENTIALS` ("Vale e-post või parool")
2. `GET /api/auth/me`
   - Request: tühi
   - Response: `CurrentUserDto` (userId, email, roleName, driverId)
   - Veaolukord: 401 Unauthorized (`UNAUTHORIZED`, "Kasutaja ei ole sisse logitud")
3. `POST /api/logout`
   - Request: tühi
   - Response: 204 No Content

## Seotud tabelid
- `user`
- `role`
- `driver`
