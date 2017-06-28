package br.com.zup.eventsourcing

import br.com.zup.eventsourcing.util.NoArgsConstructor
import org.apache.logging.log4j.LogManager

abstract class Aggregate {
    private val LOG = LogManager.getLogger(this.javaClass)

    lateinit var id: AggregateId
    var version: AggregateVersion = AggregateVersion(-1)
    lateinit var event: Event
    var events: MutableList<Event> = ArrayList()

    abstract fun load(events: List<Event>, aggregateVersion: AggregateVersion): Aggregate
    abstract fun applyEvent(event: Event)

    fun applyChange(event: Event) {
        LOG.debug("Applying event: {}", event)
        this.event = event
        events.add(event)

        applyEvent(event)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Aggregate

        if (!id.equals(other.id)) return false

        return true
    }
}
@NoArgsConstructor
data class AggregateVersion(val value: Int)
@NoArgsConstructor
open class AggregateId(val value: String) {
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

