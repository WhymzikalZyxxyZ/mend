# ADR-004: Backend Platform

**Date:** 2026-08-10
**Status:** Accepted
**Deciders:** Zyxxyz

---

## Context

MEND's Overpass proxying/caching (ADR-003) and optional recipe API proxying (ADR-002) both need server-side code to hold secrets and apply rate limiting/caching — logic that shouldn't run directly on-device. A backend platform needs to be chosen, for free.

## Decision Drivers

- Cost must stay at $0 at MEND's expected traffic level
- Prefer not to introduce a new hosting provider/account this personal project has to separately maintain
- The main site (zyxwonderland.xyz) already runs several small services this way

## Options Considered

### Option A — Cloudflare Workers
Serverless functions on Cloudflare's edge network, same platform already used for `anonymail`, `the-locator`, `elinal`, and `epitome`'s worker in the main site's monorepo (per `.github/workflows/deploy.yml`).

**Pros:** Generous free tier (100,000 requests/day), already proven at this traffic scale by sibling projects, zero new account/provider to set up, existing conventions to copy directly (CORS handling, structured logging, per-IP in-memory rate limiting — see `the-locator/worker/src/index.js` in the main site repo).
**Cons:** Cold-start/edge runtime constraints (no long-lived state without Durable Objects/KV), free-tier request cap could matter if traffic ever grows meaningfully.

### Option B — Fully on-device, no backend
Skip a backend entirely; call Overpass and any recipe API directly from the Android app.

**Pros:** Simplest possible architecture, one fewer thing to deploy/maintain.
**Cons:** This was already rejected in the session-level decision to use "client + lightweight free backend" — direct client calls would expose any recipe API key inside the APK (trivially extractable) and forfeit server-side caching/rate-limiting that protects both the free API quota and the shared Overpass infrastructure from abuse.

### Option C — Other free-tier serverless (Render, Fly.io, Vercel Functions, AWS Lambda free tier)
Any of several other free-tier-capable platforms; `editor-service` in the main monorepo already uses Fly.io, for instance.

**Pros:** Also viable and free at small scale.
**Cons:** Introduces a platform/account not already used for this exact purpose (proxying+caching a third-party API behind CORS) — Cloudflare Workers is the more direct precedent among the site's existing services for this specific shape of problem, and consolidating reduces the number of dashboards/accounts to keep track of.

## Decision

**Chosen option: Option A — Cloudflare Workers**, following the exact pattern already established by `the-locator`'s worker in the main site repo.

## Consequences

**Positive:**
- No new hosting account or billing surface introduced
- Direct code/pattern reuse from `the-locator/worker/src/index.js` (CORS allow-list regex, security headers, per-IP rate limiter, structured JSON logging)
- Free tier comfortably covers a personal-project traffic level

**Negative / accepted tradeoffs:**
- If MEND ever needs persistent server-side state beyond simple caching, KV or Durable Objects will need to be added (both still free-tier-capable at small scale, but add complexity)

**Risks:**
- Free-tier request cap becomes a real constraint if usage grows past personal-project scale — see `docs/RISKS.md`
- The per-IP in-memory rate limiter pattern reused from `the-locator` is isolate-local, not globally consistent (documented as an accepted tradeoff there too)

## Notes

- `worker/wrangler.toml` and `worker/src/index.js` in this repo are the initial stub following this decision; they currently only expose a `/health` endpoint.
- Deployment wiring (GitHub Actions job analogous to the main site's `deploy.yml` Cloudflare Workers jobs) is a follow-up, not yet built.
