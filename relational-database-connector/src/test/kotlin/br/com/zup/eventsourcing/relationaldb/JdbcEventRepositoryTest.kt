package br.com.zup.eventsourcing.relationaldb

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.relationaldb.config.RepositoryBaseTest
import br.com.zup.eventsourcing.relationaldb.domain.MyAggregateRoot
import br.com.zup.eventsourcing.relationaldb.domain.MyJdbcEventRepository
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class JdbcEventRepositoryTest : RepositoryBaseTest() {

    @Autowired lateinit var myJdbcEventRepository: MyJdbcEventRepository

    @Test
    fun saveAggregate_withoutMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        myJdbcEventRepository.save(myAggregate)
    }

    @Test
    fun saveAggregate_withMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", myAggregate)
        myAggregate.modify()
        myJdbcEventRepository.save(myAggregate, metaData)
    }

    @Test
    fun saveAndGetAggregate_withoutMetaData() {
        val id = UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        myJdbcEventRepository.save(myAggregate)

        val mySavedAggregate = myJdbcEventRepository.get(AggregateId(id))
        assertEquals(myAggregate, mySavedAggregate)
        assertEquals(myAggregate.status, mySavedAggregate.status)
    }

    @Test
    fun getMetaData_withOneEventOnly() {
        val aggregateId = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateId)
        val metaData = MetaData()
        metaData.set("teste", "teste1")
        myJdbcEventRepository.save(myAggregate, metaData)
        val newMetaData = myJdbcEventRepository.getLastMetaData(aggregateId)
        assertEquals("teste1", newMetaData["teste"])
    }

    @Test
    fun getMetaData_withTwoEventsOnly() {
        val aggregateId = AggregateId(UUID.randomUUID())
        val myAggregate = MyAggregateRoot(aggregateId)
        val metaData = MetaData()
        metaData.set("teste", "teste1")
        myJdbcEventRepository.save(myAggregate, metaData)
        val newMyAggregate = myJdbcEventRepository.get(aggregateId)
        metaData.set("teste", "teste2")
        newMyAggregate.modify()
        myJdbcEventRepository.save(newMyAggregate, metaData)
        val newMetaData = myJdbcEventRepository.getLastMetaData(aggregateId)
        assertEquals("teste2", newMetaData["teste"])
    }

}