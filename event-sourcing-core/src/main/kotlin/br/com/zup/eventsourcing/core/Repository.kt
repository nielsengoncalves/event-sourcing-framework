package br.com.zup.eventsourcing.core

import java.lang.reflect.ParameterizedType

abstract class Repository<T : AggregateRoot> {
    abstract fun save(aggregateRoot: T, lock: Repository.OptimisticLock = OptimisticLock.ENABLED)
    abstract fun save(aggregateRoot: T, metaData: MetaData, lock: Repository.OptimisticLock = OptimisticLock.ENABLED)
    abstract fun get(aggregateId: AggregateId): T
    abstract fun getLastMetaData(aggregateId: AggregateId): MetaData


    protected fun getGenericClass(): Class<T> =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>

    protected fun getGenericName(): String =
            getGenericClass().simpleName

    protected fun getGenericCanonicalName(): String =
            getGenericClass().canonicalName

    class NotFoundException : Throwable()

    enum class OptimisticLock {
        DISABLED,
        ENABLED
    }
}