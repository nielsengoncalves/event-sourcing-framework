package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.Aggregate
import br.com.zup.eventsourcing.AggregateId
import br.com.zup.eventsourcing.Event
import br.com.zup.eventsourcing.util.NoArgsConstructor
import java.time.LocalDateTime

/**
 * Created by zacacj on 6/20/2017.
 */
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