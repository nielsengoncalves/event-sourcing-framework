package br.com.zup.realwave.common.eventstore

abstract class Aggregate {

    lateinit var id: AggregateId
    var version: AggregateVersion = AggregateVersion(-1)
    lateinit var event: Event

    abstract fun load(events: List<Event>, aggregateVersion: AggregateVersion): Aggregate

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Aggregate

        if (!id.equals(other.id)) return false

        return true
    }
}

data class AggregateVersion(val value: Int)
data class AggregateId(val value: String)

