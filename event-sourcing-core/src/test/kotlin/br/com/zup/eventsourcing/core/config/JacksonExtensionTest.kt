package br.com.zup.eventsourcing.core.config

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.domain.MyAggregateRoot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class JacksonExtensionTest {

    @Test
    fun jsonToObject() {
        val id = UUID.randomUUID()
        val json = "{\"id\": { \"value\": \"$id\"}}"
        val obj = json.jsonToObject(MyAggregateRoot::class.java)
        assertEquals(id.toString(), obj.id.value)
    }

    @Test
    fun objectToJson() {
        val uuid = UUID.randomUUID()
        val myAggregateRoot = MyAggregateRoot(AggregateId(uuid))
        val string = "\"id\":{\"value\":\"$uuid\"}"
        val json = myAggregateRoot.objectToJson()
        assertTrue(json.contains(string))
    }
}