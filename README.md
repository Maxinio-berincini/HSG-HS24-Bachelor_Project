# Helix
## Local-First Collaborative Formula Editor

<p align="center"> 
  <img src="FormulaEditor/src/main/resources/logo.png" alt="Project Logo"  height="200"/> 
</p>

![CI](https://github.com/Maxinio-berincini/HSG-HS24-Bachelor_Project/actions/workflows/test.yml/badge.svg)

Helix Formula Editor is a **local-first** collaborative Excel-style formula editor. 
It uses CRDT-based merging rules, allowing users to simultaneously edit, merge, and synchronize workbooks.

---

## Table of Contents
- [Repository Structure](#repository-structure)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Application](#running-the-application)
    - [Testing](#testing)
---


## Repository Structure

The GitHub repository is organized into three main directories:

- **`FormulaEditor/`**  
  Contains the source code for the local-first collaborative formula editor.
    - **CRDT Logic** (`crdt/`)
    - **Data Models** (`model/`)
    - **Network Layer** (`network/`)
    - **Formula Parser** (`parser/`)
      - **AST** (`parser/ast/`)
    - **JavaFX UI** (`ui/`)
    - **Tests** in the `test` directory

- **`docker/`**  
  Docker files for the WebSocket signaling server. This server helps peers discover each other and synchronize changes over the network.

- **`docs/`**  
  Contains PDF files of the final and interim presentations as well as handwritten test scenarios. These scenarios were later converted into the unit tests you see in the `test` folder.

---

## Getting Started

### Prerequisites

- **Java 19** or higher
- **Maven 3.8+** 

**Note for macOS Users**  
For certain macOS environments, **Java 21 and JavaFX 21 or higher** may be required to properly run Helix. 
### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Maxinio-berincini/HSG-HS24-Bachelor_Project.git
   cd HSG-HS24-Bachelor_Project/FormulaEditor
   ```
2. **Build the Project**
   ```bash
   mvn clean install
   ```
   This command will download dependencies, compile the source code, and run tests.

### Running the Application

1. **Via Maven**  
   With the [JavaFX Maven plugin](https://github.com/openjfx/javafx-maven-plugin) configured in the `pom.xml`:
   ```bash
   mvn javafx:run
   ```
   or:
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="org.example.formulaeditor.ui.MainUI"
   ```
2. **Multiple Instances**  
     - By default, `MainUI` runs a single instance. This can be changed to run multiple instances for simulating collaboration. 

3. **Network Configuration**
    - By default, `NetworkService` connects to a WebSocket server hosted at `wss://helix.berinet.ch`.
    - This can be changed at runtime within the UI.

### Testing

Run the unit tests with:
```bash
mvn test
```
