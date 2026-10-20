# Soodora back office

The administration panel: Vue 3 and Vite. Admins manage users and roles and follow the platform indicators. See the [root README](../README.md) for the whole project, the API and how to give an account the admin role.

## Requirements

- Node.js 20 or later with npm
- The API running on `VITE_API_URL` (default http://localhost:8080)

## Setup and run

```bash
cp .env.template .env
npm ci
npm run dev -- --port 5174      # http://localhost:5174
```

Keep port 5174: the API only accepts this origin (`ADMINISTRATION_URL`). Admins usually open the panel from their profile page in the app, which passes their session token.

| Variable | Purpose |
|---|---|
| `VITE_API_URL` | API base URL |
| `VITE_APPLICATION_URL` | URL of the fan / artist app |

## Scripts

| Command | Purpose |
|---|---|
| `npm run dev` | development server with hot reload |
| `npm run build` | type check (`vue-tsc`) and production build in `dist/` |
| `npm run preview` | serve the production build |
| `npm run lint` / `npm run lint:fix` | ESLint |
| `npx vitest run` | unit tests |
| `npm run test:e2e` | Cypress end-to-end tests |
