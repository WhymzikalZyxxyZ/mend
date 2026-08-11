# MEND — Risk & Gap Register

Living document. Update as decisions are made or new risks surface. Each entry: what the risk is, why it matters, and the current mitigation stance (even if the stance is "accepted, not yet mitigated").

## Health / liability risk — HIGH

Meal and restaurant suggestions target celiac disease and lactose intolerance, both conditions where a wrong "safe" label causes real physical harm (celiac reactions from trace gluten are a genuine medical event, not just discomfort for many people).

**Mitigation stance:**
- The app must carry a persistent, non-dismissible-on-first-run disclaimer: MEND is not medical advice, does not guarantee ingredient accuracy, and users must independently verify labels/ask restaurant staff directly.
- Curated recipe data (ADR-002) is preferred specifically because its provenance can be vetted, versus blindly trusting third-party tagging.
- Restaurant "safe for X" claims should never be presented as verified unless sourced from an explicit, attributable tag — general presence in OSM/an API result is not a safety claim.
- **Gap:** no legal/liability review has been done. Before any real-world release (even sideloaded to non-family users), consider a basic liability disclaimer reviewed against applicable local law — this is a personal project and this gap is currently accepted, not resolved.

## API / third-party cost & availability risk — MEDIUM

Any free-tier API (recipe API in ADR-002's expansion option, Overpass in ADR-003) can throttle, change pricing, or go away.

**Mitigation stance:** ADR-002's hybrid design means the app keeps working (on the curated set) if a recipe API disappears entirely. Overpass has no paid tier to fall back to if public instances become unreliable — that's an accepted risk of the free-only constraint (see ADR-003 Notes on self-hosting cost).

## Location privacy — MEDIUM

Nearby-restaurant search needs device location, which is sensitive data.

**Mitigation stance:**
- Location permission requested explicitly, with a clear reason shown before the OS prompt.
- Coarse location preferred over fine where sufficient (see `docs/architecture/overview.md`).
- Coordinates sent to the Worker should be rounded/bucketed, not raw precise GPS, to limit precision leaking into any logs or caches.
- No location data should be persisted server-side beyond short-TTL caching needed to serve the request.
- **Gap:** exact bucketing precision and cache TTL are not yet specified — needs a concrete number before implementation (e.g. round to ~1km grid, TTL measured in minutes not days).

## Distribution friction & security — MEDIUM

Sideloading (ADR-001) means no Play Protect vetting and no automatic update channel.

**Mitigation stance:** implemented — the app self-checks its installed version against the latest GitHub Release tag (`xyz.zyxwonderland.mend.update`) and shows a dismissible in-app banner when a newer one exists; see the README's "Updating" section. Users must still be clearly told, before installing, that they're trusting a self-signed APK from a personal GitHub repo, not a vetted store listing — that messaging doesn't exist yet and remains a gap.

## Backend free-tier ceiling — LOW (for now)

Cloudflare Workers' free tier (ADR-004) has a request cap; fine at personal-project scale, a real constraint if usage ever grows beyond that.

**Mitigation stance:** none needed yet; revisit ADR-004 if/when traffic approaches the free-tier limit.

## Data freshness / completeness (restaurants) — MEDIUM

OpenStreetMap coverage and structured dietary tagging (e.g. "serves gluten-free") vary heavily by region and are often simply absent (ADR-003).

**Mitigation stance:** UI copy must set honest expectations rather than imply comprehensive coverage; never upgrade "tag absent" to "not available" — absence of data is not evidence of absence of an option.

## No crash reporting / analytics pipeline — LOW

Skipping the Play Store (ADR-001) means no built-in crash reporting.

**Mitigation stance:** accepted for now. **Gap:** if the app grows past the maintainer's own devices, consider a lightweight opt-in crash reporter (e.g. self-hosted or a generous free tier) — not decided or built in this phase.

## Recipe/allergen data accuracy sourcing — HIGH (tracked, not yet resolved)

The curated dataset (ADR-002) is only as trustworthy as the sourcing process used to build it, and that process doesn't exist yet.

**Mitigation stance:** **Gap, explicitly not resolved by this documentation phase.** Before the curated dataset is populated, define where allergen/diet tags come from (e.g. cross-referencing ingredient lists against a known allergen database) and how errors get corrected. This is the single highest-priority open item before real content is added.
