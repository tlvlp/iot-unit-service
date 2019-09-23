# IoT Server Unit Service

## Service
Part of the tlvlp IoT project's server side microservices.

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
- For settings and deployment details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.


### POST Incoming messages:

Processes incoming MQTT messages and handles related actions, eg. status update, inactivation or error reporing

#### Related environment variables:
- ${UNIT_SERVICE_API_INCOMING_MESSAGE}

#### Input:
RequestBody:
- **topic**: String containing the targeted MQTT topic
- **payload**: Map<String, String> of the payload to be sent to the subscribers of the topic

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

#### Output:
- **type**: String with the parsed request type (status, inactive, error)
- **object**: Map<String, Object> differs according to type:
    - status: updated Unit object
    - inactive: updated Unit object
    - error: the generated UnitLog object

```
{
    "type": "status",
    "object": { ... }
}
```


### GET Units by example:

Returns a list of Units that match all values in the example.
Null values / empty fields will be ignored

#### Related environment variables:
- ${UNIT_SERVICE_API_LIST_UNITS_BY_EXAMPLE}

#### Input:
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
#### Output:

A list of Units or an empty list

```
[{...}, {...}, {...}]
```



### GET All Units:

Returns a list of Units that match all values in the example

#### Related environment variables:
- ${UNIT_SERVICE_API_LIST_ALL_UNIT}

#### Input:
Takes no arguments.

#### Output:

A list of Units or an empty list

```
[{...}, {...}, {...}]
```


### GET Global Unit status request message:

Composes and returns a message with the global status request MQTT topic

#### Related environment variables:
- ${UNIT_SERVICE_API_REQUEST_GLOBAL_STATUS}

#### Input:
Takes no arguments.

#### Output:
A message object with the global status request.
```
{
    "topic": "/global/statusrequesttopic",
    "payload": {}
}
```

### Get Module control message:

Composes and returns a message to a unit-specific MQTT control topic.
Once delivered it will execute the requested action in the module eg. switching a relay

#### Related environment variables:
- ${UNIT_SERVICE_API_RELAY_CONTROL}

#### Input:
RequestBody:
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
#### Output:

A message object with unit specific control topic.
```
{
    "topic": "/unit_specific_topic",
    "payload": {...}
}
```


### POST a scheduled event to Unit:

Adds a scheduled event to a Unit's event list

#### Related environment variables:
- ${UNIT_SERVICE_API_ADD_SCHEDULED_EVENT}

#### Input:
RequestParams (all fields are mandatory):
- **unitID**: String - ID of the containing Unit
- **eventID**: String - event ID

#### Output:
The updated unit

### DELETE Removed scheduled event from Unit:

Removes a scheduled event from a Unit's event list

#### Related environment variables:
- ${UNIT_SERVICE_API_DELETE_SCHEDULED_EVENT}

#### Input:
RequestParams (all fields are mandatory):
- **unitID**: String - ID of the containing Unit
- **eventID**: String - event ID

#### Output:
The updated unit



### Get Unit logs:

Returns the log entries for the given unit for a given time frame

#### Related environment variables:
- ${UNIT_SERVICE_API_GET_UNIT_LOGS}

#### Input:
RequestParams:
- **unitID**: String - ID of the containing Unit
- **timeFrom**: LocalDateTime - Lower time limit for the report (included)
- **timeTo**: LocalDateTime - Upper time limit for the report (excluded)

#### Output:

A list of UnitLog entries or an empty list

```
[{...}, {...}, {...}]
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