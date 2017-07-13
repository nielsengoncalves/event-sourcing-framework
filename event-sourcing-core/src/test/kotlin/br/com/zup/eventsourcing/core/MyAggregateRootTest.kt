package br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.domain.CreateEvent
import br.com.zup.eventsourcing.core.domain.ModifyEvent
import br.com.zup.eventsourcing.core.domain.MyAggregateRoot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MyAggregateRootTest {

    @Test
    fun createAggregate() {
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        assertTrue(myAggregate.event is CreateEvent)
        assertEquals(1, myAggregate.events.count { it is CreateEvent })
        assertEquals(id, myAggregate.id.value)
        assertEquals("OPENED", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

    @Test
    fun modifyAggregate() {
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        assertTrue(myAggregate.event is ModifyEvent)
        assertEquals(1, myAggregate.events.count { it is ModifyEvent })
        assertEquals(id, myAggregate.id.value)
        assertEquals("ModifyEvent", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

    @Test
    fun clearEvents() {
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        assertEquals(1, myAggregate.events.size)
        myAggregate.clearEvents()
        assertEquals(0, myAggregate.events.size)
    }

}