# Releasing Findora

All builds run in **GitHub Actions** — no local Java or Android Studio needed.
Package name: `com.findora.app`.

There are two paths depending on what you need.

---

## A. Just want signed APK + AAB to test/sideload (no Play account)

Actions tab → **Release** → *Run workflow* (or push a `v*` tag).

The workflow will, in one run:
1. Run the unit tests.
2. **Generate a signing key in the cloud** (you have no local Java, so it's made on
   the runner) — unless you've already configured a stable key via secrets.
3. Build a **signed** `app-release.apk` **and** `app-release.aab`.
4. Attach both to a **GitHub Release** and upload them as the **`findora-release`**
   artifact.

Download `findora-release` → you get both signed files. The APK installs directly on
a device; the AAB is for Play.

> If the run generated its own key, it also uploads a **`findora-generated-keystore`**
> artifact (the `.jks`, its base64, and the passwords). **Keep it** if you might
> publish to Play later — Play requires every future update to use the *same* upload
> key. If you only ever sideload the APK, you can ignore it.

---

## B. Publishing to Google Play (stable upload key)

### Step 1 — Create your upload keystore (once)

Actions tab → **Generate Upload Keystore** → *Run workflow*. Enter:
- **Key alias** — e.g. `findora-upload`
- **Keystore password** and **Key password** — pick strong values and **write them
  down**; they cannot be recovered.

When it finishes, open the run → **Artifacts** → download **`findora-upload-keystore`**
(`upload.jks` + `upload.jks.base64`).

### Step 2 — Add the four repo secrets

Settings → *Secrets and variables* → *Actions* → **New repository secret**:

| Secret | Value |
|--------|-------|
| `KEYSTORE_BASE64` | full contents of `upload.jks.base64` |
| `KEYSTORE_PASSWORD` | the keystore password you chose |
| `KEY_ALIAS` | the alias you chose (e.g. `findora-upload`) |
| `KEY_PASSWORD` | the key password you chose |

Now **Release** and **Play Store AAB** will sign with this stable key every time.

### Step 3 — Build the signed AAB

Actions tab → **Play Store AAB** → *Run workflow* (pick `internal` / `draft` for a
first run). Download the **`findora-release-aab`** artifact — that's your
`app-release.aab`.

### Step 4 — Upload to Play Console

Create the app in [Play Console](https://play.google.com/console) → upload the `.aab`.
Keep **Play App Signing** enabled (default): Google holds the final signing key; your
keystore above is only the *upload* key.

> The first bundle for a brand-new app must be uploaded **manually**. After that, set
> the optional `PLAY_SERVICE_ACCOUNT_JSON` secret and the **Play Store AAB** workflow
> can publish updates automatically.

---

## Version numbers

`versionCode`/`versionName` are set from CI env vars (`app/build.gradle.kts`).
The workflows derive `versionCode = run_number + 1000`, so every run produces a higher
code and Play never rejects a duplicate. Local builds fall back to `versionCode = 1` /
`versionName = "1.0"`.

## Workflows at a glance

| Workflow | Trigger | Output |
|---|---|---|
| **CI** | push / PR to main | debug APK + unit tests |
| **Release** | `v*` tag or manual | signed APK **and** AAB + GitHub Release |
| **Play Store AAB** | manual | signed AAB for Play (+ optional auto-publish) |
| **Generate Upload Keystore** | manual | a stable upload key to store in secrets |
