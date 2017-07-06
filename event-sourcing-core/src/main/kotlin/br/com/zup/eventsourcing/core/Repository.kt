package br.com.zup.eventsourcing.core

import java.lang.reflect.ParameterizedType

abstract class Repository<T : AggregateRoot> {
    abstract fun save(aggregate: T)
    abstract fun save(aggregate: T, metaData: MetaData)
    abstract fun get(id: AggregateId): T

    protected fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }

    protected fun getGenericCanonicalName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).canonicalName
    }

}