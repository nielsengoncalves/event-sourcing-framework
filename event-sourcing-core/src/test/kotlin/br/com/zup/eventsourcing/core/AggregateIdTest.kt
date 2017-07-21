package br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.domain.ModifyEvent
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class AggregateIdTest {
    @Test
    fun equals_withSameObject() {
        val myAggregateIdFirst = AggregateId(UUID.randomUUID())
        val myAggregateIdSecond = myAggregateIdFirst
        assertTrue(myAggregateIdFirst.equals(myAggregateIdSecond))
        assertTrue(myAggregateIdSecond.equals(myAggregateIdFirst))
    }

    @Test
    fun equals_withDiffertClasses() {
        val myAggregateIdFirst = AggregateId(UUID.randomUUID())
        val myEvent = ModifyEvent("Modified", LocalDateTime.now())
        assertTrue(!myAggregateIdFirst.equals(myEvent))
        assertTrue(!myEvent.equals(myAggregateIdFirst))
    }

    @Test
    fun equals_withSameAggregateId() {
        val uuid = UUID.randomUUID()
        val myAggregateIdFirst = AggregateId(uuid)
        val myAggregateIdSecond = AggregateId(uuid)
        assertTrue(myAggregateIdFirst.equals(myAggregateIdSecond))
        assertTrue(myAggregateIdSecond.equals(myAggregateIdFirst))
    }

    @Test
    fun equals_withDifferentAggregateId() {
        val myAggregateIdFirst = AggregateId(UUID.randomUUID())
        val myAggregateIdSecond = AggregateId(UUID.randomUUID())
        assertTrue(!myAggregateIdFirst.equals(myAggregateIdSecond))
        assertTrue(!myAggregateIdSecond.equals(myAggregateIdFirst))
    }

    @Test
    fun hashCode_sameObject() {
        val myAggregateIdFirst = AggregateId(UUID.randomUUID())
        val myAggregateIdSecond = myAggregateIdFirst
        assertTrue(myAggregateIdFirst.hashCode().equals(myAggregateIdSecond.hashCode()))
        assertTrue(myAggregateIdSecond.hashCode().equals(myAggregateIdFirst.hashCode()))
    }

    @Test
    fun hashCode_withSameAggregateId() {
        val uuid = UUID.randomUUID()
        val myAggregateIdFirst = AggregateId(uuid)
        val myAggregateIdSecond = AggregateId(uuid)
        assertTrue(myAggregateIdFirst.hashCode().equals(myAggregateIdSecond.hashCode()))
        assertTrue(myAggregateIdSecond.hashCode().equals(myAggregateIdFirst.hashCode()))
    }

    @Test
    fun hashCode_withDifferentAggregateId() {
        val myAggregateIdFirst = AggregateId(UUID.randomUUID())
        val myAggregateIdSecond = AggregateId(UUID.randomUUID())
        assertTrue(!myAggregateIdFirst.hashCode().equals(myAggregateIdSecond.hashCode()))
        assertTrue(!myAggregateIdSecond.hashCode().equals(myAggregateIdFirst.hashCode()))
    }

}