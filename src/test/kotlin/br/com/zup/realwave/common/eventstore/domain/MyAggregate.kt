package br.com.zup.realwave.common.eventstore.domain

import br.com.zup.realwave.common.eventstore.Aggregate
import br.com.zup.realwave.common.eventstore.AggregateId
import br.com.zup.realwave.common.eventstore.AggregateVersion
import br.com.zup.realwave.common.eventstore.Event

/**
 * Created by zacacj on 6/20/2017.
 */
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