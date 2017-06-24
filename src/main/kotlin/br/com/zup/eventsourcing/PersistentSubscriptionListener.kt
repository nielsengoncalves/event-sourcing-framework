package br.com.zup.eventsourcing

import akka.actor.AbstractActor
import akka.event.Logging
import br.com.zup.eventsourcing.config.jsonToObject
import eventstore.PersistentSubscriptionActor
import eventstore.ResolvedEvent
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.nio.charset.Charset

/**
 * Created by cleber on 6/7/17.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class PersistentSubscriptionListener(val eventHandler: EventHandler) :
        AbstractActor() {
    companion object {
        val BEAN_NAME = "persistentSubscriptionListener"
    }

    private val log = Logging.getLogger(context.system, this)

    override fun createReceive(): Receive {
        return receiveBuilder()
                .match(ResolvedEvent::class.java, {
                    run {
                        log.info("Received ResolvedEvent message: {}", it)
                        onReceive(it)
                    }
                })
                .matchAny { _ -> log.info("received unknown message") }
                .build()
    }

    fun onReceive(resolvedEvent: ResolvedEvent) {
        val obj = getEventData(resolvedEvent).jsonToObject(Class.forName(getEventDataClassName(resolvedEvent)))
        val aggregateId = br.com.zup.eventsourcing.AggregateId(getAggregateId(resolvedEvent))
        val metaData = getEventMetaData(resolvedEvent).jsonToObject(br.com.zup.eventsourcing.MetaData::class.java)
        val event = obj as Event
        eventHandler.handle(aggregateId, event, metaData, br.com.zup.eventsourcing.AggregateVersion(resolvedEvent.linkedEvent()
                                                                                                            .number().value()))
        sender.tell(PersistentSubscriptionActor.ManualAck(resolvedEvent.linkEvent().data().eventId()), self)
    }

    private fun  getAggregateId(resolvedEvent: ResolvedEvent): String {
        return extractAggregateId(resolvedEvent.linkedEvent().streamId().value())
    }

    private fun  extractAggregateId(value: String): String { return value.substringAfter("-")}


    private fun getEventMetaData(message: ResolvedEvent) = message.linkedEvent().data().metadata().value().decodeString(Charset
            .defaultCharset())

    private fun getEventDataClassName(message: ResolvedEvent) = message.data().eventType()

    private fun getEventData(message: ResolvedEvent) = message.linkedEvent().data().data().value().decodeString(Charset
            .defaultCharset())

}