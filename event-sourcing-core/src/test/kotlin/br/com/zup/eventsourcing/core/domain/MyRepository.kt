package br.com.zup.eventsourcing.core.domain

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.core.Repository


class MyRepository : Repository<MyAggregateRoot>() {
    override fun save(aggregateRoot: MyAggregateRoot) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(aggregateRoot: MyAggregateRoot, metaData: MetaData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(aggregateId: AggregateId): MyAggregateRoot {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastMetaData(aggregateId: AggregateId): MetaData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getGenericNameTest(): String {
        return getGenericName()
    }

    fun getGenericCanonicalNameTest(): String {
        return getGenericCanonicalName()
    }

    fun throwNotFoundExceptionTest(): String {
        throw NotFoundException()
    }
}