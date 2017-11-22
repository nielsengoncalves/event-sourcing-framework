package br.com.zup.eventsourcing.eventstore

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.eventstore.config.BaseTest
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRoot
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

class OverrideStreamNameTest : BaseTest() {

    @Service
    open class MyAggregateRepositoryWithOverloadedStreamName : EventStoreRepository<MyAggregateRoot>(){
        override fun getStreamName(aggregateId: AggregateId): String = "Test-$aggregateId"
    }

    @Autowired
    lateinit var myAggregateRepository: MyAggregateRepositoryWithOverloadedStreamName

    @Test
    fun saveMyAggregate_WithoutMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregateRepository.save(myAggregate)
        assertEquals(1, myAggregate.events.size)
    }

    @Test
    fun saveMyAggregateCreateAndGet() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregateRepository.save(myAggregate)
        val myAggregateGot = myAggregateRepository.get(myAggregate.id)
        assertEquals(myAggregate, myAggregateGot)
        assertEquals(1, myAggregate.events.size)
        assertEquals(0, myAggregateGot.events.size)
    }

    @Test
    fun createAndModifyAggregate() {
        val id = UUID.randomUUID()

        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        myAggregate.modify()
        myAggregateRepository.save(myAggregate)

        val loadedFromEventStore = myAggregateRepository.get(myAggregate.id)

        assertEquals(myAggregate, loadedFromEventStore)
        assertEquals("ModifyEvent", loadedFromEventStore.status)
        assertEquals(2, loadedFromEventStore.modificationHistory.size)
        assertEquals(2, loadedFromEventStore.version.value)
        assertEquals(3, myAggregate.events.size)
        assertEquals(0, loadedFromEventStore.events.size)
    }
}