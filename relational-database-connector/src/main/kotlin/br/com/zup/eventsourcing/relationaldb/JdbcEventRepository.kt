package br.com.zup.eventsourcing.relationaldb

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.AggregateRoot
import br.com.zup.eventsourcing.core.AggregateVersion
import br.com.zup.eventsourcing.core.Event
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.core.Repository
import br.com.zup.eventsourcing.core.config.jsonToObject
import br.com.zup.eventsourcing.core.config.objectToJson
import br.com.zup.eventsourcing.relationaldb.extractor.MyAggregateEventsMapper
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate


abstract class JdbcEventRepository<T : AggregateRoot> @Autowired constructor(val jdbcTemplate: JdbcTemplate) :
        Repository<T>() {

    private val LOG = LogManager.getLogger(this.javaClass)

    companion object {
        val TABLE_NAME: String = "ES_EVENT"
        val ID_COLUMN: String = "ID"
        val AGGREGATE_ID_COLUMN = "AGGREGATE_ID"
        val VERSION_COLUMN = "VERSION"
        val EVENT_TYPE_COLUMN = "EVENT_TYPE"
        val EVENT_COLUMN = "EVENT"
        val META_DATA_COLUMN = "META_DATA"
        val AGGREGATE_TYPE = "AGGREGATE_TYPE"
        val CREATED_AT_COLUMN = "CREATED_AT"
    }

    override fun save(aggregate: T) {
        saveAggregate(aggregate, null)
    }

    override fun save(aggregate: T, metaData: MetaData) {
        saveAggregate(aggregate, metaData)
    }

    fun saveAggregate(aggregate: T, metaData: MetaData? = null) {
        var version = aggregate.version.value + 1
        aggregate.events.forEach {
            val sql = "insert into $TABLE_NAME ($ID_COLUMN, $AGGREGATE_ID_COLUMN, $VERSION_COLUMN, " +
                    "$EVENT_TYPE_COLUMN, $EVENT_COLUMN, $META_DATA_COLUMN, $AGGREGATE_TYPE, $CREATED_AT_COLUMN) " +
                    "values (?, ?, ?, ?, ?, ?, ?, now())"
            jdbcTemplate.update(sql, it.id.value, aggregate.id.value, version, it
                    .retrieveEventType().value, it.retrieveJsonData().data, metaData.objectToJson(), this
                    .getGenericCanonicalName())
            version += 1
        }
    }

    override fun get(id: AggregateId): T {
        val sql = "select * from $TABLE_NAME where $AGGREGATE_ID_COLUMN = ? order by $VERSION_COLUMN"
        val events = jdbcTemplate.query(sql, MyAggregateEventsMapper(), id.value)
        val aggregateClass: Class<*> = Class.forName(getGenericCanonicalName())
        val aggregate = aggregateClass.newInstance()
        val list = mutableListOf<Event>()
        var version = 0
        events.forEach {
            list.add(it.event.jsonToObject(Class.forName(it.eventType)) as Event)
            version = it.version.toInt()
        }
        (aggregate as T)
        aggregate.load(list, AggregateVersion(version))
        return aggregate
    }

}