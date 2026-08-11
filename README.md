# Parking Slot Monitor

[![Java CI with Maven in Linux](https://github.com/wissalbarhoumi03-hub/parking-slot-monitor/actions/workflows/maven.yml/badge.svg)](https://github.com/wissalbarhoumi03-hub/parking-slot-monitor/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/wissalbarhoumi03-hub/parking-slot-monitor/badge.svg?branch=master)](https://coveralls.io/github/wissalbarhoumi03-hub/parking-slot-monitor?branch=master)
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=wissalbarhoumi03-hub_parking-slot-monitor&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=wissalbarhoumi03-hub_parking-slot-monitor)

A desktop application to monitor parking slots. It allows the user to add parking slots,
mark them as occupied or free, and keep a history of the corresponding events. The project
is developed following Test-Driven Development, build automation, and continuous integration
practices, based on the book *Test-Driven Development, Build Automation, Continuous Integration
(with Java, Eclipse and friends)* by Lorenzo Bettini.

## Prerequisites

- Java 17 (the project is configured for Java 17)
- Docker running, required for the integration and end-to-end tests, which rely on Testcontainers to start MongoDB automatically

## Build and test

The whole build, including unit, integration, and end-to-end tests, is run with a single Maven
command from the repository root:

    mvn -f parking-slot-monitor/pom.xml clean verify

To also generate the JaCoCo code coverage report, enable the `jacoco` profile:

    mvn -f parking-slot-monitor/pom.xml clean verify -Pjacoco

## Run the application

The application needs a running MongoDB instance. For example, a local MongoDB can be started with:

    docker run -d -p 27017:27017 --name mongo mongo:5

Then the application can be started from the `parking-slot-monitor` directory:

    mvn exec:java "-Dexec.mainClass=com.example.parking.app.swing.ParkingSwingApp"

By default the application connects to MongoDB on `localhost:27017`. The connection can be
customized with the following command-line options: `--mongo-host`, `--mongo-port`,
`--db-name`, `--slot-collection`, `--event-collection`.
