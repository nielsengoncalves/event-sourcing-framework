package br.com.zup.eventsourcing.core

import java.lang.reflect.ParameterizedType

abstract class Repository<T : AggregateRoot> {
    abstract fun save(aggregateRoot: T)
    abstract fun save(aggregateRoot: T, metaData: MetaData)
    abstract fun get(aggregateId: AggregateId): T

    protected fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }

    protected fun getGenericCanonicalName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).canonicalName
    }

}