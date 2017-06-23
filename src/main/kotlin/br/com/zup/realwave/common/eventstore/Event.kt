package  br.com.zup.realwave.common.eventstore

import br.com.zup.realwave.common.eventstore.config.objectToJson

abstract class Event {
    fun retrieveEventType(): EventType = EventType(this.javaClass.canonicalName)
    fun retrieveJsonData(): JsonData {
        return JsonData(this.objectToJson())
    }
}

class JsonData(val data: String)
class EventType(val value: String)
