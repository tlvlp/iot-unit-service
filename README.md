# IoT Server Unit Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for all Microcontroller Unit (MCU) related operations on the server side:
- Incoming status/error/inactivity messages: Register new units or update the status of existing ones
- Control units
- Schedule unit control events

## Building and publishing JAR + Docker image
This project is using the using the [Palantir Docker Gradle plugin](https://github.com/palantir/gradle-docker).
All configuration can be found in the [Gradle build file](build.gradle) file 
and is recommended to be run with the docker/dockerTagsPush task.

## Deployment
- This service is currently designed as **stateless** and can have an arbitrary number of instances running per Docker Swarm Stack.
- For settings and deployment details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### POST Message (incoming messages):

Handles incoming messages

#### Related environment variables:
- ${UNIT_SERVICE_API_INCOMING_MESSAGE}
- ${UNIT_SERVICE_API_INCOMING_MESSAGE_URL}

#### Fields:
RequestBody (all fields are mandatory):
- **topic**: String containing the targeted MQTT topic
- **payload**: Map<String, String> of the payload to be sent to the subscribers of the topic
NOTE: Honoring the contents of the payload is the responsibility of the MCUs.

```
{
    "topic": "/global/status",
    "payload": 
        {
            "first": "value",
            "second": "value"
        }
}
```

### GET Units by example:

Returns a list of Units that match all values in the example

#### Related environment variables:
- ${UNIT_SERVICE_API_LIST_UNITS_BY_EXAMPLE}
- ${UNIT_SERVICE_API_LIST_UNITS_BY_EXAMPLE_URI}

#### Fields:
RequestBody (all the empty fields are ignored):
- **id**: String - the unique ID of the unit
- **name**: String - name of the unit
- **project**: String - related project name 
- **active**: Boolean - becomes false if the broker sends the unit's last will message after a pre-set period of inactivity
- **controlTopic**: String - unit-specific topic where the MCU listens for control messages
- **lastSeen**: LocalDateTime - the date and time of the last message received from the unit

```
{
    "id": "tlvlp.iot.BazsalikON-soil",
    "name": "soil",
    "project": "tlvlp.iot.BazsalikON",
    "active": true,
    "controlTopic": "/units/tlvlp.iot.BazsalikON-soil/control",
    "lastSeen": "2019-08-25T11:27:14.852"
}

```

### GET All Units:

Returns a list of Units that match all values in the example

#### Related environment variables:
- ${UNIT_SERVICE_API_LIST_ALL_UNIT}
- ${UNIT_SERVICE_API_LIST_ALL_UNIT_URI}

#### Fields:
Takes no arguments.


### POST Global status request to the Units:

Sends a message to the global status request MQTT topic to which all Units must respond by sending their status.

#### Related environment variables:
- ${UNIT_SERVICE_API_REQUEST_GLOBAL_STATUS}
- ${UNIT_SERVICE_API_REQUEST_GLOBAL_STATUS_URI}

#### Fields:
Takes no arguments.


### POST Module Control to Unit:

Module specific control endpoint.
Sends a message to a unit-specific MQTT control topic to manipulate the module in that given unit.

#### Related environment variables:
- ${UNIT_SERVICE_API_RELAY_CONTROL}
- ${UNIT_SERVICE_API_RELAY_CONTROL_URI}

#### Fields:
RequestBody (all fields are mandatory):
- **moduleID**: String - module ID
- **value**: Double - requested value/state of the Module
- **unitID**: String - ID of the containing Unit

```
{
    "moduleID": "relay|growlight",
    "value": 1,
    "unitID": "tlvlp.iot.BazsalikON-soil"
}

```


### POST Add scheduled event to Unit:

Adds a scheduled event to the list of events in a given Unit

#### Related environment variables:
- ${UNIT_SERVICE_API_ADD_SCHEDULED_EVENT}
- ${UNIT_SERVICE_API_ADD_SCHEDULED_EVENT_URL}

#### Fields:
RequestBody (all fields are mandatory):
- **unitID**: String - ID of the containing Unit
- **eventID**: String - event ID

```
{
    "eventID": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "unitID": "tlvlp.iot.BazsalikON-soil"
}

```


### DELETE Removed scheduled event from Unit:

Removes a scheduled event from the list of events in a given Unit

#### Related environment variables:
- ${UNIT_SERVICE_API_DELETE_SCHEDULED_EVENT}
- ${UNIT_SERVICE_API_DELETE_SCHEDULED_EVENT_URL}

#### Fields:
RequestBody (all fields are mandatory):
- **unitID**: String - ID of the containing Unit
- **eventID**: String - event ID

```
{
    "eventID": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "unitID": "tlvlp.iot.BazsalikON-soil"
}

```



## MCU-side API (MQTT) 
Actual MQTT topics are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.
All MQTT messages must be in JSON format.

### TOPIC: Global Status
- **Environment variable**: ${MCU_MQTT_TOPIC_GLOBAL_STATUS}
- **Description**: All MCU send their status messages to this topic. 
    Modules use a "module_reference|module_name" format where the module_reference is matched to its Module on the server side
    and the module_name is used to distinguish different instances of the module within a given Unit (eg. multiple relays) 
- **Posting here**: All MCUs
- **Subscribers**: Server side MQTT Client
- **Payload format**: Unit ID details plus all the modules with their current statuses
```
{
    "unitID": "tlvlp.iot.BazsalikON-soil", 
    "project": "tlvlp.iot.BazsalikON", 
    "name": "soil", 
    "relay|growlight": 0, 
    "gl5528|lightPercent": 85, 
    "somo|soilMoisturePercent": 80
}
```

### TOPIC: Global Status Request
- **Environment variable**: ${MCU_MQTT_TOPIC_GLOBAL_STATUS_REQUEST}
- **Description**: Requests a status update from all subscribed MCUs, who must respond by sending a status update to the global status topic
- **Posting here**: Server side MQTT Client
- **Subscribers**: All MCUs
- **Payload format**: The payload is ignored, An empty string is recommended.
```
""
```

### TOPIC: Global Error
- **Environment variable**: ${MCU_MQTT_TOPIC_GLOBAL_ERROR}
- **Description**: All MCU send their error messages to this topic. 
    For obvious reasons the connection related errors cannot be posted here.
- **Posting here**: All MCUs
- **Subscribers**: Server side MQTT Client
- **Payload format**: Unit ID details and an error message.
```
{
    "unitID": "tlvlp.iot.BazsalikON-soil", 
    "project": "tlvlp.iot.BazsalikON", 
    "name": "soil", 
    "error": "Error! Something mildly terrible has happened."
}
```

### TOPIC: Global Inactive
- **Environment variable**: ${MCU_MQTT_TOPIC_GLOBAL_ERROR}
- **Description**: All MCU set a "last will" when they connect to the MQTT broker containing their unitID details as the payload. 
    The broker sends the unit's payload after a pre-set time of inactivity. 
    The Unit whose details are sent to this topic will be flagged as inactive.
- **Posting here**: MQTT broker on behalf of the MCUs
- **Subscribers**: Server side MQTT Client
- **Payload format**: Unit ID details
```
{
    "unitID": "tlvlp.iot.BazsalikON-soil", 
    "project": "tlvlp.iot.BazsalikON", 
    "name": "soil"
}
```

### TOPIC: Unit control topics
- **Environment variable**: No related variable. Each topic is unit-specific.
- **Description**: All MCUs have their unit-specific topic generated in this pattern: "/units/**unitID**/control" 
    The Unit whose details are sent to this topic will be flagged as inactive.
- **Posting here**: Server side MQTT Client
- **Subscribers**: Each MCU to their own topic
- **Payload format**: Each  message should contain *only one* Module's details:
    - **moduleID** of a controllable module that is implemented in the Unit
    - **value** of the module.
```
{
    "relay|growlight": 1
}
```