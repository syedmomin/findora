# Findora

Smart OCR • Document Search • Productivity — a native Android app that finds
information hidden inside screenshots, receipts, IDs, and documents using fast,
**on-device** OCR and full-text search.

Built with **Kotlin + Jetpack Compose**, following the design system in
[`applicaiton.md`](applicaiton.md).

---

## Requirements

This project is **source-complete** but must be built with the Android toolchain,
which is not installed in the environment it was authored in.

To build and run you need:

- **Android Studio** (Ladybug 2024.2+ recommended) — bundles the JDK 17 + Android SDK
- An **emulator or physical device** running **Android 7.0 (API 24)** or newer
- On first launch, ML Kit downloads its Latin-script OCR model (a few MB)

## Build & run

1. Open the project root (`D:\projects\findora`) in Android Studio.
2. Let Gradle sync (it will download dependencies and generate the Gradle wrapper JAR).
3. Pick a device/emulator and press **Run ▶**.

From the command line (once a JDK 17 + Android SDK are on the machine and
`local.properties` points at the SDK):

```
./gradlew :app:assembleDebug     # build the APK
./gradlew test                   # run the JVM unit tests
```

## What works

| Screen | Status |
|---|---|
| Splash | Brand-blue, geometric **F**, subtle scale+fade |
| Home | Greeting, search bar, recent searches, category row, recent documents |
| Scanner | CameraX preview, flash, gallery import, crop → **real ML Kit OCR** → save |
| Search | Debounced instant full-text search (Room FTS4) with highlighted snippets |
| Document detail | Image, recognized text, **Share / Copy / Rename / Delete** |
| Categories | Grid with live per-category counts, plus per-category document list |
| Settings | Theme (System/Light/Dark, persisted), storage, privacy, about |

Field-aware search extracts **emails, phones, dates, amounts, and invoice/order
numbers** from OCR text and indexes them alongside the raw text.

## Architecture

```
com.findora.app
├── data/
│   ├── db/          Room: documents table + documents_fts (FTS4) index + DAO
│   ├── ocr/         OcrService — Google ML Kit Text Recognition (on-device)
│   ├── search/      EntityExtractor (regex fields) + snippet/highlight builder
│   ├── repository/  DocumentRepository, SettingsRepository (DataStore)
│   └── ImageStore   copies scans into app-private storage
├── di/              AppContainer — lightweight manual DI (no Hilt/annotation procs)
└── ui/
    ├── theme/       Colors, Typography, Shapes (radius scale), light/dark Theme
    ├── components/  SearchBar, DocumentCard, CategoryCard, buttons, skeletons…
    ├── navigation/  Routes, bottom-nav scaffold, NavHost
    └── screens/     splash · home · search · scanner · detail · categories · settings
```

- **Pattern:** MVVM. Each screen has a `ViewModel` exposing `StateFlow`, obtained
  via a colocated `viewModelFactory`.
- **Privacy-first:** OCR and search run entirely on-device; documents never leave
  the phone.

## Tests

Pure, deterministic logic is unit-tested (`app/src/test`):

- `EntityExtractorTest` — email/phone/date/amount/invoice extraction + category guess
- `SnippetTest` — snippet windowing and highlight ranges

## Deployment (CI → signed APK + AAB)

Everything builds in **GitHub Actions** — no local Java needed. See
[`RELEASING.md`](RELEASING.md) for the full guide.

- **CI** (`ci.yml`) — on push/PR: compile, run unit tests, produce a debug APK.
- **Release** (`release.yml`) — on a `v*` tag or manual run: run tests, then build a
  **signed** `app-release.apk` **and** `app-release.aab`, and attach both to a GitHub
  Release. Signs with the committed stable key in `keystore-backup/` (or a repo secret).
- **Play Store AAB** (`playstore.yml`) — manual: signed `.aab` for Play, with optional
  auto-publish.
- **Generate Upload Keystore** (`generate-keystore.yml`) — one-off cloud key generation.

A real, working signing keystore already exists at `keystore-backup/findora-upload.jks`
(PKCS12, alias `findora-upload`; credentials in `keystore-backup/CREDENTIALS.txt`), so
release builds are signed out of the box. **Move it into repo Secrets and delete it
from git if this repo is public** — see `keystore-backup/README.md`.

## Notes / next steps

- **Fonts:** the spec calls for *Inter*. Drop the `.ttf` files into `res/font/` and
  point `FindoraFontFamily` (in `ui/theme/Type.kt`) at them to match the brand exactly.
- **Launcher PNGs:** adaptive icons (API 26+) and a vector fallback (API 24–25) are
  included. You can regenerate raster densities via Android Studio's Image Asset tool
  if you want crisper legacy icons.
