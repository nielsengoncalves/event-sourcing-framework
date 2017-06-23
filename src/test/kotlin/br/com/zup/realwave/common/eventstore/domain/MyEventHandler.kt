package br.com.zup.realwave.common.eventstore.domain

import br.com.zup.realwave.common.eventstore.AggregateId
import br.com.zup.realwave.common.eventstore.AggregateVersion
import br.com.zup.realwave.common.eventstore.Event
import br.com.zup.realwave.common.eventstore.EventHandler
import br.com.zup.realwave.common.eventstore.MetaData
import org.springframework.stereotype.Component

/**
 * Created by zacacj on 6/21/2017.
 */
@Component
open class MyEventHandler : EventHandler {

    open lateinit var aggregateId : AggregateId
    open lateinit var event : Event
    open lateinit var metaData : MetaData
    override fun handle(aggregateId: AggregateId,event: Event, metaData: MetaData, version: AggregateVersion) {
        this.aggregateId = aggregateId
        this.event = event
        this.metaData = metaData
    }
}