package  br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.config.objectToJson
import java.util.*

abstract class Event(val id: EventID = EventID()) {
    fun retrieveEventType(): EventType = EventType(this.javaClass.canonicalName)
    fun retrieveJsonData(): JsonData = JsonData(this.objectToJson())
}

class EventID(val value: UUID = UUID.randomUUID())
class JsonData(val data: String)
class EventType(val value: String)
