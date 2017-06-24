# Event Sourcing Framework

This is a fremework meant to ease an [event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) implementation on the JVM.
Initially we provide event persistence and subscription with [Event Store](http://docs.geteventstore.com/) and [EventStore.JVM](https://github.com/EventStore/EventStore.JVM).

## EventStore.JVM setup

### Logs
If you do want logging you should use **log4j**. That means you need to exclude dependencies like **logback-classic** (if you ar using spring-boot-starter for example), and add the appropriate log implementation:
```xml
<dependencies>
...
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>VERSION GOES HERE</version>
</dependency>
...
</dependencies>
```

### Connecting to EventStore
The connection is configured using Akka/Scala config file. Create a `application.conf` file on your resources directory:

```
akka {
    loglevel = "INFO"
    stdout-loglevel = "INFO"
}
```
 
 You can create environment specific configuration files, just remember to include the main **application.conf** in it. For 
 instance, if you have a `qa.conf`, it should look like:

```
include "application"

eventstore {
 # IP & port of Event Store
 address {
  host = 127.0.0.1
  port = 1112
 }

 credentials {
  login = "admin"
  password = "changeit"
 }

 http {
  protocol = "http"
  port = 2114
  prefix = ""
 }

 cluster {
  dns = eventstore.cluster
  external-gossip-port = 2114 
 }
}
```
To use a specific environment you need to start your application with: 
```script
-Dconfig.resource=file:/qa.conf
```
or if it will be external to the application itself, then use this:

```script
-Dconfig.file=/home/zaca/production.conf
```

## But how to use the lib?

### As a Repository

To implement the command flow you should make your aggregate root inherit from `Aggregate`, your events should inherit
 from `Event` and then you should implement your repository for the aggregate using the `EventStoreRepository`.
 
```kotlin
class CreateEvent(val aggregateId: AggregateId) : Event()
```
 
The aggregate needs to have the load method implemented so the repository can ask him to replay the events.
 ```kotlin
class MyAggregate() : Aggregate() {
    constructor(aggregateId: AggregateId) : this() {

        applyChange(CreateEvent(aggregateId))
    }
    override fun load(events: List<Event>, aggregateVersion: AggregateVersion): Aggregate {
        for (event: Event in events) {
            applyChange(event)
        }
        version = aggregateVersion
        return this
    }
    private fun applyChange(event: Event) {
        this.event = event
        if (event is CreateEvent) apply(event)
    }
    private fun apply(event: CreateEvent) {
        this.id = event.aggregateId
    }
}  
```
 
```kotlin
class CreateEvent(val aggregateId: AggregateId) : Event()
```

The `EventStoreRepository` implements for now only the save and get operations
```kotlin
@Service
open class MyAggregateRepository : EventStoreRepository<MyAggregate>()
 ```
 
 ### As an Event Subscriber
 
 If your intend to implement CQRS you might think about subscribing to the events so you can create 
 your view, this lib is used to subscribe to a stream with name is your aggregate class simple name, to do so you 
 should create your subscriber inheriting from PersistentAggregateSubscriber, be aware we using Persistent 
 Subscribers check it out on [Event Store documentation](http://docs.geteventstore.com/)
 
 ```kotlin
@Service
open class MyAggregateSubscriber : PersistentAggregateSubscriber<MyAggregate>()
 ```
 
 And since someone need to handle the messages that will rise you should implement the 
 `EventHandle` interface, we use Spring to inject it so annotate you implementation
 
 To be better handle the subscriber does not start listening upon it's creation, you should call method `start()`. and 
 for customization you can pass the subscription group name as the constructor parameter, if not it will try to listen to 
 *YourAggregateSimpleClassName*SubscriptionGroup
 
 #### Event Store not covered features

Setup of things like enabling projection, creating projections, and creating subscription groups, on `run.sh` you can find
some curl examples used to serve for our testing purposes 


