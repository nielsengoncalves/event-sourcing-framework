package br.com.zup.eventsourcing

/**
 * Created by luizs on 24/06/2017
 */
interface Repository<T : Aggregate> {
    fun save(aggregate: T)
    fun save(aggregate: T, metaData: MetaData)
    fun get(id: AggregateId): T
}