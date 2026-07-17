# Auth Workflow

Dieser Service ist Teil des zentralen Auth-Flows der Anwendung. Das Gateway ist die erste Eintrittsstelle für Requests aus dem Frontend und entscheidet, ob ein Request ohne Authentifizierung durchgelassen wird oder ein JWT erforderlich ist.

## 1. Registrierung

Ablauf:

1. Das Frontend sendet den Request zur Benutzererstellung an das Gateway.
2. Das Gateway routet öffentliche Auth-Pfade ohne JWT-Prüfung an den Auth-Service weiter.
3. Der Auth-Service verarbeitet den Request im `UserRestController`.
4. Der `UserService` legt den Benutzer an und speichert das Passwort gehasht.
5. Die Antwort geht über das Gateway zurück an das Frontend.

Wichtige Stellen:

- Gateway-Route für Auth: `gateway/src/main/resources/application.yaml`
- Public-Bypass im Gateway: `gateway/src/main/java/com/dennis/gateway/components/AuthFilter.java`
- Registrierung im Auth-Service: `auth/src/main/java/com/dennis/auth/controller/UserRestController.java`
- Gemeinsames Path-Prefix: `microservice-common/src/main/java/com/dennis/common/configs/CommonAutoConfiguration.java`
- Prefix-Konfiguration: `auth/src/main/resources/application.properties` mit `app.web.path-prefix=auth`

Externer Pfad zur Registrierung:

- `POST /auth/api/users`

## 2. Login

Ablauf:

1. Das Frontend sendet Benutzername und Passwort an das Gateway.
2. Das Gateway behandelt Login-Routen als öffentlich.
3. Der Auth-Service prüft die Credentials gegen den gespeicherten Benutzer.
4. Bei Erfolg erzeugt `JwtService` ein signiertes JWT.
5. Das Token wird an das Frontend zurückgegeben.

Wichtige Stellen:

- Login-Controller: `auth/src/main/java/com/dennis/auth/controller/AuthController.java`
- Login-Logik: `auth/src/main/java/com/dennis/auth/services/AuthService.java`
- JWT-Erzeugung: `auth/src/main/java/com/dennis/auth/services/JwtService.java`

## 3. Geschützte API-Calls

Ablauf:

1. Das Frontend sendet einen Request mit `Authorization: Bearer <token>` an das Gateway.
2. `AuthFilter` prüft, ob der Request unter `/auth/**` fällt.
3. Alle anderen Requests werden gegen das JWT validiert.
4. Bei gültigem Token extrahiert das Gateway Claims wie `sub` und `role`.
5. Das Gateway ergänzt diese Informationen als Header:
   - `X-User-Email`
   - `X-User-Role`
6. Danach routet das Gateway den Request an den Zielservice, z. B. `career`.

Wichtige Stellen:

- JWT-Prüfung im Gateway: `gateway/src/main/java/com/dennis/gateway/components/JwtUtil.java`
- Request-Filter im Gateway: `gateway/src/main/java/com/dennis/gateway/components/AuthFilter.java`
- Route für Career-Service: `gateway/src/main/resources/application.yaml`
- Route für Auth-Service: `gateway/src/main/resources/application.yaml`

## 4. Konfiguration und Infrastruktur

Relevante Einstellungen:

- `gateway/src/main/resources/application.properties`
  - `jwt.secret=...`
- `auth/src/main/resources/application.properties`
  - `jwt.secret=...`
  - `app.web.path-prefix=auth`
  - `server.port=8082`
- `career-service/src/main/resources/application.properties`
  - `app.web.path-prefix=career`
  - `server.port=8081`

Gemeinsame Annahme:

- Gateway und Auth-Service nutzen denselben `jwt.secret`.
- Dadurch kann das Gateway Tokens prüfen, die der Auth-Service erzeugt.

## 5. Kurzfassung

- Registrierung und Login sind öffentlich und laufen über das Gateway zum Auth-Service.
- Nach dem Login kommt ein JWT zurück.
- Alle weiteren Requests brauchen das JWT im `Authorization`-Header.
- Das Gateway validiert das Token zentral und reicht User-Infos als Header weiter.
- Fach-Services wie `career` bekommen dadurch bereits vorgeprüfte Requests.
