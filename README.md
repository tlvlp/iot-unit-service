# IoT Server Unit Service

## Service
Part of the [tlvlp IoT project](https://github.com/tlvlp/iot-project-summary)'s server side microservices.

This Dockerized SpringBoot-based service is responsible for all Microcontroller Unit (MCU) related operations on the server side:
- Process incoming status/error/inactivity messages: Register new units or update the status of existing ones
- Returns unit details on demand
- Add/remove scheduled events for units
- Persist unit related log entries and return them on demand
- Generate Unit/Module control messages
- Generate global status request messages

## Building and publishing JAR + Docker image
This project is using the using the [Palantir Docker Gradle plugin](https://github.com/palantir/gradle-docker).
All configuration can be found in the [Gradle build file](build.gradle) file 
and is recommended to be run with the docker/dockerTagsPush task.

## Deployment
- This service is currently designed as **stateless** and can have an arbitrary number of instances running per Docker Swarm Stack.
- For settings and deployment details see the project's [deployment repository](https://github.com/tlvlp/iot-server-deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://github.com/tlvlp/iot-server-deployment) via environment variables.


> API documentation has been temporarily removed!