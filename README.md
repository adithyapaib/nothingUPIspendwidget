# Spend Widget

Android home-screen widget that shows today's UPI spend by parsing incoming SMS alerts.

## Preview

![Spend Widget screenshot](./ss.jpg)

## Features

- 2x2 widget for quick spend visibility
- SMS-based auto-update for eligible transaction alerts
- Lightweight setup flow for SMS permissions
- Persists today's amount locally for widget refreshes

## Tech Stack

- Kotlin + Jetpack Compose (setup screen)
- App Widget (`AppWidgetProvider`) for the home-screen widget
- Android min SDK 24, target SDK 35
- Gradle Kotlin DSL

## Permissions

This app requests:

- `RECEIVE_SMS`
- `READ_SMS`
- `RECEIVE_BOOT_COMPLETED`
- `WAKE_LOCK`

These are used to read incoming SMS, update the widget amount, and restore behavior after reboot.

## Getting Started

### Prerequisites

- Android Studio (Koala or newer recommended)
- JDK 17 (for Gradle + Android builds)
- Android SDK installed through Android Studio

### Clone

```bash
git clone https://github.com/adithyapaib/spend-widget.git
cd spend-widget
```

### Open and Run

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Run `app` on a device/emulator.
4. Add the widget from the launcher widget picker.

### Build from CLI

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

## Tests

```bash
./gradlew testDebugUnitTest
```

On Windows PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest
```

## Project Structure

- `app/src/main/java/com/example/progressbar10/` - app and widget source files
- `app/src/main/res/` - widget layouts, drawables, and strings
- `app/src/main/AndroidManifest.xml` - app components and permissions

## Contributing

1. Fork the repository.
2. Create a branch: `feature/<short-name>`
3. Commit your changes.
4. Open a pull request.

## Security and Privacy Notes

- SMS data is sensitive. Review parsing/storage behavior before production release.
- Do not commit signing keys, `local.properties`, or generated build artifacts.

## License

This repository currently has no explicit license. Add a `LICENSE` file before sharing broadly if you want others to reuse the code.
