package br.com.zup.eventsourcing

import br.com.zup.eventsourcing.config.BaseTest
import br.com.zup.eventsourcing.domain.MyAggregate
import br.com.zup.eventsourcing.domain.MyAggregateRepository
import eventstore.WrongExpectedVersionException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/**
 * Created by zacacj on 6/20/2017.
 */
class MyAggregateRepositoryTest : BaseTest() {

    @Autowired
    lateinit var myAggregateRepository: MyAggregateRepository

    @Test
    fun saveMyAggregateCreate() {
        val id = UUID.randomUUID().toString()
        val myAggregate = MyAggregate(br.com.zup.eventsourcing.AggregateId(id))
        val metaData = br.com.zup.eventsourcing.MetaData()
        metaData.set("teste", "teste")
        val result = myAggregateRepository.save(myAggregate, metaData)
        assertEquals(id, result)
    }

    @Test
    fun saveMyAggregateCreateAndGet() {
        val id = UUID.randomUUID().toString()
        val myAggregate = MyAggregate(br.com.zup.eventsourcing.AggregateId(id))
        val metaData = br.com.zup.eventsourcing.MetaData()
        metaData.set("teste2", myAggregate)
        myAggregateRepository.save(myAggregate, metaData)
        val myAggregateGot = myAggregateRepository.get(myAggregate.id)
        assertEquals(myAggregate, myAggregateGot)
    }

    @Test
    fun createAndModifyAggregate() {
        val id = UUID.randomUUID().toString()
        var myAggregate = MyAggregate(br.com.zup.eventsourcing.AggregateId(id))
        val metaData = br.com.zup.eventsourcing.MetaData()
        metaData.set("teste2", myAggregate)
        myAggregateRepository.save(myAggregate, metaData)
        //right now needed, talk to maybe change
        myAggregate = myAggregateRepository.get(myAggregate.id)
        myAggregate.modify()
        myAggregateRepository.save(myAggregate, metaData)
        val myAggregateGot = myAggregateRepository.get(myAggregate.id)
        assertEquals(myAggregate, myAggregateGot)
        assertEquals("ModifyEvent", myAggregateGot.status)
    }

    @Test(expected = WrongExpectedVersionException::class)
    fun saveWithWrongExpectedVersion() {
        val id = UUID.randomUUID().toString()
        val myAggregate = MyAggregate(br.com.zup.eventsourcing.AggregateId(id))
        val metaData = br.com.zup.eventsourcing.MetaData()
        metaData.set("teste2", myAggregate)
        myAggregate.version = br.com.zup.eventsourcing.AggregateVersion(3)
        myAggregateRepository.save(myAggregate, metaData)
    }
}