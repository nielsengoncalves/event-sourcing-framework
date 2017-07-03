package  br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.config.objectToJson

abstract class Event {
    fun retrieveEventType(): EventType = EventType(this.javaClass.canonicalName)
    fun retrieveJsonData(): JsonData = JsonData(this.objectToJson())
}

class JsonData(val data: String)
class EventType(val value: String)
