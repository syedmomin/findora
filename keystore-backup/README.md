# keystore-backup

Contains the app's **stable signing/upload key**.

| File | What it is |
|------|-----------|
| `findora-upload.jks` | the keystore (PKCS12), alias `findora-upload` |
| `findora-upload.jks.base64` | same key base64-encoded (for the `KEYSTORE_BASE64` secret) |
| `CREDENTIALS.txt` | the passwords + alias |

This key is a **real, valid PKCS12 keystore** generated with OpenSSL (there is no JDK
on the authoring machine, and PKCS12 is Android's default keystore format). It signs
the release APK/AAB in the **Release** and **Play Store AAB** workflows automatically —
no secrets required.

## ⚠️ Security

A keystore + its passwords is a credential. It is committed here for zero-setup
signing, matching the reference project's convention — but that is only safe in a
**private** repository. If this repo is (or becomes) public:

1. Move the four values into repo **Secrets** (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`,
   `KEY_ALIAS`, `KEY_PASSWORD` — see `CREDENTIALS.txt`).
2. Delete this folder from git history.

The workflows already prefer secrets over this committed key when both exist.

## Keep it

Google Play requires every future update to use the **same** upload key. Back this
folder up somewhere safe — losing it means you can't ship updates without a Play
support key reset.
