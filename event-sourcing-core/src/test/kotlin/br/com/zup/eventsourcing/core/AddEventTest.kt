package br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.domain.CreateEvent
import br.com.zup.eventsourcing.core.domain.ModifyEvent
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class AddEventTest {
    @Test
    fun createAddEvent() {
        val id = UUID.randomUUID()
        val myAddEvent = CreateEvent(AggregateId(id))

        assertEquals(CreateEvent::class.java.canonicalName, myAddEvent.retrieveEventType().value)
        //language=JSON
        assertThat(myAddEvent.retrieveJsonData().data, CoreMatchers.containsString(
                "\"aggregateId\":{\"value\":\"$id\"}"))
    }

    @Test
    fun createModifyEvent() {
        val myModifyEvent = ModifyEvent(ModifyEvent::class.java.simpleName, LocalDateTime.now())

        assertEquals(ModifyEvent::class.java.canonicalName, myModifyEvent.retrieveEventType().value)
        assertThat(myModifyEvent.retrieveJsonData().data, CoreMatchers.containsString("\"status\":\"${ModifyEvent::class.java.simpleName}\""))
    }
}