# MEND

**M**eals **E**ngineered for **N**utritional **D**ietary-needs

MEND is an Android app that suggests meal plans and recipes tailored to dietary restrictions — starting with lactose intolerance and celiac disease/gluten-free — and helps find nearby restaurant options. It's a free, personal, sideloaded app: no Play Store fee, no paid APIs, no ongoing hosting bill.

> **This repo is currently in its design/documentation phase.** No app functionality is implemented yet — see [Status](#status) below.

## Status

This repository currently contains:
- A minimal, buildable Android/Compose skeleton (`app/`) with no feature logic
- An in-app update checker (self-sideloaded apps get no Play Store auto-update — see [Updating](#updating))
- A release pipeline that builds and signs an APK and attaches it to a GitHub Release on tag push (see [Cutting a release](#cutting-a-release))
- A stub Cloudflare Worker (`worker/`) exposing only a `/health` endpoint
- Full design documentation (this README, ADRs, architecture doc, risk register)

Not yet built: dietary rules engine, Room schema, meal plan generation, recipe browsing, restaurant lookup UI, curated recipe dataset, Worker routes beyond `/health`.

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

## Updating

Since MEND isn't on the Play Store, there's no automatic-update channel. On launch, the app pings this repo's [GitHub Releases API](https://api.github.com/repos/WhymzikalZyxxyZ/mend/releases/latest) (at most once every 24 hours, silently ignoring failures — offline, rate-limited, or no release cut yet) and shows a dismissible in-app banner if a newer version is available. It never auto-downloads or auto-installs anything; "View" just opens the Release page in a browser so you can grab the APK yourself.

## Cutting a release

The release workflow (`.github/workflows/release.yml`) builds and signs the release APK and attaches it to a GitHub Release whenever a tag matching `v*.*.*` is pushed:

```
git tag v0.2.0
git push origin v0.2.0
```

This requires a signing keystore, set up once:

1. Generate a keystore (requires a JDK): `keytool -genkeypair -v -keystore mend-release.keystore -alias mend -keyalg RSA -keysize 2048 -validity 10000`
2. **Back this file up somewhere safe outside git.** It's the app's permanent signing identity — losing it means any future release can no longer update an already-installed copy of the app; users would have to uninstall and reinstall from scratch.
3. Base64-encode it and add these as repo secrets (Settings → Secrets and variables → Actions):
   - `RELEASE_KEYSTORE_BASE64` — `base64 -w0 mend-release.keystore` (or `certutil -encode` on Windows, stripping the header/footer lines)
   - `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` — matching what you set in step 1

Without these secrets, the workflow fails fast at the "Decode release keystore" step rather than silently producing an unsigned APK. Local/PR builds (`gradle assembleRelease` without the env vars set) also produce an unsigned build — fine for testing the build itself, but not installable as-is.

## License

MIT — see [LICENSE](LICENSE).
