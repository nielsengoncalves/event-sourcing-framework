package br.com.zup.eventsourcing

interface EventHandler {
    fun handle(aggregateId: AggregateId, event: Event, metaData: MetaData, version: AggregateVersion)
}