# MANAS v1.0 — Android source preview

A standalone Kotlin + Jetpack Compose meditation app starter, not a production-ready paid app.

## Implemented
- Dark blue/purple UI, Home, Journey, Player, Progress, Premium screens
- Seven free Malayalam lesson titles and TTS guidance prototype
- 90-day course listing; Day 8–90 locked
- Device-local completion tracking
- ₹199 / 12-month offer **display only** (no purchases or payment collection)

## Not implemented
- Google Play Billing / payment verification / 12-month entitlements
- Firebase auth, cloud sync, reminders, certificates
- Studio-quality recorded Malayalam audio and licensed logo artwork
- Signed production APK, Play Store release, accessibility and QA

## Build
Requires JDK 17, Android SDK 35, Gradle 8.9 or compatible Android Studio.
`gradle assembleDebug` (or generate a Gradle wrapper using Gradle 8.9).

For GitHub Actions, use the included workflow; it generates an unsigned-for-distribution debug APK as an artifact for testing only.

**Security:** Never commit service-account JSON, keystores, secrets, or personal data. Use a release signing key and test before public distribution.

**Branding:** The logo shown in earlier chat images is not embedded; provide an authorized square icon PNG and replace Android launcher assets before public release.
