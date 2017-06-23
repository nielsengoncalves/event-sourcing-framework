package br.com.zup.realwave.common.eventstore

import br.com.zup.realwave.common.eventstore.domain.CreateEvent
import br.com.zup.realwave.common.eventstore.domain.ModifyEvent
import br.com.zup.realwave.common.eventstore.domain.MyAggregate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

/**
 * Created by zacacj on 6/20/2017.
 */
class MyAggregateTest {

    @org.junit.Test
    fun createAggregate() {
        val id = java.util.UUID.randomUUID().toString()
        val myAggregate = MyAggregate(AggregateId(id))
        assertTrue(myAggregate.event is CreateEvent)
        assertEquals(id, myAggregate.id.value)
        assertEquals("OPENED", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

    @org.junit.Test
    fun modifyAggregate() {
        val id = java.util.UUID.randomUUID().toString()
        val myAggregate = MyAggregate(AggregateId(id))
        myAggregate.modify()
        assertTrue(myAggregate.event is ModifyEvent)
        assertEquals(id, myAggregate.id.value)
        assertEquals("ModifyEvent", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

}