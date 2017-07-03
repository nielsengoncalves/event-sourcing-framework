package br.com.zup.eventsourcing.core

interface EventHandler {
    fun handle(aggregateId: AggregateId, event: Event, metaData: MetaData, version: AggregateVersion)
}