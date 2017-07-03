package br.com.zup.eventsourcing.eventstore.domain

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.AggregateVersion
import br.com.zup.eventsourcing.core.Event
import br.com.zup.eventsourcing.core.EventHandler
import br.com.zup.eventsourcing.core.MetaData
import org.springframework.stereotype.Component

@Component
open class MyEventHandler : EventHandler {
    open lateinit var aggregateId: AggregateId
    open lateinit var event: Event
    open lateinit var metaData: MetaData
    override fun handle(aggregateId: AggregateId, event: Event, metaData: MetaData, version: AggregateVersion) {
        this.aggregateId = aggregateId
        this.event = event
        this.metaData = metaData
    }
}