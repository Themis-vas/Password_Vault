# PassGuard

PassGuard is an offline-first password vault for Android phones and tablets built entirely with Kotlin, Jetpack Compose and Material 3. The app stores all secrets locally, encrypts sensitive fields before persisting them, and keeps your vault locked behind a PIN and biometric authentication.

## Features

- **Master Lock** – 4–8 digit PIN with optional biometric unlock, automatic lock after timeout/background, and optional secure screen flag.
- **Credential Management** – Create, edit, favorite, reveal and copy credentials with automatic clipboard clearing.
- **Search & Filters** – Full text search across title, username, URL and notes with filters for favorites, categories and recent updates.
- **Password Generator** – Customisable generator with length, character classes, ambiguous-character avoidance and live strength meter.
- **Categories** – Built-in categories with the ability to add/remove custom ones and browse via chips.
- **Import / Export** – Encrypted `.pgvault` backups using AES-GCM with scrypt-derived keys.
- **Settings** – Dark theme default, in-app language override (English, Ελληνικά, 中文), auto-lock timeout, clipboard duration and secure screen toggle.
- **Tablet & Foldable Ready** – Responsive Compose UI that adapts with Material 3 window size classes.

## Architecture

- **Stack**: Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room, DataStore, Hilt, androidx.security.crypto.
- **Module**: Single `:app` module structured into `data`, `domain`, `ui`, and `core` packages following MVVM.
- **Storage**: Room database with entities `CredentialEntity` and `CategoryEntity`. Sensitive fields (`password`, `notes`) are encrypted in the app layer using `CryptoManager` (AES-GCM via Android Keystore).
- **DI**: Hilt modules provide the database, repositories, DataStore and use cases.
- **Clipboard**: Secure clipboard manager clears copied secrets after a configurable timeout.

## Security Notes

- No internet permission – the vault remains strictly on-device.
- `android:allowBackup="false"` and optional `FLAG_SECURE` screen protection.
- PIN stored as PBKDF2 hash with random salt inside Android Keystore-backed `EncryptedSharedPreferences`.
- Export files are encrypted with AES-GCM and scrypt-derived keys.
- Logs never contain sensitive data.

## Build & Run

1. Install Android Studio Ladybug (or newer) with the latest SDK (compile/target 34, minSdk 23).
2. Clone the repository and open it in Android Studio.
3. Sync Gradle and run on a device or emulator running API 23+.

### Gradle

```bash
./gradlew assembleDebug
```

### Tests

Run unit and instrumentation tests:

```bash
./gradlew testDebugUnitTest connectedDebugAndroidTest
```

## Screenshots

_dark theme preview_

*(Add screenshots captured from the running app in dark mode here)*

## License

© 2025 PassGuard Contributors. All rights reserved.
