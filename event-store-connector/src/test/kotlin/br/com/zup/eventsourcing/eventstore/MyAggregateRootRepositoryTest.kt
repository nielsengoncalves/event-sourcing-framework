package br.com.zup.eventsourcing.eventstore

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.AggregateVersion
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.core.Repository
import br.com.zup.eventsourcing.eventstore.config.BaseTest
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRepository
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRepositoryWithoutLocking
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRoot
import eventstore.WrongExpectedVersionException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class MyAggregateRootRepositoryTest : BaseTest() {

    @Autowired
    lateinit var myAggregateRepository: MyAggregateRepository

    @Autowired
    lateinit var myAggregateRepositoryWithoutLocking: MyAggregateRepositoryWithoutLocking

    @Test
    fun saveMyAggregate_WithoutMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregateRepository.save(myAggregate)
        assertEquals(1, myAggregate.events.size)
    }

    @Test
    fun saveMyAggregate_WithMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregateRepository.save(myAggregate, metaData)
        assertEquals(1, myAggregate.events.size)
    }

    @Test
    fun saveMyAggregateCreateAndGet() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregateRepository.save(myAggregate, metaData)
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
        val metaData = MetaData()
        metaData.set("teste2", myAggregate)
        myAggregateRepository.save(myAggregate, metaData)

        val loadedFromEventStore = myAggregateRepository.get(myAggregate.id)

        assertEquals(myAggregate, loadedFromEventStore)
        assertEquals("ModifyEvent", loadedFromEventStore.status)
        assertEquals(2, loadedFromEventStore.modificationHistory.size)
        assertEquals(2, loadedFromEventStore.version.value)
        assertEquals(3, myAggregate.events.size)
        assertEquals(0, loadedFromEventStore.events.size)

    }

    @Test(expected = WrongExpectedVersionException::class)
    fun saveWithWrongExpectedVersion() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste2", myAggregate)
        myAggregate.version = AggregateVersion(3)
        myAggregateRepository.save(myAggregate, metaData)
        assertEquals(1, myAggregate.events.size)

    }


    @Test
    fun saveMyAggregateCreateNoModifyAndSaveAgain() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", "testeSaveAgain")
        myAggregateRepository.save(myAggregate, metaData)
        assertEquals(1, myAggregate.events.size)
        myAggregateRepository.save(myAggregate, metaData)
        assertEquals(1, myAggregate.events.size)
    }

    @Test
    fun getMetaData_whenOnlyOneEvent() {
        val aggregateID = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateID)
        val metaData = MetaData()
        metaData.set("teste", "teste")
        myAggregateRepository.save(myAggregate, metaData)
        assertEquals(1, myAggregate.events.size)
        val myRetrievedMetaData = myAggregateRepository.getLastMetaData(aggregateID)
        assertEquals(metaData["teste"], myRetrievedMetaData["teste"])
    }

    @Test
    fun getMetaData_whenTwoEvents() {
        val aggregateID = AggregateId(UUID.randomUUID())
        var myAggregate = MyAggregateRoot(aggregateID)
        val metaData = MetaData()
        metaData.set("teste", "teste")
        myAggregateRepository.save(myAggregate, metaData)
        myAggregate.clearEvents()
        assertEquals(0, myAggregate.events.size)
        var myRetrievedMetaData = myAggregateRepository.getLastMetaData(aggregateID)
        assertEquals(metaData["teste"], myRetrievedMetaData["teste"])

        myAggregate = myAggregateRepository.get(aggregateID)
        myAggregate.modify()
        metaData.set("teste", "modify")
        myAggregateRepository.save(myAggregate, metaData)
        myRetrievedMetaData = myAggregateRepository.getLastMetaData(aggregateID)
        assertEquals("modify", myRetrievedMetaData["teste"])

    }

    @Test(expected = Repository.NotFoundException::class)
    fun getStreamNotFoundException() {
        val aggregateID = AggregateId(UUID.randomUUID())
        myAggregateRepository.get(aggregateID)
    }

}