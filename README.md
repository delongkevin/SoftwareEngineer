# SoftwareEngineer

Software Engineering applications for mobile and web platforms, designed to be fully functional and deployable in real-world environments.

## Repository Structure

```
├── src/                          # Source code for all applications
│   └── infotainment-system/      # Vehicle infotainment system project
│       ├── radio-app/            # Android application (primary)
│       └── web-companion/        # Web interface (expandable)
├── build/                        # Build output artifacts
│   └── infotainment-system/      # Built infotainment artifacts
└── README.md
```

## Projects

### Infotainment System - Radio App

A fully functional vehicle infotainment radio application designed for Android OS deployment in real vehicles.

**Features:**
- AM/FM radio tuner with scan, seek, and preset functionality
- Bluetooth audio for phone connectivity and media playback
- Hardware Abstraction Layer (HAL) for real vehicle hardware integration
- Dark automotive UI theme (landscape orientation)
- Web companion interface for extended functionality

**Architecture:**
- **Hardware Layer** (`hardware/`): HAL interfaces that abstract physical hardware. Swap simulated implementations with real hardware drivers.
- **Service Layer** (`service/`): Business logic coordinating between UI and hardware
- **UI Layer** (`ui/`): Android Activities/ViewModels with reactive state management
- **Web Companion** (`web-companion/`): React/TypeScript web interface mirroring the Android app

See [`src/infotainment-system/README.md`](src/infotainment-system/README.md) for detailed setup and development instructions.
