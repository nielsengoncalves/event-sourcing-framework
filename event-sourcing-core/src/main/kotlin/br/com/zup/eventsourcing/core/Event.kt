package  br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.config.objectToJson
import java.util.*

abstract class Event(val id: EventID = EventID()) {
    fun retrieveEventType(): EventType = EventType(this.javaClass.canonicalName)
    fun retrieveJsonData(): JsonData = JsonData(this.objectToJson())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other === null || other !is Event -> false
        this::class != other::class -> false
        else -> id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

class EventID(val value: UUID = UUID.randomUUID()) {
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other === null || other !is EventID -> false
        this::class != other::class -> false
        else -> value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
}

class JsonData(val data: String)
class EventType(val value: String)
