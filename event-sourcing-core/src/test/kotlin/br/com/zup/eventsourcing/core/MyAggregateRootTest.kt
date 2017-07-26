package br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.domain.CreateEvent
import br.com.zup.eventsourcing.core.domain.ModifyEvent
import br.com.zup.eventsourcing.core.domain.MyAggregateRoot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class MyAggregateRootTest {

    @Test
    fun createAggregate() {
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        assertTrue(myAggregate.event is CreateEvent)
        assertEquals(1, myAggregate.events.count { it is CreateEvent })
        assertEquals(id.toString(), myAggregate.id.value)
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
        assertEquals(id.toString(), myAggregate.id.value)
        assertEquals("ModifyEvent", myAggregate.status)
        assertEquals(-1, myAggregate.version.value)
    }

    @Test
    fun clearEvents() {
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        myAggregate.modify()
        assertEquals(2, myAggregate.events.size)
        myAggregate.clearEvents()
        assertEquals(0, myAggregate.events.size)
    }

    @Test
    fun loadEvents() {
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val event = ModifyEvent("modify", LocalDateTime.now())
        myAggregate.load(listOf(event), AggregateVersion(2))
        assertEquals(1, myAggregate.events.size)
        assertEquals(2, myAggregate.version.value)
    }

    @Test
    fun equals_withSameObject() {
        val myAggregateRootFirst = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        val myAggregateRootSecond = myAggregateRootFirst
        assertTrue(myAggregateRootFirst.equals(myAggregateRootSecond))
        assertTrue(myAggregateRootSecond.equals(myAggregateRootFirst))
    }

    @Test
    fun equals_withDiffertClasses() {
        val myAggregateRoot = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        val myEvent = ModifyEvent("Modified", LocalDateTime.now())
        assertTrue(!myAggregateRoot.equals(myEvent))
        assertTrue(!myEvent.equals(myAggregateRoot))
    }

    @Test
    fun equals_withSameAggregateId() {
        val myAggregateID = AggregateId(UUID.randomUUID())
        val myAggregateRootFirst = MyAggregateRoot(myAggregateID)
        val myAggregateRootSecond = MyAggregateRoot(myAggregateID)
        assertTrue(myAggregateRootFirst.equals(myAggregateRootSecond))
        assertTrue(myAggregateRootSecond.equals(myAggregateRootFirst))
    }

    @Test
    fun equals_withDifferentAggregateId() {
        val myAggregateRootFirst = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        val myAggregateRootSecond = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        assertTrue(!myAggregateRootFirst.equals(myAggregateRootSecond))
        assertTrue(!myAggregateRootSecond.equals(myAggregateRootFirst))
    }

}