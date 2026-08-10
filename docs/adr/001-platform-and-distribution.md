# ADR-001: Platform & Distribution Strategy

**Date:** 2026-08-10
**Status:** Accepted
**Deciders:** Zyxxyz

---

## Context

MEND needs to reach a phone, be written in Kotlin, and be downloadable from zyxwonderland.xyz. Two independent decisions sit inside that: which platform(s) to target, and how to get the built app into a user's hands — while keeping the project free to run.

## Decision Drivers

- Zero ongoing cost is a hard requirement, not a preference
- Kotlin is the mandated language
- The site already has a proven precedent for this exact shape of problem: The Warden and EPITOME Desktop are both built in their own repos and linked from the site as external GitHub Releases downloads
- Time-to-first-working-app matters more than maximum platform reach for a personal project

## Options Considered

### Option A — Android-only, Jetpack Compose
Native Android app targeting API 26+, built with Gradle/Kotlin/Compose. Ships as a single APK.

**Pros:** Simplest possible toolchain, entirely free (no paid developer account required to sideload), fastest to a working build, largest realistic single-platform reach for a free/sideloaded app.
**Cons:** iOS users can't install it.

### Option B — Kotlin Multiplatform (Android + iOS)
Shared Kotlin business logic (`shared` module) with Compose Multiplatform UI, mirroring this monorepo's existing `kotlin/` chess portfolio piece.

**Pros:** One codebase reaches both major mobile platforms.
**Cons:** iOS distribution is not free — it requires a Mac to build and an Apple Developer Program membership ($99/yr) even for ad-hoc/TestFlight distribution outside the App Store. That directly violates the free-to-run requirement. Compose Multiplatform's iOS target also adds real build/tooling complexity for a project explicitly meant to be lightweight.

### Option C — Publish to Google Play in addition to sideloading
Same Android app, but also submitted to the Play Store.

**Pros:** No "unknown sources" friction for installers, automatic updates, some legitimacy signal.
**Cons:** One-time $25 Play Developer fee (not free), and Play's review process applies real scrutiny to apps making health/dietary claims — a meaningful delay and rejection risk for a personal project not currently scoped to handle that review.

## Decision

**Chosen option: Option A, combined with sideloaded distribution (not Option C)** — Android-only Kotlin/Compose app, distributed as a self-signed APK attached to GitHub Releases in this repo, linked from the Technologist section of zyxwonderland.xyz. This is a direct repeat of the pattern already proven and accepted for The Warden and EPITOME Desktop.

## Consequences

**Positive:**
- Zero recurring or one-time cost
- Reuses a distribution pattern the site already has working infrastructure for (nav entry + card link out to an external repo's Releases page)
- Smallest possible toolchain surface for a solo-maintained project

**Negative / accepted tradeoffs:**
- No iOS app, ever, unless this ADR is revisited
- Users must manually enable "install unknown apps" to sideload, and must manually check for updates (no Play Store auto-update)
- No Play Store discoverability

**Risks:**
- Android may increase sideloading friction/warnings in future OS versions (Play Protect prompts have gotten more aggressive over time) — worth monitoring
- Self-signed APKs offer no third-party validation of publisher identity; users have to trust the GitHub repo/release directly

## Notes

- See ADR-004 for the backend platform decision that this app's networking layer depends on.
- Follow-up (not yet done): wire a GitHub Actions release workflow in this repo to build and attach a signed APK to a tagged Release, and update the site's nav link from the repo root to `/releases` once a real release exists.
