# Event Store connector for  :ocean:RealWave:ocean:

This is a lib to work with [Event Store](http://docs.geteventstore.com/). It's meant to facilitate integration...we 
hope at least!!!

## Setups
first of all if you do want logging you should at a lot of places remove you **log4j** implementations autowired 
dependencies like **logback-classic**

```xml
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
```
        
talking about setup, your connection should be made Akka/Scala like, so on you resource add a `application.conf`
 
```
akka {
    loglevel = "INFO"
    stdout-loglevel = "INFO"
}
```
 
 and if you'd like to create environment setup suit yourself but remember to include de **application.conf** for 
 instance this `qa.conf`

```
 # This is a Sales Manager early stage setup
include "application"

eventstore {
 # IP & port of Event Store
 address {
  host = realwave-lab-eventstorecluster.azure.zup.com.br
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
  dns = realwave-lab-eventstorecluster.azure.zup.com.br
  external-gossip-port = 2114 }
}
```

...and the last
but not least to use your environment setup you need to start your application with 

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
 from `Event` and then you should implement your repository for the aggregate using the `EventStoreRepository`
 
```kotlin
class CreateEvent(val aggregateId: AggregateId) : Event()
```
 
The aggregate need to have the load method implemented so the repository can ask him to replay the events
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




