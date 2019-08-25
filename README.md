# IoT Server Unit Service

## Service
Part of the tlvlp IoT project's server side microservices.

This Dockerized SpringBoot-based service is responsible for all Microcontroller Unit (MCU) related operations on the server side:
- Incoming status/error/inactivity messages: Register new units or update the status of existing ones
- Control units
- Schedule control events

## Deployment
- This service is currently designed as **stateless**can have an arbitrary number of instances running per Docker Swarm Stack.
- For settings and deployemnt details see the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment)

## Server-side API
Actual API endpoints are inherited from the project's [deployment repository](https://gitlab.com/tlvlp/iot.server.deployment) via environment variables.

### GET Units by example:

#### Related environment variables:
- ${UNIT_SERVICE_UNIT_LIST_BY_EXAMPLE_CONTROL}
- ${UNIT_SERVICE_UNIT_LIST_BY_EXAMPLE_CONTROL_URI}

#### Fields:
Takes a ScheduledEvent object in the RequestBody where all the empty fields are ignored
- **id**: String - the unique ID of the unit
- **name**: String - name of the unit
- **project**: String - related project name 
- **active**: Boolean - becomes false if the broker sends the unit's last will message after a pre-set period of inactivity
- **controlTopic**: String - unit-specific topic where the MCU listens for control messages
- **lastSeen**: LocalDateTime - the date and time of the last message received from the unit
- **modules**: Set<Module> - a set of Module objects that mostly represents hardware like relays and sensors

Get one event by sending a ScheduledEvent object:
```
{
    "id": "2019-08-24-9229F2B8-377F-440C-B251-23F866C927AC",
    "cronSchedule": "* * * * *",
    "targetUri": "http://mqtt-client:8100/messages",
    "info": "Posts an mqtt message every minute",
    "payload": {
        "topic": "/global/test",
        "payload": {
            "much": "payload",
            "even": "better"
        }
    }
}


```
## MCU-side API (MQTT) 

### TOPIC: /global/status
- fd