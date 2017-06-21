## Prerequisites

JDK 8, gradle

## Build

Download gradle wrapper:

```
gradle wrapper
```

Build distribution:

```
./gradlew clean build
```

Built jars will be placeв under *dist/* directory.

## Run

### Run server

You can run server using command bellow:
```
java -cp .:./dist/simplerpc-server-1.0-SNAPSHOT.jar:./dist/simplerpc-common-1.0-SNAPSHOT.jar:./dist/log4j-api-2.8.2.jar:./dist/log4j-core-2.8.2.jar com.martianov.simplerpc.server.Main 9019
```
In this case serer will be used server.properties and logger setting from project directory.

### Run example application

You can run example application using command below:

```
java -cp .:./dist/simplerpc-example-1.0-SNAPSHOT.jar:./dist/simplerpc-client-1.0-SNAPSHOT.jar:./dist/simplerpc-common-1.0-SNAPSHOT.jar:./dist/log4j-api-2.8.2.jar:./dist/log4j-core-2.8.2.jar com.martianov.simplerpc.example.Main localhost 9019 1 1 1 atomicLong getAndIncrement
```

Example application has following format: host port clientsCount workersCount callsCount serviceName methodName [serviceName methodName]

Example application connects to server using specified *host* and *port*. It creates *clientsCount* clients and *workersCount* worker for each client.
Single pair serviceName and methodName will be associated with every worker. If specified more than one such pair it will be associated using round-robin.
Each worker will make *callsCount* calls for associated service and method without arguments.
