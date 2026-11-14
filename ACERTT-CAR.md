# Acertt Car Launcher

This Android home app is based on [Femto Car Launcher](https://github.com/seijikohara/femto-car-launcher/tree/a36bdf40404547ffc67a132accfcc901671f0daf) by Seiji Kohara. The derivative keeps the [Apache 2.0 license](LICENSE), upstream credits, and third-party attributions.

The Acertt Car edition adds its own application ID and a prototype monogram. On first launch, it defaults to a dark theme with red accents, left-hand-drive layout, metric units, and a 24-hour clock. The dashboard, app drawer, and common settings have Brazilian Portuguese labels when the device is set to Portuguese (Brazil). Advanced settings and messages can fall back to English.

## Build and install

Open this folder in Android Studio. Set the Gradle JDK to JDK 25 and use the SDK Manager to install Android SDK Platform 37.0 and the Build Tools, NDK and CMake versions pinned in [the version catalog](gradle/libs.versions.toml). Gradle downloads the Node.js and pnpm versions used to bundle the map.

Run `./gradlew assembleStableDebug` on Linux or macOS, or `.\gradlew.bat assembleStableDebug` in PowerShell. The debug APK appears under `app/build/outputs/apk/stable/debug/`.

Install the APK on an Android 10 or newer device and open it from the app list. On a compatible head unit, select **Acertt Car Launcher** under the Android default Home app settings. Some head units lock this setting and offer a startup-app option instead. The app can also launch directly from its icon.

Grant location access for the live map and notification access for media controls, if you want those features. Choose apps already installed on the head unit from the app drawer. Hold a dock item to customize the shortcuts.

The debug build is for device testing. The Acertt Car update feed must be configured explicitly for versioned builds to offer in-app updates.
