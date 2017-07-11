package br.com.zup.eventsourcing.eventstore

import akka.actor.Status
import akka.util.Timeout
import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.AggregateRoot
import br.com.zup.eventsourcing.core.AggregateVersion
import br.com.zup.eventsourcing.core.Event
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.core.Repository
import br.com.zup.eventsourcing.core.config.jsonToObject
import br.com.zup.eventsourcing.core.config.objectToJson
import eventstore.EventNumber
import eventstore.ExpectedVersion
import eventstore.ReadStreamEventsCompleted
import eventstore.StreamNotFoundException
import eventstore.WriteResult
import eventstore.j.EsConnection
import eventstore.j.EventDataBuilder
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import java.nio.charset.Charset


abstract class EventStoreRepository<T : AggregateRoot> : Repository<T>() {
    private val LOG = LogManager.getLogger(this.javaClass)

    @Autowired lateinit var connection: EsConnection

    override fun save(aggregateRoot: T) {
        return save(aggregateRoot, MetaData())
    }

    override fun save(aggregateRoot: T, metaData: MetaData) {

        LOG.debug("receive save message with aggregate: $aggregateRoot and meta data: $metaData")
        try {
            saveEventsSynchronously(aggregateRoot, metaData)
        } catch (e: Exception) {
            LOG.error("error saving aggregate: $aggregateRoot and meta data: $metaData", e)
            throw e
        }
        LOG.debug("aggregate saved: $aggregateRoot and meta data: $metaData")
    }

    private fun saveEventsSynchronously(aggregate: T, metaData: MetaData) {
        if (aggregate.events.size > 0) {
            val timeout = Timeout(Duration.create(60, "seconds"))
            val items = aggregate.events.map { event ->
                EventDataBuilder(event.retrieveEventType().value)
                        .eventId(event.id.value)
                        .jsonData(event.retrieveJsonData().data)
                        .jsonMetadata(metaData.objectToJson())
                        .build()
            }
            val future: Future<WriteResult> = connection.writeEvents(
                    "${aggregate.javaClass.simpleName}-${aggregate.id.value}",
                    getExceptedVersion(aggregate.version.value),
                    items,
                    null,
                    false)
            val message = Await.result(future, timeout.duration())
            validateSaveMessageResult(aggregate, message)
        }
    }

    private fun getExceptedVersion(expectedVersion: Int): ExpectedVersion? {
        if (expectedVersion == -1)
            return ExpectedVersion.`NoStream$`.`MODULE$`
        else
            return ExpectedVersion.Exact(expectedVersion)
    }

    private fun validateSaveMessageResult(aggregate: T, message: Any?) {
        if (message == null) {
            val aggregateGot = get(aggregate.id)
            if (aggregateGot.version.value == aggregate.version.value + 1) {
                LOG.warn("is null, but we checked, the message is there, better look if server its ok: ")
            } else {
                LOG.warn("is null, and I dont know why so check if server its ok: ")
                throw InternalError()
            }
        } else if (message is WriteResult) {
            LOG.debug("on WriteResult: " + message.toString())
        } else if (message is Status.Failure) {
            LOG.error("on Status.Failure: " + message.toString())
            throw Exception(message.toString())
        } else {
            LOG.error("on Failure: " + message.toString())
            throw Exception(message.toString())
        }
    }

    override fun get(aggregateId: AggregateId): T {
        LOG.debug("receive get message with aggregateId: $aggregateId")
        val message = tryReadStreamEventsFromBeginning(aggregateId)
        LOG.debug("receive get message with aggregateId: $aggregateId and message $message")
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
            LOG.error("stream was empty: $readStreamEventsCompleted")
            throw NotFoundException()
        }
    }

    override fun getLastMetaData(aggregateId: AggregateId): MetaData {
        LOG.debug("receive get meta data message with aggregateId: $aggregateId")
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
            LOG.warn("there is no aggregate with aggregateId: $aggregateId", e)
            throw NotFoundException()
        } catch (e: Exception) {
            LOG.error("was not able to find and aggregate with aggregateId: $aggregateId", e)
            throw e
        }

    }

    private fun readStreamEventsFromBeginning(aggregateId: AggregateId): ReadStreamEventsCompleted {
        val timeout = Timeout(Duration.create(60, "seconds"))
        val future = connection.readStreamEventsForward(getGenericName() + "-" + aggregateId.value, EventNumber.Exact(0),
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

    class NotFoundException : Throwable()
    class MessageNotExceptedException : Throwable()
}


