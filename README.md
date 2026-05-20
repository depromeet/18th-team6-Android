This is a Kotlin Multiplatform project targeting Android and iOS.

* [/android/app](./android/app/src) contains the Android app entry point and Android-specific UI code.
  The main source set lives under [src/main](./android/app/src/main).

* [/android/feature/sample](./android/feature/sample/src) is a sample Android feature module.
  Use [src/main](./android/feature/sample/src/main) when you split Android-only UI and feature code out of the app shell.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for the code that will be shared between targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :android:app:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :android:app:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
