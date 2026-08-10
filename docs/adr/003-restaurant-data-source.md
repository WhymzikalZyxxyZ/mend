# ADR-003: Restaurant Lookup Data Source

**Date:** 2026-08-10
**Status:** Accepted
**Deciders:** Zyxxyz

---

## Context

MEND needs to surface nearby restaurant options, ideally ones with dietary-restriction-friendly menus. This requires a geodata source callable from the Worker (never directly from the client with an exposed key) and, again, must stay free.

## Decision Drivers

- Cost must stay at $0, including no requirement to attach a billing account/credit card
- Location data is sensitive — the fewer third parties it touches, the better (see `docs/RISKS.md`)
- Good-enough data beats perfect data for a personal project's first version

## Options Considered

### Option A — OpenStreetMap / Overpass API
Query OSM's Overpass API for nearby nodes tagged `amenity=restaurant` (and related), filtered by a bounding box around the user's coarse location.

**Pros:** Completely free, no API key, no billing account required at all — nothing to leak, nothing to get billed for. Community-maintained, global coverage.
**Cons:** Data completeness and freshness vary a lot by region; structured tags like "serves gluten-free" are inconsistent or absent in most areas; Overpass has fair-use rate limits and public instances can be slow or temporarily unavailable under load.

### Option B — Google Places API
Google's Places Nearby Search / Details API.

**Pros:** Much richer, more consistently structured data, better coverage in most regions, more filter options.
**Cons:** Requires a Google Cloud project with billing enabled even to use the free monthly credit — a card on file that can be charged if usage grows past the credit or the credit policy changes. Directly conflicts with the "free, no ongoing risk" requirement for a personal project.

## Decision

**Chosen option: Option A — OpenStreetMap / Overpass API.** The billing-account requirement of Option B is disqualifying for a project with a hard $0 budget; Overpass's data-quality gaps are an acceptable tradeoff that can be communicated honestly in the UI rather than hidden.

## Consequences

**Positive:**
- Zero cost and zero billing-account exposure
- No API key to protect or rotate

**Negative / accepted tradeoffs:**
- UI copy must set honest expectations about restaurant data completeness, especially outside dense urban areas
- "Gluten-free menu available"-style claims generally can't be sourced reliably from OSM tags alone and should not be presented as verified unless a specific tag is present and its provenance is clear

**Risks:**
- Public Overpass instances are shared infrastructure with fair-use limits; the Worker should cache results and apply its own rate limiting per the existing `the-locator` Worker's pattern (see `worker/src/index.js` and the sibling `the-locator/worker/src/index.js` in the main site repo) rather than hammering Overpass directly per app request
- If a self-hosted Overpass instance is ever needed for reliability, that reintroduces a hosting cost this ADR currently avoids

## Notes

- Location permission must be requested explicitly and scoped to coarse location where sufficient — see `docs/RISKS.md`.
