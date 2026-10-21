# Soodora — A Platform for Independent Artists

![CI](https://github.com/alir961/soodora-platform/actions/workflows/ci.yml/badge.svg)

Soodora helps independent artists get discovered and grow their audience, and lets music fans find new talent. Artists publish posts (releases, events, merch drops) that can be exported for social media, build a link page, sell merch in a marketplace, and manage their business with media kits and quotes. Fans follow artists and get a personalised feed.

> **Epitech Innovative Project (EIP)**: a 3-person team, built over two years (2024–2026). See [My contribution](#my-contribution).

## Repository layout

| Part | Stack | What it is |
|---|---|---|
| [`backend/`](backend) | Java 21 · Spring Boot 3.5 · MariaDB · Flyway | REST API |
| [`frontend/`](frontend) | Vue 3 · Ionic · Capacitor · Pinia · Vite | Web and iOS app for artists and fans |
| [`administration/`](administration) | Vue 3 · Vite | Back office (users, roles, KPIs) |

## Architecture

The API is a multi-module Maven project with **layered, domain-driven** boundaries. Dependencies point inward, so the domain has no framework code:

```
api/             REST controllers, security (JWT filter, Spring Security), OpenAPI docs
service/         use cases: auth, posts, marketplace & Stripe, quotes & PDF export, playlists, link hubs…
domain/          entities, value objects, repository interfaces
infrastructure/  JPA persistence, JWT, Cloudinary storage, Google OAuth, SMTP
test/            shared test factories and fixtures
coverage/        JaCoCo aggregate report
```

**Integrations:** Stripe (checkout and signed webhooks), Google OAuth 2.0, Cloudinary CDN, YouTube, SMTP, Mapbox.

## Features

- **Explore and follow**: personalised feed, artist discovery, likes, reposts, comments
- **Posts**: releases, events, announcements and merch drops, exportable as social-media visuals
- **Link page**: a customisable page gathering an artist's streaming and social links
- **Marketplace**: merch sales through Stripe Checkout, stock management, sale notifications by e-mail
- **Business tools**: media kits, and client quotes with PDF export
- **Playlists** of audio posts, including an automatic "liked" playlist
- **Back office**: user and role management, indicators

## Quality

- **563 backend tests**: 284 unit (JUnit 5, Mockito) and 279 integration (Spring Boot Test, MockMvc, in-memory H2). JaCoCo coverage, Spotless formatting.
- Front ends: ESLint, Prettier, type-checked builds (`vue-tsc`), Vitest unit tests
- GitHub Actions runs everything on every push

### Security review

Before publishing, I reviewed the API (including code I had written myself) and fixed these issues. Each fix is covered by tests that failed before it:

| Issue | Impact | Fix |
|---|---|---|
| JWT `iat`/`exp` set in milliseconds, read by the library as seconds | Tokens expired in the year 58769: a stolen token never expired | Proper date claims, 1-hour validity |
| Expired or forged tokens threw from the auth filter | Users with an old token got 500 errors, even on public pages | Invalid tokens are treated as anonymous |
| Checkout price, vendor and buyer taken from the client, on a public route | Merch could be bought for any amount while the sale was recorded at full price | Price and vendor come from the stored product, the buyer from the session; stock and status checks |
| No ownership check on quotes | Any artist could read, edit, delete or export other artists' quotes, including their clients' personal data | Ownership checks on every quote and article operation, search scoped to the caller |
| Profile descriptions rendered with `v-html`, event titles and descriptions put raw into map popups | Stored XSS: a profile or event could run script in the viewer's session, admins included | Descriptions rendered as text; popup content escaped and links limited to http(s) |
| Stripe webhook not idempotent, stock checked then written without a transaction | A retried event recorded the sale twice; two buyers could both get the last item; a buyer paid when stock ran out got nothing | Each Checkout session is recorded once, stock is taken in one conditional update, a sold-out payment is refunded, and the sale records the amount Stripe charged |
| Google OAuth: session JWT used as the `state` and returned in `?token=`; states from `java.util.Random` kept forever; access tokens cached forever | The token leaked into URLs and history; YouTube calls failed an hour after linking | Random one-time states that expire, linking through an authenticated request, a one-time code instead of the token, access tokens refreshed when they expire |

## Supported platforms

| OS | Status |
|---|---|
| Linux | Supported (CI runs on Ubuntu) |
| macOS | Supported; also the only OS that can build the iOS app (Xcode) |
| Windows | Supported with Docker Desktop. Use `mvnw.cmd` instead of `./mvnw` and `copy` instead of `cp` in PowerShell, or run the commands in Git Bash / WSL 2 |

## Requirements

- **Docker** with the Compose plugin (`docker compose version`): runs MariaDB, the API and the Mailpit mail catcher
- **Node.js 20 or later** with npm, for the two front ends
- **Java 21** (JDK), only to run the API or its tests outside Docker. Maven is not needed: the repository ships the Maven wrapper (`./mvnw`)
- `openssl` (or any tool that prints random base64) for the JWT secret
- Free ports: **8080** (API), **3306** (MariaDB), **8025** and **1025** (Mailpit), **5173** (app), **5174** (back office)
- Optional accounts for the matching features: Stripe, Google OAuth, Cloudinary, Mapbox. Everything else works without them

## Getting started

```bash
git clone https://github.com/alir961/soodora-platform.git
cd soodora-platform

# API, database and a local mail catcher
cd backend
cp .env.template .env          # set JWT_SECRET (openssl rand -base64 64); the other keys can stay empty to start
docker compose up --build -d   # API on http://localhost:8080 (Swagger UI at /swagger-ui.html), mail inbox on http://localhost:8025

# Fan / artist app on http://localhost:5173
cd ../frontend && cp .env.template .env && npm ci && npm run dev -- --port 5173

# Back office on http://localhost:5174 (in another terminal, from the repository root)
cd administration && cp .env.template .env && npm ci && npm run dev -- --port 5174
```

The first `docker compose up --build` downloads the Maven dependencies and builds the API image, so it takes a few minutes. The API runs the Flyway database migrations at startup. `docker compose logs -f api` follows its logs, and `docker compose down` stops the stack (add `-v` to also delete the database volume).

The compose project is named `soodora` and the database container `mariadb`. If another checkout of the project already runs them, stop it first: compose would otherwise replace that container.

The API only accepts calls from those two origins (`APPLICATION_URL` / `ADMINISTRATION_URL`), so keep the ports.

**Accounts.** Sign up in the app: the confirmation e-mail lands in the Mailpit inbox (http://localhost:8025), and its link activates the account. To open the back office, give that account the admin role, then log in again:

```bash
docker exec mariadb mariadb -uuser -ppassword database -e \
  "INSERT INTO users_roles (user_id, role_id) SELECT id, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN') FROM users WHERE email = 'you@example.com';"
```

**Optional keys** in `backend/.env`: Stripe (marketplace checkout), Google (sign in with Google), Cloudinary (image and audio uploads), a real SMTP server instead of Mailpit (see the comments in `.env.template`), and `VITE_MAPBOX_GL_TOKEN` in `frontend/.env` for maps.

**Run the API without Docker** (hot reload), with the database and Mailpit still in Docker:

```bash
cd backend
docker compose up -d database mailpit
./mvnw install -DskipTests                 # once, builds the modules the api module depends on
set -a; . ./.env; set +a                   # export the .env variables
SMTP_HOST=localhost ./mvnw -pl api spring-boot:run
```

Run the checks the same way CI does (each line from the repository root):

```bash
cd backend && ./mvnw verify                                  # 563 unit + integration tests (in-memory H2, no database needed), coverage, formatting
cd frontend && npm run lint && npx vitest run && npm run build
cd administration && npm run lint && npx vitest run && npm run build
```

The production jar is `backend/api/target/api-0.0.1-SNAPSHOT.jar` (`java -jar` it with the `.env` variables exported, and `SPRING_DATASOURCE_URL` pointing at your MariaDB). Each front end builds to its `dist/` folder.

**iOS app** (macOS with Xcode and CocoaPods): `cd frontend && npm run build && npx cap sync ios && npx cap open ios`, then run it from Xcode.

## My contribution

About 270 of the project's commits are mine, across the API and the app:

- **Marketplace**: products, sales, Stripe Checkout integration and the "My sales" panel
- **Quote system**: creation, update, search and PDF export
- **Playlists** (including the automatic liked-audio playlist), **link pages**, **media kits**
- Search and pagination, request validation, and security configuration for public routes
- The security review and fixes above

## Team

Ali Raad · Timothé Medico · Mathis Legrand

## License

[MIT](LICENSE)
