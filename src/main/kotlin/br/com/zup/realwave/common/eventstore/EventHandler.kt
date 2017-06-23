package br.com.zup.realwave.common.eventstore

interface EventHandler {
    fun handle(aggregateId: AggregateId,event: Event, metaData: MetaData, version: AggregateVersion)
}