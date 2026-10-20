# Soodora app

The fan and artist app: Vue 3, Ionic, Pinia, Vite, and Capacitor for the iOS build. See the [root README](../README.md) for the whole project and the API.

## Requirements

- Node.js 20 or later with npm (no Ionic CLI needed)
- The API running on `VITE_API_URL` (default http://localhost:8080)
- For iOS only: macOS with Xcode and CocoaPods

## Setup and run

```bash
cp .env.template .env
npm ci
npm run dev -- --port 5173      # http://localhost:5173
```

Keep port 5173: the API only accepts this origin (`APPLICATION_URL`).

| Variable | Purpose |
|---|---|
| `VITE_API_URL` | API base URL |
| `VITE_APPLICATION_URL` | public URL of this app (used for link pages) |
| `VITE_ADMINISTRATION_URL` | back-office URL, opened from the profile page of admins |
| `VITE_MAPBOX_GL_TOKEN` | Mapbox token for maps (optional) |

## Scripts

| Command | Purpose |
|---|---|
| `npm run dev` | development server with hot reload |
| `npm run build` | type check (`vue-tsc`) and production build in `dist/` |
| `npm run preview` | serve the production build |
| `npm run lint` / `npm run lint:fix` | ESLint |
| `npx vitest run` | unit tests (`npm run test:unit` watches) |
| `npm run test:e2e` | Cypress end-to-end tests (needs the app and the API running) |

## iOS

```bash
npm run build && npx cap sync ios && npx cap open ios
```
