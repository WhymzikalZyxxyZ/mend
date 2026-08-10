# MEND

**M**eals **E**ngineered for **N**utritional **D**ietary-needs

MEND is an Android app that suggests meal plans and recipes tailored to dietary restrictions — starting with lactose intolerance and celiac disease/gluten-free — and helps find nearby restaurant options. It's a free, personal, sideloaded app: no Play Store fee, no paid APIs, no ongoing hosting bill.

> **This repo is currently in its design/documentation phase.** No app functionality is implemented yet — see [Status](#status) below.

## Status

This repository currently contains:
- A minimal, buildable Android/Compose skeleton (`app/`) with no feature logic
- A stub Cloudflare Worker (`worker/`) exposing only a `/health` endpoint
- Full design documentation (this README, ADRs, architecture doc, risk register)

Not yet built: dietary rules engine, Room schema, meal plan generation, recipe browsing, restaurant lookup UI, curated recipe dataset, Worker routes beyond `/health`, and a release pipeline.

## Features (planned)

- Set dietary restrictions (lactose intolerance, celiac disease, extensible to others)
- Get meal plan suggestions filtered against those restrictions
- Browse recipes, tagged by allergen/diet compatibility
- Find nearby restaurants via OpenStreetMap data

## Why these choices

Full reasoning lives in `docs/adr/`, but in short:

| Decision | Choice | Why |
|---|---|---|
| Platform | Android-only, Kotlin + Jetpack Compose | iOS distribution isn't free (Apple Developer Program is $99/yr); Android-only keeps this a genuinely $0 project ([ADR-001](docs/adr/001-platform-and-distribution.md)) |
| Distribution | Sideloaded APK via GitHub Releases | No Play Store fee, no review process ([ADR-001](docs/adr/001-platform-and-distribution.md)) |
| Recipe data | Curated dataset + optional free-tier API expansion | Works offline day one; safety-critical dietary tags start from vetted data, not blind third-party trust ([ADR-002](docs/adr/002-recipe-data-source.md)) |
| Restaurant data | OpenStreetMap / Overpass API | No billing account required at all, unlike Google Places ([ADR-003](docs/adr/003-restaurant-data-source.md)) |
| Backend | Cloudflare Workers | Matches the free-tier platform already used by sibling projects on zyxwonderland.xyz ([ADR-004](docs/adr/004-backend-platform.md)) |

## Architecture

See [`docs/architecture/overview.md`](docs/architecture/overview.md) for module boundaries and data flow diagrams.

## Risks & known gaps

See [`docs/RISKS.md`](docs/RISKS.md) — read this before adding real content or shipping to anyone outside personal testing. The highest-priority open item is defining how allergen/diet data gets sourced and verified before the curated recipe dataset is populated.

## Building

Requires Android Studio (which supplies its own bundled Gradle and handles the wrapper automatically) or a local Gradle install (`gradle wrapper` once to generate `gradlew`/`gradlew.bat` if building from the command line).

```
git clone https://github.com/WhymzikalZyxxyZ/mend.git
cd mend
# Open in Android Studio, let it sync, then Run.
# Or, with Gradle installed locally:
gradle assembleDebug
```

Min SDK 26, target/compile SDK 35, Kotlin 2.0.21, Jetpack Compose.

### Worker (local dev)

```
cd worker
npm install
npm run dev
```

## Downloading

Once a release is cut, the APK will be attached to this repo's [Releases](https://github.com/WhymzikalZyxxyZ/mend/releases) page, and linked from the Technologist section of [zyxwonderland.xyz](https://zyxwonderland.xyz). Installing requires enabling "install unknown apps" for your browser/file manager, since this isn't distributed through the Play Store.

## License

MIT — see [LICENSE](LICENSE).
