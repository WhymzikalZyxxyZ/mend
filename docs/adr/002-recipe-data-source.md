# ADR-002: Recipe Data Source

**Date:** 2026-08-10
**Status:** Accepted
**Deciders:** Zyxxyz

---

## Context

MEND needs recipes that can be filtered by dietary restriction (lactose intolerance, celiac disease/gluten-free, and similar). The data needs to be trustworthy enough that "safe for celiac" claims aren't casually wrong (see `docs/RISKS.md`), and it needs to stay within a $0 budget.

## Decision Drivers

- Cost must stay at $0
- Allergen/dietary tags need to be accurate enough to be safety-relevant, not just flavor text
- The app is meant to be lightweight — avoid over-building a content pipeline before there are users
- Should degrade gracefully offline (core README/architecture requirement)

## Options Considered

### Option A — Hand-curated JSON dataset shipped in the app
A small, manually vetted set of recipes with explicit allergen/diet tags, bundled as app assets and cached in Room.

**Pros:** Free, no rate limits, no third-party dependency, tags are exactly as trustworthy as the curation effort put in, works fully offline.
**Cons:** Manual upkeep, limited breadth, doesn't grow on its own.

### Option B — Third-party recipe API proxied through the Worker
E.g. Spoonacular's free tier, which supports `intolerances` and `diet` query parameters natively. The Worker holds the API key as a secret and proxies/caches requests.

**Pros:** Much larger recipe breadth without manual content work; filtering logic partly outsourced to a vendor that already models dietary tags.
**Cons:** Free tier is capped (e.g. ~150 points/day on Spoonacular) and can change or disappear; still needs the app to independently verify safety-critical claims rather than blindly trusting a third party's tagging (see RISKS.md); adds a live dependency and a secret to manage; doesn't work offline without caching.

### Option C — Hybrid: curated core set + optional API-backed expansion
Ship a small curated, high-confidence dataset as the offline default. When online and within quota, the Worker can proxy a free-tier API for a larger result set, clearly distinguished in the UI from the curated set, with graceful fallback to curated-only when the quota is exhausted or the network is unavailable.

**Pros:** Best of both — works offline on day one with data the maintainer trusts, can grow without a rewrite, degrades gracefully instead of breaking when a free tier caps out.
**Cons:** More moving parts than either pure option; requires UI to clearly label data provenance so a quota-exhausted state doesn't look like "no results."

## Decision

**Chosen option: Option C (hybrid)** — curated dataset is the trusted offline default; API-backed expansion is an enhancement, not a dependency. This can be revisited if the curated set proves sufficient on its own, in which case the API integration is simply never turned on.

## Consequences

**Positive:**
- App is useful with zero network access and zero API keys from day one
- Safety-critical dietary filtering starts from data the maintainer directly vetted
- Growth path exists without committing to ongoing API cost or availability risk

**Negative / accepted tradeoffs:**
- Two data paths to maintain and keep visually distinct in the UI
- Curating the initial dataset is manual work that has to happen before the app is useful

**Risks:**
- See `docs/RISKS.md` — health/liability risk and API/cost risk both apply directly here
- Any chosen third-party API's terms of service must be checked before proxying its data — some recipe APIs restrict caching/redistribution even on paid tiers

## Notes

- Concrete API vendor selection (if/when the API-backed expansion is built) is deferred to implementation time, not fixed by this ADR.
- The curated dataset format and allergen-tag schema belongs in `docs/architecture/overview.md`, not here.
