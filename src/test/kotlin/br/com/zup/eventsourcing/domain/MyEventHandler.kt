package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.*
import org.springframework.stereotype.Component

/**
 * Created by zacacj on 6/21/2017.
 */
@Component
open class MyEventHandler : EventHandler {
    open lateinit var aggregateId : AggregateId
    open lateinit var event : Event
    open lateinit var metaData : MetaData
    override fun handle(aggregateId: AggregateId, event: Event, metaData: MetaData, version: AggregateVersion) {
        this.aggregateId = aggregateId
        this.event = event
        this.metaData = metaData
    }
}