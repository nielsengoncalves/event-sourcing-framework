package br.com.zup.eventsourcing.relationaldb

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.core.Repository
import br.com.zup.eventsourcing.relationaldb.config.RepositoryBaseTest
import br.com.zup.eventsourcing.relationaldb.domain.MyAggregateRoot
import br.com.zup.eventsourcing.relationaldb.domain.RepositoryOptimisticLockEnabled
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class JdbcEventRepositoryTest : RepositoryBaseTest() {

    @Autowired lateinit var repositoryOptimisticLockEnabled: RepositoryOptimisticLockEnabled

    @Test
    fun saveAggregate_withoutMetaData_withLock() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate)
    }

    @Test
    fun saveAggregate_withoutMetaData_withoutLock() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate, Repository.OptimisticLock.DISABLED)
    }

    @Test
    fun saveAggregate_withMetaData_withLock() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate, metaData)
    }

    @Test
    fun saveAggregate_withMetaData_withoutLock() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate, metaData, Repository.OptimisticLock.DISABLED)
    }

    @Test
    fun saveAndGetAggregate_withoutMetaData_withLock() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate)

        val mySavedAggregate = repositoryOptimisticLockEnabled.get(AggregateId(id))
        assertEquals(myAggregate, mySavedAggregate)
        assertEquals(myAggregate.status, mySavedAggregate.status)
    }

    @Test
    fun saveAndGetAggregate_withoutMetaData_withoutLock() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate, Repository.OptimisticLock.DISABLED)

        val mySavedAggregate = repositoryOptimisticLockEnabled.get(AggregateId(id))
        assertEquals(myAggregate, mySavedAggregate)
        assertEquals(myAggregate.status, mySavedAggregate.status)
    }

    @Test
    fun getMetaData_withOneEventOnly_withLock() {
        val aggregateId = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateId)
        val metaData = MetaData()
        metaData.set("teste", "teste1")
        repositoryOptimisticLockEnabled.save(myAggregate, metaData)
        val newMetaData = repositoryOptimisticLockEnabled.getLastMetaData(aggregateId)
        assertEquals("teste1", newMetaData["teste"])
    }

    @Test
    fun getMetaData_withOneEventOnly_withoutLock() {
        val aggregateId = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateId)
        val metaData = MetaData()
        metaData.set("teste", "teste1")
        repositoryOptimisticLockEnabled.save(myAggregate, metaData, Repository.OptimisticLock.DISABLED)
        val newMetaData = repositoryOptimisticLockEnabled.getLastMetaData(aggregateId)
        assertEquals("teste1", newMetaData["teste"])
    }

    @Test
    fun getMetaData_withTwoEventsOnly_withLock() {
        val aggregateId = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateId)
        val metaData = MetaData()
        metaData.set("teste", "teste1")
        repositoryOptimisticLockEnabled.save(myAggregate, metaData)
        val newMyAggregate = repositoryOptimisticLockEnabled.get(aggregateId)
        metaData.set("teste", "teste2")
        newMyAggregate.modify()
        repositoryOptimisticLockEnabled.save(newMyAggregate, metaData)
        val newMetaData = repositoryOptimisticLockEnabled.getLastMetaData(aggregateId)
        assertEquals("teste2", newMetaData["teste"])
    }

    @Test
    fun getMetaData_withTwoEventsOnly_withoutLock() {
        val aggregateId = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateId)
        val metaData = MetaData()
        metaData.set("teste", "teste1")
        repositoryOptimisticLockEnabled.save(myAggregate, metaData, Repository.OptimisticLock.DISABLED)
        val newMyAggregate = repositoryOptimisticLockEnabled.get(aggregateId)
        metaData.set("teste", "teste2")
        newMyAggregate.modify()
        repositoryOptimisticLockEnabled.save(myAggregate, metaData, Repository.OptimisticLock.DISABLED)
        val newMetaData = repositoryOptimisticLockEnabled.getLastMetaData(aggregateId)
        assertEquals("teste2", newMetaData["teste"])
    }

    @Test(expected = Repository.NotFoundException::class)
    fun getNotFoundException_withLock() {
        val aggregateID = AggregateId(UUID.randomUUID())
        repositoryOptimisticLockEnabled.get(aggregateID)
    }

    @Test(expected = Repository.NotFoundException::class)
    fun getNotFoundException_withoutLock() {
        val aggregateID = AggregateId(UUID.randomUUID())
        repositoryOptimisticLockEnabled.get(aggregateID)
    }

}