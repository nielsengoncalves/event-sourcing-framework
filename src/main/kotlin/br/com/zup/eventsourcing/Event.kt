package  br.com.zup.eventsourcing

import br.com.zup.eventsourcing.config.objectToJson


abstract class Event {
    fun retrieveEventType(): EventType = EventType(this.javaClass.canonicalName)
    fun retrieveJsonData(): br.com.zup.eventsourcing.JsonData {
        return br.com.zup.eventsourcing.JsonData(this.objectToJson())
    }
}

class JsonData(val data: String)
class EventType(val value: String)
