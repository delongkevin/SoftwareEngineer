# Infotainment System

A vehicle infotainment system built on Android OS with web application expandability. The system is designed as software-first with hardware abstraction interfaces that allow integration into real vehicle hardware.

## Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                    UI Layer                       │
│  ┌──────────────────┐  ┌─────────────────────┐  │
│  │  Android App     │  │  Web Companion      │  │
│  │  (MainActivity)  │  │  (React/TypeScript) │  │
│  └────────┬─────────┘  └──────────┬──────────┘  │
│           │                        │             │
│  ┌────────┴────────────────────────┴──────────┐  │
│  │           ViewModel / State                 │  │
│  └────────────────────┬───────────────────────┘  │
├───────────────────────┼──────────────────────────┤
│                Service Layer                     │
│  ┌────────────────┐  ┌────────────────────────┐  │
│  │ RadioTunerSvc  │  │ BluetoothAudioSvc     │  │
│  └───────┬────────┘  └───────────┬────────────┘  │
├──────────┼────────────────────────┼──────────────┤
│          Hardware Abstraction Layer (HAL)         │
│  ┌───────┴────────┐  ┌───────────┴────────────┐  │
│  │ RadioTunerHAL  │  │ BluetoothAudioHAL     │  │
│  │ (Interface)    │  │ (Interface)            │  │
│  └───────┬────────┘  └───────────┬────────────┘  │
│          │                        │              │
│  ┌───────┴────────┐  ┌───────────┴────────────┐  │
│  │ Simulated      │  │ Simulated             │  │
│  │ (Development)  │  │ (Development)         │  │
│  └────────────────┘  └────────────────────────┘  │
│          │                        │              │
│  ┌───────┴────────┐  ┌───────────┴────────────┐  │
│  │ Real Hardware  │  │ Real Hardware          │  │
│  │ (Vehicle)      │  │ (Vehicle BT Module)   │  │
│  └────────────────┘  └────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

## Radio App (Android)

### Prerequisites

- Android Studio (latest stable)
- JDK 17+
- Android SDK 34
- Kotlin 1.9+

### Setup

1. Open `radio-app/` directory in Android Studio
2. Sync Gradle project
3. Run on emulator or physical device (min SDK 28)

### Building

```bash
cd radio-app
./gradlew assembleDebug    # Debug build
./gradlew assembleRelease  # Release build
```

### Project Structure

```
radio-app/
├── app/src/main/
│   ├── java/com/infotainment/radio/
│   │   ├── RadioApplication.kt      # Application entry point
│   │   ├── hardware/                 # Hardware Abstraction Layer
│   │   │   ├── RadioTunerHAL.kt     # Radio tuner interface
│   │   │   ├── BluetoothAudioHAL.kt # Bluetooth audio interface
│   │   │   ├── AudioOutputHAL.kt    # Audio output interface
│   │   │   ├── SimulatedRadioTunerHAL.kt
│   │   │   ├── SimulatedBluetoothAudioHAL.kt
│   │   │   └── SimulatedAudioOutputHAL.kt
│   │   ├── model/                    # Data models
│   │   │   ├── RadioModels.kt       # Radio state/station models
│   │   │   ├── BluetoothModels.kt   # BT device/media models
│   │   │   └── AudioModels.kt       # Audio system models
│   │   ├── service/                  # Business logic services
│   │   │   ├── RadioTunerService.kt
│   │   │   └── BluetoothAudioService.kt
│   │   └── ui/                       # UI components
│   │       ├── MainActivity.kt
│   │       └── RadioViewModel.kt
│   └── res/
│       ├── layout/activity_main.xml
│       └── values/
├── build.gradle
└── settings.gradle
```

## Web Companion

### Prerequisites

- Node.js 18+
- npm or yarn

### Setup

```bash
cd web-companion
npm install
npm run dev      # Development server at http://localhost:3000
npm run build    # Production build to ../../build/infotainment-system/web-companion
```

## Integrating with Real Vehicle Hardware

The system uses a Hardware Abstraction Layer (HAL) pattern to separate software logic from hardware communication. To integrate with real vehicle hardware:

### 1. Radio Tuner Hardware

Implement the `RadioTunerHAL` interface with your specific hardware driver:

- **Serial/UART**: For standalone FM tuner modules (e.g., Si4703, TEA5767)
- **Android Radio HAL**: For vehicles using Android Automotive OS
- **CAN Bus**: For integrated vehicle radio systems
- **SDR**: For Software Defined Radio implementations

### 2. Bluetooth Module

Implement the `BluetoothAudioHAL` interface:

- **Android BT Stack**: Use Android's native `BluetoothA2dp` and `BluetoothAvrcpController`
- **Vehicle BT Module**: For vehicles with dedicated Bluetooth hardware separate from Android

### 3. Audio Output

Implement the `AudioOutputHAL` interface:

- **CAN Bus**: For controlling the vehicle's amplifier
- **I2C/SPI**: For DSP control
- **ALSA/HAL**: For Android audio routing

### Example: Replacing Simulated HAL

In `RadioViewModel.Factory`, replace:
```kotlin
val radioHAL = SimulatedRadioTunerHAL()
```
With your hardware implementation:
```kotlin
val radioHAL = VehicleRadioTunerHAL(serialPort = "/dev/ttyUSB0")
```

## Target Platform

- **OS**: Android 9.0+ (API 28+)
- **Orientation**: Landscape (vehicle head unit)
- **Display**: Optimized for 7-10 inch touchscreens
- **Hardware**: ARM/x86 (automotive SoCs)
