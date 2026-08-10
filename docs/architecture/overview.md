# MEND — Architecture Overview

See `docs/adr/` for the reasoning behind each decision referenced here.

## System shape

```
┌─────────────────────────────┐
│  Android app (app/)          │
│  Kotlin + Jetpack Compose    │
│                               │
│  ┌─────────────────────────┐ │
│  │ UI (Compose screens)     │ │
│  └────────────┬─────────────┘ │
│               │                │
│  ┌────────────▼─────────────┐ │
│  │ Domain layer              │ │
│  │  - dietary rules engine   │ │
│  │  - meal plan generator    │ │
│  └────────────┬─────────────┘ │
│               │                │
│  ┌────────────▼─────────────┐ │
│  │ Data layer                 │ │
│  │  - Room (local cache +     │ │
│  │    curated recipe set)     │ │
│  │  - Ktor client              │ │
│  └────────────┬─────────────┘ │
└───────────────┼────────────────┘
                │ HTTPS (only when online)
                ▼
┌─────────────────────────────┐
│  Cloudflare Worker            │
│  (worker/, ADR-004)           │
│                                │
│  /health                      │
│  /restaurants  → Overpass API │  (ADR-003)
│  /recipes      → recipe API   │  (ADR-002, optional expansion)
└─────────────────────────────┘
```

## Module boundaries

- **UI (Compose screens):** presentation only. No network or database calls happen here directly; screens observe state from ViewModels.
- **Domain layer:** the dietary-restriction rules engine (e.g. "exclude any recipe/menu item tagged with an allergen the user has flagged") and meal-plan generation logic. Pure Kotlin, no Android framework dependency, so it can be unit tested without instrumentation.
- **Data layer:**
  - Room database is the single source of truth on-device: user's dietary restrictions/preferences, the curated recipe dataset (ADR-002 Option C), and a cache of restaurant/recipe API responses with a TTL.
  - Ktor HTTP client talks only to the MEND Worker — never directly to Overpass or any third-party recipe API. This keeps API keys server-side and lets the Worker apply caching/rate limiting.
- **Worker:** thin proxy + cache in front of Overpass (ADR-003) and, if ADR-002's API-backed expansion is built, the recipe API. Holds all third-party secrets. No user accounts or persistent user data live here in this version — see `docs/RISKS.md` on location privacy.

## Data flow: "suggest a meal plan"

1. User sets dietary restrictions once (stored locally in Room, never sent anywhere required).
2. Domain layer filters the curated recipe set (always available, works offline) against those restrictions.
3. If online and the API-backed expansion (ADR-002) is enabled, the app additionally asks the Worker for more recipes matching the same restrictions; results are clearly labeled by source in the UI and merged with the curated results.
4. Meal plan is assembled from the filtered pool and cached locally so the last plan remains viewable offline.

## Data flow: "nearby restaurants"

1. App requests coarse (not fine, unless the user explicitly wants precise results) device location, only after an explicit permission prompt explaining why.
2. App calls the Worker's `/restaurants` endpoint with a rounded/bucketed coordinate (never raw precise GPS) to limit location precision leaking into logs or caches.
3. Worker queries Overpass (with its own caching layer, per ADR-003's note on protecting shared Overpass infrastructure) and returns a normalized list.
4. Results are cached locally with a short TTL so repeat views don't require a fresh network call.

## Offline behavior

- Dietary restrictions, the curated recipe set, and the last-generated meal plan are always available offline (Room-backed).
- Restaurant lookups and API-backed recipe expansion require connectivity; the UI must distinguish "no results because offline" from "no results found" rather than presenting them identically.

## Explicitly out of scope for this documentation phase

No Compose screens, Room schema/entities, rules engine implementation, or Worker route logic beyond `/health` exist yet — this document describes the target shape referenced by the ADRs, to guide the first implementation pass.
