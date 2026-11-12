# PlantsHome

An Android app for looking after house plants. Sign up, verify your email, pick the plants you like, then follow a daily care report and task list and earn badges as you go.

Built in Java with Firebase Authentication and the AndroidX Navigation component.

[![Android CI](https://github.com/Shahryartariq/Plant_Home_App/actions/workflows/android.yml/badge.svg)](https://github.com/Shahryartariq/Plant_Home_App/actions/workflows/android.yml)

## Features

- **Accounts:** sign up with name, email and password, email verification, log in, forgot password, change password, change email, and delete account (all through Firebase Auth).
- **Onboarding:** splash screen, Get Started, then a one-time plant choices screen.
- **Home:** a grid of plants; tap one for its care details.
- **Daily report and Tasks panel:** mark the day's care as done to earn a badge.
- **Badges:** start the next day from the badge screen.
- **Navigation drawer:** profile, feedback (opens your email app), rate, share and logout.

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Java 17 |
| UI | XML layouts, ViewBinding, Material Components, ConstraintLayout |
| Navigation | AndroidX Navigation (drawer + fragments) |
| Auth | Firebase Authentication (email and password) |
| Build | Gradle 8.14, Android Gradle Plugin 8.13, version catalog |
| SDK | minSdk 23, targetSdk / compileSdk 36 |
| Release | R8 code shrinking, resource shrinking, signing from environment variables |
| CI | GitHub Actions: debug and release builds, Android Lint, unit tests, debug APK artifact |

## Project structure

```
app/src/main/java/com/example/plantshome/
├── BaseActivity.java          edge-to-edge handling shared by every screen
├── SplashActivity.java        → GetStarted, or straight in if already signed in
├── LoginActivity.java / SignupActivity.java / VerifyEmailActivity.java
├── ChoicesActivity.java       one-time plant picker
├── MainActivity.java          toolbar + navigation drawer host
├── PlantDetailActivity.java / ProfileActivity.java / ResetPasswordActivity.java / ThanksActivity.java
├── ui/                        drawer screens: home, report, taskpanel, badge, feedback
└── util/                      validation, Firebase error messages, navigation and intent helpers
```

## Run it locally

1. Install [Android Studio](https://developer.android.com/studio) (it bundles JDK 17).
2. Clone the repo:
   ```bash
   git clone https://github.com/Shahryartariq/Plant_Home_App.git
   ```
3. Add your Firebase config. It is not in the repo on purpose.
   1. In the [Firebase console](https://console.firebase.google.com/), create a project, or open an existing one.
   2. Add an Android app with the package name `com.example.plantshome`.
   3. Download `google-services.json` and put it in the `app/` folder.
   4. Under **Authentication → Sign-in method**, turn on **Email/Password**.
4. Open the project in Android Studio and run it on a device or emulator (Android 6.0 or newer).

Command line:

```bash
./gradlew assembleDebug         # build the debug APK
./gradlew testDebugUnitTest     # unit tests
./gradlew lintDebug             # Android Lint
```

## Release build

The release build is minified with R8. Signing details come from environment variables, or from `~/.gradle/gradle.properties`, and are never committed:

```properties
PLANTSHOME_KEYSTORE_FILE=/path/to/release.jks
PLANTSHOME_KEYSTORE_PASSWORD=...
PLANTSHOME_KEY_ALIAS=...
PLANTSHOME_KEY_PASSWORD=...
```

```bash
./gradlew bundleRelease         # app bundle for Google Play
```

Without these variables, `assembleRelease` still builds an unsigned APK.

## CI

Every push to `main` and every pull request runs [`android.yml`](.github/workflows/android.yml). It builds debug and release, runs Lint and the unit tests, and uploads the debug APK. If the `GOOGLE_SERVICES_JSON` repository secret is set, CI uses it. Otherwise it builds against a placeholder config, which compiles the app but can't sign in.
