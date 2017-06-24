package br.com.zup.eventsourcing

import br.com.zup.eventsourcing.domain.CreateEvent
import br.com.zup.eventsourcing.domain.ModifyEvent
import br.com.zup.eventsourcing.domain.MyAggregate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by zacacj on 6/20/2017.
 */
class MyAggregateTest {

    @Test
    fun createAggregate() {
        val id = java.util.UUID.randomUUID().toString()
        val myAggregate = MyAggregate(br.com.zup.eventsourcing.AggregateId(id))
        assertTrue(myAggregate.event is CreateEvent)
        assertEquals(id, myAggregate.id.value)
        assertEquals("OPENED", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

    @Test
    fun modifyAggregate() {
        val id = java.util.UUID.randomUUID().toString()
        val myAggregate = MyAggregate(br.com.zup.eventsourcing.AggregateId(id))
        myAggregate.modify()
        assertTrue(myAggregate.event is ModifyEvent)
        assertEquals(id, myAggregate.id.value)
        assertEquals("ModifyEvent", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

}