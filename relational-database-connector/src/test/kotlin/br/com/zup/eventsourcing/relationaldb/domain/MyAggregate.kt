package br.com.zup.eventsourcing.relationaldb.domain

import br.com.zup.eventsourcing.core.Aggregate
import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.Event
import br.com.zup.eventsourcing.core.util.NoArgsConstructor
import java.time.LocalDateTime

@NoArgsConstructor
class MyAggregate() : Aggregate() {
    var status: String = "OPENED"
    var modificationHistory: MutableList<LocalDateTime> = mutableListOf()

    constructor(aggregateId: AggregateId) : this() {
        applyChange(CreateEvent(aggregateId))
    }

    override fun applyEvent(event: Event) {
        if (event is CreateEvent) apply(event)
        if (event is ModifyEvent) apply(event)
    }

    private fun apply(event: CreateEvent) {
        this.id = event.aggregateId
    }

    private fun apply(event: ModifyEvent) {
        this.status = event.status
        this.modificationHistory.add(event.date)
    }

    fun modify() {
        applyChange(ModifyEvent(ModifyEvent::class.java.simpleName, LocalDateTime.now()))
    }
}