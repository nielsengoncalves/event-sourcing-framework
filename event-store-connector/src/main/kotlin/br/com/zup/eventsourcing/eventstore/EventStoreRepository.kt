package br.com.zup.eventsourcing.eventstore

import akka.actor.ActorSystem
import akka.actor.Status
import akka.util.Timeout
import br.com.zup.eventsourcing.core.*
import br.com.zup.eventsourcing.core.config.jsonToObject
import br.com.zup.eventsourcing.core.config.objectToJson
import eventstore.EventNumber
import eventstore.ExpectedVersion
import eventstore.ReadStreamEventsCompleted
import eventstore.StreamNotFoundException
import eventstore.WriteResult
import eventstore.j.EsConnection
import eventstore.j.EsConnectionFactory
import eventstore.j.EventDataBuilder
import org.apache.logging.log4j.LogManager
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import java.nio.charset.Charset


abstract class EventStoreRepository<T : AggregateRoot> : Repository<T>() {
    val actorSystem = ActorSystem.create()!!
    val esConnection: EsConnection = EsConnectionFactory.create(actorSystem)
    private val log = LogManager.getLogger(this.javaClass)

    open fun getStreamName(aggregateId: AggregateId): String = "${getGenericName()}-${aggregateId.value}"

    override fun save(aggregateRoot: T, lock: OptimisticLock) = save(aggregateRoot, MetaData(), lock)

    override fun save(aggregateRoot: T, metaData: MetaData, lock: OptimisticLock) {
        log.debug("received save message with aggregate: $aggregateRoot and meta data: $metaData")
        try {
            saveEventsSynchronously(aggregateRoot, metaData, lock)
        } catch (e: Exception) {
            log.error("error saving aggregate: $aggregateRoot and meta data: $metaData", e)
            throw e
        }
        log.debug("aggregate saved: $aggregateRoot and meta data: $metaData")
    }

    private fun saveEventsSynchronously(aggregate: T, metaData: MetaData, lock: OptimisticLock) {
        if (aggregate.events.size > 0) {
            val timeout = Timeout(Duration.create(60, "seconds"))
            val items = aggregate.events.map { event ->
                EventDataBuilder(event.retrieveEventType().value)
                        .eventId(event.id.value)
                        .jsonData(event.retrieveJsonData().data)
                        .jsonMetadata(metaData.objectToJson())
                        .build()
            }
            val future: Future<WriteResult> = esConnection.writeEvents(
                    getStreamName(aggregate.id),
                    getExceptedVersion(aggregate.version.value, lock),
                    items,
                    null,
                    false)
            val message = Await.result(future, timeout.duration())
            validateSaveMessageResult(aggregate, message)
        }
    }

    private fun getExceptedVersion(expectedVersion: Int, lock: OptimisticLock): ExpectedVersion? {
        return when(lock) {
            OptimisticLock.ENABLED -> {
                if (expectedVersion == -1)
                    ExpectedVersion.`NoStream$`.`MODULE$`
                else
                    ExpectedVersion.Exact(expectedVersion)
            }
            OptimisticLock.DISABLED -> {
                ExpectedVersion.`Any$`.`MODULE$`
            }
        }
    }

    private fun validateSaveMessageResult(aggregate: T, message: Any?) {
        if (message == null) {
            val aggregateGot = get(aggregate.id)
            if (aggregateGot.version.value == aggregate.version.value + 1) {
                log.warn("is null, but we checked, the message is there, better look if server its ok: ")
            } else {
                log.warn("is null, and I dont know why so check if server its ok: ")
                throw InternalError()
            }
        } else if (message is WriteResult) {
            log.debug("on WriteResult: " + message.toString())
        } else if (message is Status.Failure) {
            log.error("on Status.Failure: " + message.toString())
            throw Exception(message.toString())
        } else {
            log.error("on Failure: " + message.toString())
            throw Exception(message.toString())
        }
    }

    override fun get(aggregateId: AggregateId): T {
        log.debug("receive get message with aggregateId: $aggregateId")
        val message = tryReadStreamEventsFromBeginning(aggregateId)
        log.debug("receive get message with aggregateId: $aggregateId and message $message")
        return replayAggregateRoot(message)

    }

    private fun replayAggregateRoot(readStreamEventsCompleted: ReadStreamEventsCompleted): T {
        val events = ArrayList<Event>()
        var version = -1
        val aggregateClass: Class<*> = Class.forName(getGenericCanonicalName())
        for (event: eventstore.Event in readStreamEventsCompleted.events()) {
            val obj = event.record().data().data().value().decodeString(Charset.defaultCharset()).jsonToObject(Class.forName(event.data().eventType()))
            val orderEvent = obj as Event
            version = event.number().value()
            events.add(orderEvent)
        }

        if (events.size > 0) {
            val aggregate = aggregateClass.newInstance()
            (aggregate as T).load(events, AggregateVersion(version))
            return aggregate
        } else {
            log.error("stream was empty: $readStreamEventsCompleted")
            throw NotFoundException()
        }
    }

    override fun getLastMetaData(aggregateId: AggregateId): MetaData {
        log.debug("receive get meta data message with aggregateId: $aggregateId")
        val message = tryReadStreamEventsFromBeginning(aggregateId)
        return getMetaDataFromLastEvent(message)

    }

    private fun getMetaDataFromLastEvent(readStreamEventsCompleted: ReadStreamEventsCompleted): MetaData {
        val event = readStreamEventsCompleted.events().last()
        return event.data().metadata().value().decodeString(Charset.defaultCharset()).jsonToObject(Class.forName
        (MetaData::class.qualifiedName)) as MetaData
    }

    private fun tryReadStreamEventsFromBeginning(aggregateId: AggregateId): ReadStreamEventsCompleted {
        try {
            return readStreamEventsFromBeginning(aggregateId)
        } catch (e: StreamNotFoundException) {
            log.warn("there is no aggregate with aggregateId: $aggregateId", e)
            throw NotFoundException()
        } catch (e: Exception) {
            log.error("was not able to find and aggregate with aggregateId: $aggregateId", e)
            throw e
        }
    }

    private fun readStreamEventsFromBeginning(aggregateId: AggregateId): ReadStreamEventsCompleted {
        val timeout = Timeout(Duration.create(60, "seconds"))
        val future = esConnection.readStreamEventsForward(getStreamName(aggregateId), EventNumber.Exact(0),
                4000,
                true,
                null)
        val message = Await.result(future, timeout.duration())
        return validatedReadStreamEventsCompleted(message)
    }

    private fun validatedReadStreamEventsCompleted(readStreamEventsCompleted: ReadStreamEventsCompleted?):
            ReadStreamEventsCompleted {
        if (readStreamEventsCompleted is ReadStreamEventsCompleted) {
            return readStreamEventsCompleted
        } else {
            throw MessageNotExceptedException()
        }
    }

    class MessageNotExceptedException : Throwable()
}


