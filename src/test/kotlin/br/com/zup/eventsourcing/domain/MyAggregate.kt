package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.Aggregate
import br.com.zup.eventsourcing.AggregateId
import br.com.zup.eventsourcing.AggregateVersion
import br.com.zup.eventsourcing.Event
import br.com.zup.eventsourcing.util.NoArgsConstructor

/**
 * Created by zacacj on 6/20/2017.
 */
@NoArgsConstructor
class MyAggregate() : Aggregate() {
    var status: String = "OPENED"

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
        if (event is ModifyEvent) apply(event)
    }

    private fun apply(event: CreateEvent) {
        this.id = event.aggregateId
    }

    private fun apply(event: ModifyEvent) {
        this.status = event.status
    }

    fun modify() {
        applyChange(ModifyEvent(ModifyEvent::class.java.simpleName))
    }
}