package br.com.zup.realwave.common.eventstore

import br.com.zup.realwave.common.eventstore.domain.CreateEvent
import br.com.zup.realwave.common.eventstore.domain.ModifyEvent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Created by zacacj on 6/20/2017.
 */
class AddEventTest {
    @Test
    fun createAddEvent() {
        val id = UUID.randomUUID().toString()
        val myAddEvent = CreateEvent(AggregateId(id))

        assertEquals(CreateEvent::class.java.canonicalName, myAddEvent.retrieveEventType().value)
        //language=JSON
        assertEquals("{\"aggregateId\":{\"value\":\"$id\"}}", myAddEvent.retrieveJsonData().data)
    }

    @Test
    fun createModifyEvent() {
        val myModifyEvent = ModifyEvent(ModifyEvent::class.java.simpleName)

        assertEquals(ModifyEvent::class.java.canonicalName, myModifyEvent.retrieveEventType().value)
        assertEquals("{\"status\":\"${ModifyEvent::class.java.simpleName}\"}", myModifyEvent.retrieveJsonData().data)
    }
}