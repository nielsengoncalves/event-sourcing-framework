package br.com.zup.eventsourcing.eventstore

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.AggregateVersion
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.eventstore.config.BaseTest
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRepositoryWithoutLocking
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRoot
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class MyAggregateRepositoryWithoutLockingTest : BaseTest() {

    @Autowired
    lateinit var myAggregateRepositoryWithoutLocking: MyAggregateRepositoryWithoutLocking

    @Test
    fun saveMyAggregate_WithoutMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregateRepositoryWithoutLocking.save(myAggregate)
        Assert.assertEquals(1, myAggregate.events.size)
    }

    @Test
    fun saveMyAggregate_WithMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregateRepositoryWithoutLocking.save(myAggregate, metaData)
        Assert.assertEquals(1, myAggregate.events.size)
    }

    @Test
    fun saveMyAggregateCreateAndGet() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregateRepositoryWithoutLocking.save(myAggregate, metaData)
        val myAggregateGot = myAggregateRepositoryWithoutLocking.get(myAggregate.id)
        Assert.assertEquals(myAggregate, myAggregateGot)
        Assert.assertEquals(1, myAggregate.events.size)
        Assert.assertEquals(0, myAggregateGot.events.size)
    }

    @Test
    fun createAndModifyAggregate() {
        val id = UUID.randomUUID()

        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        myAggregate.modify()
        val metaData = MetaData()
        metaData.set("teste2", myAggregate)
        myAggregateRepositoryWithoutLocking.save(myAggregate, metaData)

        val loadedFromEventStore = myAggregateRepositoryWithoutLocking.get(myAggregate.id)

        Assert.assertEquals(myAggregate, loadedFromEventStore)
        Assert.assertEquals("ModifyEvent", loadedFromEventStore.status)
        Assert.assertEquals(2, loadedFromEventStore.modificationHistory.size)
        Assert.assertEquals(2, loadedFromEventStore.version.value)
        Assert.assertEquals(3, myAggregate.events.size)
        Assert.assertEquals(0, loadedFromEventStore.events.size)

    }

    @Test
    fun saveWithWrongExpectedVersion() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste2", myAggregate)
        myAggregate.version = AggregateVersion(3)
        myAggregateRepositoryWithoutLocking.save(myAggregate, metaData)
        Assert.assertEquals(1, myAggregate.events.size)

    }


    @Test
    fun saveMyAggregateCreateNoModifyAndSaveAgain() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", "testeSaveAgain")
        myAggregateRepositoryWithoutLocking.save(myAggregate, metaData)
        Assert.assertEquals(1, myAggregate.events.size)
        myAggregateRepositoryWithoutLocking.save(myAggregate, metaData)
        Assert.assertEquals(1, myAggregate.events.size)
    }
}