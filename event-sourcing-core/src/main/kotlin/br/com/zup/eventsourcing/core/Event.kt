package  br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.config.objectToJson
import java.util.*

abstract class Event(val id: EventID = EventID()) {
    fun retrieveEventType(): EventType = EventType(this.javaClass.canonicalName)
    fun retrieveJsonData(): JsonData = JsonData(this.objectToJson())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || other !is Event) return false
        if (this::class != other::class) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

class EventID(val value: UUID = UUID.randomUUID()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null || other !is EventID) return false
        if (this::class != other::class) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
class JsonData(val data: String)
class EventType(val value: String)
