package br.com.zup.eventsourcing.eventstore

import akka.actor.Status
import akka.util.Timeout
import br.com.zup.eventsourcing.core.Aggregate
import br.com.zup.eventsourcing.core.AggregateId
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
import java.lang.reflect.ParameterizedType
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


abstract class EventStoreRepository<T : Aggregate> : Repository<T> {
    private val LOG = LogManager.getLogger(this.javaClass)

    @Autowired lateinit var connection: EsConnection

    override fun save(aggregate: T) {
        return save(aggregate, MetaData())
    }

    override fun save(aggregate: T, metaData: MetaData) {

        LOG.debug("receive save message with aggregate: $aggregate and meta data: $metaData")
        try {
            saveEventsSynchronously(aggregate, metaData)
        } catch (e: Exception) {
            LOG.error("error saving aggregate: $aggregate and meta data: $metaData", e)
            throw e
        }
        LOG.debug("aggregate saved: $aggregate and meta data: $metaData")
    }


    override fun get(id: AggregateId): T {

        LOG.debug("receive get message with aggregateId: $id")
        try {
            val timeout = Timeout(Duration.create(60, "seconds"))
            val future = connection.readStreamEventsForward(getGenericName() + "-" + id.value, EventNumber.Exact(0),
                    4000,
                    true,
                    null)
            val message = Await.result(future, timeout.duration())
            if (message is ReadStreamEventsCompleted) {
                LOG.debug("got message with aggregateId: $id")
                return replayPurchaseOrderAggregate(message)
            } else {
                LOG.error("was not able to find and aggregate with aggregateId: $id")
                throw NotFoundException()
            }
        } catch (e: StreamNotFoundException) {
            LOG.warn("there is no aggregate with aggregateId: $id", e)
            throw NotFoundException()
        } catch (e: Exception) {
            LOG.error("was not able to find and aggregate with aggregateId: $id", e)
            throw e
        }
    }

    private fun replayPurchaseOrderAggregate(readStreamEventsCompleted: ReadStreamEventsCompleted): T {
        val events = ArrayList<Event>()
        var version = -1
        val aggregateClass: Class<*> = Class.forName(getGenericCanonicalName())
        for (event: eventstore.Event in readStreamEventsCompleted.events()) {
            val obj = event.record().data().data().value().decodeString(Charset.defaultCharset()).jsonToObject(Class.forName(event.data().eventType()))
            val purchaseOrderEvent = obj as Event
            version = event.number().value()
            events.add(purchaseOrderEvent)
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

    private fun saveEventsSynchronously(aggregate: T, metaData: MetaData) {
        if (aggregate.events.size > 0) {
            val timeout = Timeout(Duration.create(60, "seconds"))
            val items = aggregate.events.map { event ->
                EventDataBuilder(event.retrieveEventType().value)
                        .eventId(UUID.randomUUID())
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

    private fun validateSaveMessageResult(aggregate: T, message: Any?) {
        if (message == null) {
            val aggregateGot = get(aggregate.id)
            if (aggregateGot.version.value == aggregate.version.value + 1) {
                aggregate.clearEvents()
                LOG.warn("is null, but we checked, the message is there, better look if server its ok: ")
            } else {
                LOG.warn("is null, and I dont know why so check if server its ok: ")
                throw InternalError()
            }
        } else if (message is WriteResult) {
            aggregate.clearEvents()
            LOG.debug("on WriteResult: " + message.toString())
        } else if (message is Status.Failure) {
            LOG.error("on Status.Failure: " + message.toString())
            throw Exception(message.toString())
        } else {
            LOG.error("on Failure: " + message.toString())
            throw Exception(message.toString())
        }
    }

    private fun getExceptedVersion(expectedVersion: Int): ExpectedVersion? {
        if (expectedVersion == -1)
            return ExpectedVersion.`NoStream$`.`MODULE$`
        else
            return ExpectedVersion.Exact(expectedVersion)
    }

    private fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }

    private fun getGenericCanonicalName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).canonicalName
    }

    class NotFoundException : Throwable()
}


