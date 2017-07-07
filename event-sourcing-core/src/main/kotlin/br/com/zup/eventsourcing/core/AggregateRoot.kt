package br.com.zup.eventsourcing.core

import org.apache.logging.log4j.LogManager
import java.util.*

abstract class AggregateRoot {
    private val LOG = LogManager.getLogger(this.javaClass)

    lateinit var id: AggregateId
    var version: AggregateVersion = AggregateVersion(-1)
    lateinit var event: Event
    var events: MutableList<Event> = ArrayList()

    abstract fun applyEvent(event: Event)

    fun load(events: List<Event>, aggregateVersion: AggregateVersion): AggregateRoot {
        for (event: Event in events) {
            applyChangeWithoutStackingEvents(event)
        }
        version = aggregateVersion
        return this
    }

    fun applyChange(event: Event) {
        LOG.debug("Applying event: {}", event)
        this.event = event
        this.events.add(event)

        applyEvent(event)
    }

    fun clearEvents() {
        events.clear()
    }

    private fun applyChangeWithoutStackingEvents(event: Event) {
        applyEvent(event)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AggregateRoot

        if (!id.equals(other.id)) return false

        return true
    }
}

data class AggregateVersion(val value: Int)

open class AggregateId(val value: UUID) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AggregateId

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

