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

## Getting started

Requirements: Java 21, Node.js 20, Docker.

```bash
# API and database
cd backend
cp .env.template .env          # fill in your own keys (Stripe, Google, Cloudinary, SMTP)
docker compose up --build -d   # MariaDB + API on http://localhost:8080 (Swagger UI at /swagger-ui.html)

# Fan / artist app
cd ../frontend && cp .env.template .env && npm ci && npm run dev

# Back office
cd ../administration && npm ci && npm run dev
```

Run the checks the same way CI does:

```bash
cd backend && ./mvnw verify                                  # unit + integration tests, coverage, formatting
cd frontend && npm run lint && npx vitest run && npm run build
```

## My contribution

About 270 of the project's commits are mine, across the API and the app:

- **Marketplace**: products, sales, Stripe Checkout integration and the "My sales" panel
- **Quote system**: creation, update, search and PDF export
- **Playlists** (including the automatic liked-audio playlist), **link pages**, **media kits**
- Search and pagination, request validation, and security configuration for public routes
- The security review and fixes above

## Team

Ali Raad · Timothé Medico · Mathis Legrand
