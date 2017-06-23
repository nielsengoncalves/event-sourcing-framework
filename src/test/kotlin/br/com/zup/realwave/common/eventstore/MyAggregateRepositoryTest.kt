package br.com.zup.realwave.common.eventstore

import br.com.zup.realwave.common.eventstore.config.BaseTest
import br.com.zup.realwave.common.eventstore.domain.MyAggregate
import br.com.zup.realwave.common.eventstore.domain.MyAggregateRepository
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
        val myAggregate = MyAggregate(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste", "teste")
        val result = myAggregateRepository.save(myAggregate, metaData)
        assertEquals(id, result)
    }

    @Test
    fun saveMyAggregateCreateAndGet() {
        val id = UUID.randomUUID().toString()
        val myAggregate = MyAggregate(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste2", myAggregate)
        myAggregateRepository.save(myAggregate, metaData)
        val myAggregateGot = myAggregateRepository.get(myAggregate.id)
        assertEquals(myAggregate, myAggregateGot)
    }

    @Test
    fun createAndModifyAggregate() {
        val id = UUID.randomUUID().toString()
        var myAggregate = MyAggregate(AggregateId(id))
        val metaData = MetaData()
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
        val myAggregate = MyAggregate(AggregateId(id))
        val metaData = MetaData()
        metaData.set("teste2", myAggregate)
        myAggregate.version = AggregateVersion(3)
        myAggregateRepository.save(myAggregate, metaData)
    }
}