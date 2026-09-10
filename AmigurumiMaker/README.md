# Amigurumi Maker

Android app for visualizing and modeling amigurumi crochet patterns in 2D and 3D.

## About

Amigurumi Maker parses crochet pattern syntax and renders the resulting geometry as both a 2D canvas and a 3D revolution mesh. It calculates stitch counts, increase/decrease ratios, surface metrics, and curvature to help crafters verify patterns before stitching.

The app includes preset patterns (sphere, hyperbolic, cone, cylinder, flat disk) that demonstrate different geometric properties, and a custom syntax editor for writing your own patterns.

## Quick Start

### Prerequisites

- Android Studio Ladybug (2024.2.1) or later
- JDK 11+
- Android SDK 36 (min SDK 26)

### Installation

```bash
git clone https://github.com/Kouhrak/amigurumiMaker.git
cd amigurumiMaker
./gradlew assembleDebug
```

### Run

Install on a connected device or emulator:

```bash
./gradlew installDebug
```

## Available Commands

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew test` | Run unit tests |
| `./gradlew connectedAndroidTest` | Run instrumented tests |

## Project Structure

```
AmigurumiMaker/
├── app/
│   └── src/main/java/com/example/amigurumimaker/
│       ├── domain/                 # Pattern parsing, geometry computation, models
│       │   ├── model/             # Data classes (Token, StitchType, MeshCell, etc.)
│       │   └── crochet/model/     # AST node definitions
│       ├── presentation/          # MVI architecture (ViewModel, Intents, Effects)
│       │   └── wallmodeler/       # Wall Modeler feature
│       │       ├── theme/         # CAD color scheme
│       │       └── ui/            # Atomic design components
│       │           ├── atoms/     # Basic UI elements (Button, Input, Badge, etc.)
│       │           ├── molecules/ # Composed components (Header, Stats, Panels)
│       │           ├── organisms/ # Complex UI sections (Viewport, Canvas, Tables)
│       │           └── pages/     # Full screen compositions
│       └── ui/theme/              # Material3 theme configuration
├── gradle/                        # Version catalog and wrapper
└── build.gradle.kts               # Root build config
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material3)
- **3D Rendering**: SceneView
- **Architecture**: MVI (Model-View-Intent)
- **Design System**: Atomic Design
- **Build System**: Gradle (Kotlin DSL) with version catalogs

## Configuration

No environment variables or external configuration required. The app runs entirely offline.

## Contributing

PRs welcome. Follow the existing atomic design structure and MVI pattern when adding features.

## License

No license specified.
