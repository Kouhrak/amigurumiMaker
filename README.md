# Amigurumi Maker

A cross-platform project for visualizing and modeling amigurumi crochet patterns in 2D and 3D.

## About

Amigurumi Maker parses crochet pattern syntax and renders the resulting geometry as both a 2D canvas and a 3D revolution mesh. It calculates stitch counts, increase/decrease ratios, surface metrics, and curvature to help crafters verify patterns before stitching.

The project exists as two implementations:

| | Android (Kotlin) | React Native (Expo) |
|---|---|---|
| **Path** | `AmigurumiMaker/` | `amigurumiMakerType/` |
| **Stack** | Kotlin, Jetpack Compose, SceneView | TypeScript, Expo SDK 57, React Three Fiber |
| **State** | MVI (ViewModel) | Zustand |
| **UI** | Atomic Design (atoms → molecules → organisms → pages) | Atomic Design (atoms → molecules → features) |
| **Tests** | JUnit + Coroutines Test | Jest + Testing Library |
| **Status** | Original implementation | Cross-platform port |

## Project Structure

```
amigurumiMaker/
├── AmigurumiMaker/          # Android app (Kotlin)
│   └── app/src/main/java/com/example/amigurumimaker/
│       ├── domain/           # Pattern parsing, geometry computation, models
│       ├── presentation/     # MVI architecture with Atomic Design UI
│       └── ui/theme/         # Material3 theme
│
├── amigurumiMakerType/      # React Native app (Expo)
│   └── src/
│       ├── app/              # Expo Router screens
│       ├── components/       # Atomic Design UI components
│       ├── domain/           # Pattern parsing, geometry (TypeScript ports)
│       ├── engine/           # Three.js canvas & mesh hooks
│       └── store/            # Zustand state management
│
└── README.md
```

## Quick Start

### Android

```bash
cd AmigurumiMaker
./gradlew installDebug
```

### React Native (Expo)

```bash
cd amigurumiMakerType
npm install
npx expo start
```

## Tech Stack

| Layer | Android | React Native |
|-------|---------|--------------|
| Language | Kotlin | TypeScript |
| UI | Jetpack Compose (Material3) | React Native + Expo |
| 3D | SceneView | React Three Fiber + Three.js |
| State | MVI (ViewModel) | Zustand |
| Routing | Composable Navigation | Expo Router |
| Testing | JUnit | Jest |

## Configuration

Both apps run entirely offline. No environment variables required.

## License

No license specified.
