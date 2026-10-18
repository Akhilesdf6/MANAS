#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
test -f "$ANDROID_HOME/platforms/android-36/android.jar" || {
  echo "Android SDK Platform 36 missing"; exit 1;
}
if [ -f "./gradlew" ] && [ -f "./gradle/wrapper/gradle-wrapper.jar" ]; then
  chmod +x ./gradlew
  ./gradlew assembleDebug
else
  gradle assembleDebug
fi
APK="app/build/outputs/apk/debug/app-debug.apk"
test -f "$APK"
echo "BUILD SUCCESSFUL: $(pwd)/$APK"
if [ -d "$HOME/storage/downloads" ]; then
  cp -f "$APK" "$HOME/storage/downloads/ZeroBat-v0.2.0-debug.apk"
  echo "Copied to Downloads/ZeroBat-v0.2.0-debug.apk"
fi
