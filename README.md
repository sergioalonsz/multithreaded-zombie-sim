# 🧟 Zombie Apocalypse Simulation

[![Java](https://img.shields.io/badge/Java-1.8+-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A22.svg)](https://maven.apache.org/)
[![GUI](https://img.shields.io/badge/UI-Swing-orange.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Concurrency](https://img.shields.io/badge/Core-Concurrency-green.svg)](https://docs.oracle.com/javase/tutorial/essential/concurrency/)

A professional, multi-threaded **Zombie Apocalypse Simulation** showcasing advanced concurrency, design patterns, and network communications in Java. Based on survival tracking, threads coordinate between safe shelters, dangerous risk zones, and traversing tunnels. 

This repository serves as a **Production-Grade Portfolio Piece**, refactored according to clean code principles (DRY, SOLID, KISS).

## 💡 The Problem it Solves
Managing highly concurrent systems is challenging. This project demonstrates mastery over advanced Java concurrency concepts:
- **`ReentrantLock` & `Condition`**: Safely manages shared resources (food, rooms) and complex state machines for entities (humans vs. zombies).
- **`CyclicBarrier`**: Group synchronization (e.g., waiting to gather 3 humans before deploying).
- **Socket Networking**: Client-Server architecture allowing remote UI interaction and real-time monitoring of simulation statistics safely without race conditions.

## 🏗 Project Architecture

```
.
├── src/main/java/com/apocalypse
│   ├── core
│   │   ├── Apocalypse.java          # Core simulation orchestrator
│   │   └── LoggerManager.java       # Thread-safe event logging
│   ├── models
│   │   ├── Human.java               # Autonomous human survivor thread
│   │   ├── Zombie.java              # Autonomous zombie threat thread
│   │   ├── Shelter.java             # Shared resource manager (Locks/Conditions)
│   │   ├── RiskZone.java            # Combat and gathering zone
│   │   └── Tunnel.java              # Tunnel flow control (CyclicBarriers)
│   ├── network
│   │   ├── Server.java              # Real-time state metrics provider
│   │   ├── Client.java              # Network consumer
│   │   └── Connection.java          # Remote polling thread
│   └── ui
│       ├── GraphicalInterface.java  # Main Local GUI Monitor
│       └── RemoteUI.java            # Remote GUI metrics and overrides
```

## ✨ Features
- 🧵 **Multi-Threaded AI Logic**: Autonomous units handle combat, resting, and scavenging asynchronously.
- 📡 **Remote Overrides**: Connect remotely via Sockets to pause/resume real-time operations.
- 📊 **Real-time Live Stats**: Java Swing tracks zone-specific population and zombie kill-counts instantly.
- 📜 **Safe Logging**: Event dispatch utilizes synchronization monitors guaranteeing atomic history retention.
- 🧹 **Clean Codebase**: Adheres directly to standard conventions with descriptive naming and decoupled packages.

## 🚀 Setup & Execution

**Prerequisites:** You need Java 1.8+ and Maven installed.

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/multithreaded-zombie-sim.git
   cd multithreaded-zombie-sim
   ```

2. **Build the Application:**
   ```bash
   mvn clean compile
   ```

3. **Run the Simluator:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.apocalypse.core.Apocalypse"
   ```

*(This will inherently open both the main localhost simulation window and the Remote polling UI for demonstration purposes)*.

## 🗺 Roadmap
- [ ] Migrate the Swing frontend to a modern framework (React/TypeScript or JavaFX).
- [ ] Add persistence (SQLite/Postgres) instead of flat-file logging to reload simulation states based on time-series.
- [ ] Incorporate Pathfinding algorithms (A*) and graphic coordinates over basic zone IDs.

## 👥 Authors
- **Sergio Alonso** - Developer
- **Alexis Conforme** - Developer
