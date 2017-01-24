# iot-hessen-amazon-echo

SourceCode for the talk at IoT Hessen 2017: Amazon Echo 

## Development

### Prerequisites

1. JDK 8

### Running 

1. Run `mvnw spring-boot:run`

### Building

1. Run `mvnw clean package`
1. See `target` folder for fat jar

## Alexa skill configuration

### Intent schema

```json
{
  "intents": [
    {
      "intent": "QueryInventory",
      "slots": [
        {
          "name": "ware",
          "type": "LIST_OF_WARES"
        }
      ]
    },   
    {
      "intent": "OrderWare",
      "slots": [
        {
          "name": "ware",
          "type": "LIST_OF_WARES"
        }
      ]
    },
    {
      "intent": "LocateWare",
      "slots": [
        {
          "name": "ware",
          "type": "LIST_OF_WARES"
        }
      ]
    },
    {
      "intent": "Quit"
    }
  ]
}
```

### Custom slot types

#### LIST_OF_WARES

```
Schrauben
Winkel
```


### Sample utterances

```
QueryInventory Wie viele {ware} haben wir noch
OrderWare Bestelle mir neue {ware}
LocateWare In welchem Regal befinden sich die {ware}
LocateWare Wo sind die {ware}
Quit Beenden
Quit Abbrechen
Quit Nein
```

## License

[MIT License](LICENSE)

## Maintainer

Moritz Kammerer ([@phxql](https://github.com/phxql))
