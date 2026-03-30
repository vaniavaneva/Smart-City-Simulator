![Build Status](https://github.com/vaniavaneva/Smart-City-Simulator/actions/workflows/main.yml/badge.svg)
![Coverage](https://img.shields.io/badge/coverage-80%25-brightgreen)
# Smart City Simulator

## Overview
Smart City Simulator is a multithreaded Java application (built with **Maven**) that models urban infrastructure devices such as traffic lights, air sensors, street lights, and bike stations.
The system uses **event-driven architecture**, **strategy pattern**, and **concurrent execution** to simulate real-time city behavior.

## Features
- Traffic lights with adaptive and fixed strategies
- Air quality monitoring with analysis strategies
- Bike stations with renting, returning, and charging logic
- Street lights based on day/night cycle
- Observer system (Dashboard, Alerts, Data logging)
- Multithreading via ScheduledExecutorService

## Architecture
java/ <br>
├── org.citysim/ <br>
│ ├─── city/ <br>
│ │ └── City.java <br>
│ │  <br>
│ ├───concurrent/ <br>
│ │ └── CityThreadPool.java <br>
│ │ <br>
│ ├───devices/ <br>
│ │ ├── AirSensor.java <br>
│ │ ├── BikeStation.java <br>
│ │ ├── CityDevice.java <br>
│ │ ├── LightSensor.java <br>
│ │ ├── StreetLight.java <br>
│ │ ├── TrafficLight.java <br>
│ │ └── TrafficLightState.java <br>
│ │ <br>
│ ├───engine/ <br>
│ │ └── SimulationEngine.java <br>
│ <br>
│ ├───events/ <br>
│ │ └── CityEventType.java <br>
│ <br>
│ ├───factory/ <br>
│ │ ├── DeviceFactory.java <br>
│ │ └── DeviceType.java <br>
│ <br>
│ ├───observers/ <br>
│ │ ├── AlertSystem.java <br>
│ │ ├── CityEventListener.java <br>
│ │ ├── Dashboard.java <br>
│ │ └── DataLogger.java <br>
│ <br>
│ ├───strategies/ <br>
│ │ ├───air/ <br>
│ │ │ ├── AirAnalysisStrategy.java <br>
│ │ │ ├── AverageStrategy.java <br>
│ │ │ └── PeakDetectionStrategy.java <br>
│ │ │ <br>
│ │ ├───traffic/ <br>
│ │ │ ├── AdaptiveTrafficStrategy.java <br>
│ │ │ ├── FixedCycleStrategy.java <br>
│ │ │ └── TrafficStrategy.java <br>
│ <br>
│ ├───util/ <br>
│ │ ├── ColorFormatter.java <br>
│ │ ├── ConfigLoader.java <br>
│ │ ├── LoggerFactory.java <br>
│ │ └── MessageOnlyFormatter.java<br> 
│ <br>
│ ├───Main.java <br>
│ <br>
├───resources/ <br>
│ └── config.properties <br>
 
### Design Patterns used
- **Observer Pattern** – event system
- **Strategy Pattern** – dynamic behavior
- **Factory Pattern** – device creation

## Running the Project
### Prerequisites
- Java 17+
- Maven 3.8+

### Steps 
Run with Maven:
```bash
git clone https://github.com/vaniavaneva/Smart-City-Simulator.git
cd Smart-City-Simulator
mvn clean install
mvn exec:java -Dexec.mainClass="org.citysim.Main"
```

Or if using an IDE:
```bash
Open the project
Run Main.java
```

## Configuration

The simulation behavior can be customized via:
`resources/config.properties`

Examples of configurable parameters: 
 
- Simulation duration 
- Light hours 
- Air quality threshold 
 
## CI pipeline (GitHub Actions)
//tba

## JavaDoc
//tba

## Example Scenario / Demo

A typical simulation run includes:

1. Traffic lights switching between red, green and yellow 
2. Air sensors collecting pollution data with strategies 
3. Bike stations handling rentals, returns and charging concurrently 
4. Street lights activating automatically based on time of day 
5. Events being dispatched to: 
	- Dashboard (visual monitoring) 
	- AlertSystem (threshold warnings) 
	- DataLogger (persistent logs) 

Example console output:
```bash
[DASHBOARD] {TL-01} Light changed to: GREEN (next change in 10s)
[DASHBOARD] {SL-02} Hour: 1 | Light ON
[DASHBOARD] {AS-04} Air OK: 39,79
[DASHBOARD] {BS-01} Bike returned | Available: 7
[ALERT] {AS-02} Poor air quality PM2.5=76,03
[ALERT] {BS-02} Charger levels low (0)
```

## Future Improvements
### Rest API - simulation monitoring panel
