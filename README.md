# Smart City Simulator

![Build Status](https://github.com/vaniavaneva/Smart-City-Simulator/actions/workflows/main.yml/badge.svg)
![Coverage](https://img.shields.io/badge/coverage-80%25-brightgreen)
![Java](https://img.shields.io/badge/Java-23-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-blue?logo=apachemaven)
![License](https://img.shields.io/badge/license-MIT-green)

A **multithreaded Java simulation** of smart urban infrastructure — modelling traffic lights, air quality sensors, street lights, and bike stations in a concurrent, event-driven environment.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Sample Output](#sample-output)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Future Improvements](#future-improvements)

---

## Overview

Smart City Simulator models the real-time behaviour of city devices using **concurrent scheduling**, an **event-driven observer system**, and interchangeable **strategy-based algorithms**. Devices run independently on a shared thread pool, fire typed events, and are observed by a Dashboard, AlertSystem, and DataLogger — all without tight coupling.

---

## Features

| Category | Details |
|---|---|
| **Traffic Lights** | Adaptive (vehicle-count-based) and fixed-cycle green/yellow/red strategies |
| **Air Sensors** | PM2.5 monitoring with average and peak-detection analysis strategies |
| **Bike Stations** | Concurrent rent, return, and charger management with configurable capacity |
| **Street Lights** | Automatic on/off based on configurable dark-hour window |
| **Event System** | Typed `CityEventType` events dispatched to multiple listeners |
| **Dashboard** | Real-time console monitoring of all device state changes |
| **Alert System** | Threshold-based warnings for air quality and charger levels |
| **Data Logger** | Persistent event logging to `city_log.txt` |
| **Configuration** | All simulation parameters externalised in `config.properties` |
| **Concurrency** | `ScheduledExecutorService`-backed thread pool with graceful shutdown |

---

## Architecture

```
org.citysim/
├── city/
│   └── City.java                        # Central hub — device registry & event dispatcher
│
├── concurrent/
│   └── CityThreadPool.java              # ScheduledExecutorService wrapper with safe shutdown
│
├── devices/
│   ├── CityDevice.java                  # Abstract base for all devices
│   ├── TrafficLight.java                # RED / YELLOW / GREEN state machine
│   ├── TrafficLightState.java           # State enum
│   ├── AirSensor.java                   # PM2.5 sampling & strategy delegation
│   ├── LightSensor.java                 # Light level helper
│   ├── StreetLight.java                 # Day/night activation
│   └── BikeStation.java                 # Bikes, chargers, rent/return logic
│
├── engine/
│   └── SimulationEngine.java            # Wires everything together and drives the run
│
├── events/
│   └── CityEventType.java               # Typed event enum (STATUS, ALERT, …)
│
├── factory/
│   ├── DeviceFactory.java               # Creates devices by DeviceType
│   └── DeviceType.java                  # TRAFFIC_LIGHT, AIR_SENSOR, STREET_LIGHT, BIKE_STATION
│
├── observers/
│   ├── CityEventListener.java           # Listener interface
│   ├── Dashboard.java                   # Console status output
│   ├── AlertSystem.java                 # Threshold-based alerts
│   └── DataLogger.java                  # File-based event persistence
│
├── strategies/
│   ├── air/
│   │   ├── AirAnalysisStrategy.java     # Strategy interface
│   │   ├── AverageStrategy.java         # Rolling-average analysis
│   │   └── PeakDetectionStrategy.java   # Peak PM2.5 detection
│   └── traffic/
│       ├── TrafficStrategy.java         # Strategy interface
│       ├── AdaptiveTrafficStrategy.java # Vehicle-count-adaptive green timing
│       └── FixedCycleStrategy.java      # Static timing cycle
│
├── util/
│   ├── ConfigLoader.java                # Type-safe config.properties reader
│   ├── LoggerFactory.java               # Named logger factory
│   ├── ColorFormatter.java              # ANSI-coloured console output
│   └── MessageOnlyFormatter.java        # Strips JUL metadata from log lines
│
└── Main.java                            # Entry point
```

---

## Design Patterns

### 🔭 Observer Pattern
`City` maintains a list of `CityEventListener` implementations. When a device fires an event, `City.notifyListeners()` dispatches it to **Dashboard**, **AlertSystem**, and **DataLogger** simultaneously — with zero coupling between producers and consumers.

### Strategy Pattern
Behaviour is injected at runtime via interfaces:
- `TrafficStrategy` → `AdaptiveTrafficStrategy` | `FixedCycleStrategy`
- `AirAnalysisStrategy` → `AverageStrategy` | `PeakDetectionStrategy`

Swapping algorithms requires no changes to device classes.

### Factory Pattern
`DeviceFactory.create(DeviceType, id)` centralises device instantiation, keeping `SimulationEngine` clean of construction details.

---

## Getting Started

### Prerequisites

- **Java 23** (also compatible with Java 17+)
- **Maven 3.8+**

### Clone & Run

```bash
git clone https://github.com/vaniavaneva/Smart-City-Simulator.git
cd Smart-City-Simulator
mvn clean install
mvn exec:java -Dexec.mainClass="org.citysim.Main"
```

### Run from an IDE

1. Open the project as a Maven project.
2. Run `Main.java` directly.

---

## Configuration

All simulation parameters are externalised in `src/main/resources/config.properties`. No recompilation is needed to tune the simulation.

| Property | Default | Description |
|---|---|---|
| `simulation.duration` | `120000` | Total simulation time in milliseconds |
| `simulation.timeout` | `30` | Graceful shutdown timeout (seconds) |
| `thread.pool.size` | `4` | Number of threads in the shared pool |
| `traffic.fixed.green` | `10` | Fixed-cycle green duration (seconds) |
| `traffic.adapt.min` | `5` | Adaptive strategy minimum green duration |
| `traffic.adapt.max` | `10` | Adaptive strategy maximum green duration |
| `traffic.vehicle.threshold` | `2` | Vehicle count threshold for adaptive scaling |
| `traffic.scale.factor` | `4` | Scaling multiplier for adaptive green duration |
| `air.quality.threshold` | `55` | PM2.5 alert threshold |
| `air.pm.base` | `20` | Minimum simulated PM2.5 value |
| `air.pm.range` | `60` | Random range added to base PM2.5 |
| `air.max.history` | `100` | Maximum readings retained for analysis |
| `bike.rent.probability` | `0.4` | Probability a bike is rented per tick |
| `bike.min.capacity` | `2` | Minimum station capacity |
| `bike.max.capacity` | `10` | Maximum station capacity |
| `bike.min.chargers` | `2` | Minimum number of chargers at a station |
| `bike.max.chargers` | `4` | Maximum number of chargers at a station |
| `street.dark.start` | `20` | Hour at which street lights turn on (24h) |
| `street.dark.end` | `6` | Hour at which street lights turn off (24h) |

---

## Sample Output

```
[DASHBOARD] {TL-01} Light changed to: GREEN (next change in 10s)
[DASHBOARD] {TL-02} Light changed to: RED
[DASHBOARD] {SL-01} Hour: 21 | Light ON
[DASHBOARD] {AS-03} Air OK: 39.79
[DASHBOARD] {BS-01} Bike returned | Available: 7
[ALERT]     {AS-02} Poor air quality PM2.5=76.03
[ALERT]     {BS-02} Charger levels low (0)
```

Event logs are also persisted to `city_log.txt` in the project root.

---

## Testing

The project includes unit and integration tests using **JUnit 5** and **Mockito**.

```bash
mvn test
```

Test coverage includes:

| Module | Test Class |
|---|---|
| `City` | `CityTest` |
| `CityThreadPool` | `CityThreadPoolTest` |
| `AirSensor` | `AirSensorTest` |
| `BikeStation` | `BikeStationTest` |
| `CityDevice` | `CityDeviceTest` |
| `StreetLight` | `StreetLightTest` |
| `TrafficLight` | `TrafficLightTest` |
| `SimulationEngine` | `SimulationEngineTest` |
| `DeviceFactory` | `DeviceFactoryTest` |
| End-to-end | `IntegrationTest` |

---

## Future Improvements

- **REST API** — HTTP monitoring and control panel for live simulation state
- **JavaDoc** — Full API documentation generation via `mvn javadoc:javadoc`
- **CI pipeline** — GitHub Actions workflow with test reporting and coverage upload
- **Additional device types** — Water meters, waste collection sensors, noise monitors
- **Configurable city layout** — Define device topology via external configuration

---

> Built with Java 23 · Maven · JUnit 5 · Mockito
