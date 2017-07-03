package br.com.zup.eventsourcing.core

interface Repository<T : Aggregate> {
    fun save(aggregate: T)
    fun save(aggregate: T, metaData: MetaData)
    fun get(id: AggregateId): T
}