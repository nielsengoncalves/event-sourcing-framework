package br.com.zup.eventsourcing.core

import java.lang.reflect.ParameterizedType

abstract class Repository<T : AggregateRoot> {
    abstract fun save(aggregateRoot: T, lock: Repository.OptimisticLock = OptimisticLock.ENABLED)
    abstract fun save(aggregateRoot: T, metaData: MetaData, lock: Repository.OptimisticLock = OptimisticLock.ENABLED)
    abstract fun get(aggregateId: AggregateId): T
    abstract fun getLastMetaData(aggregateId: AggregateId): MetaData

    protected fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }

    protected fun getGenericCanonicalName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).canonicalName
    }

    class NotFoundException : Throwable()

    enum class OptimisticLock {
        DISABLED,
        ENABLED
    }
}