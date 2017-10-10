package br.com.zup.eventsourcing.relationaldb

import br.com.zup.eventsourcing.core.*
import br.com.zup.eventsourcing.core.config.jsonToObject
import br.com.zup.eventsourcing.core.config.objectToJson
import br.com.zup.eventsourcing.relationaldb.extractor.MyAggregateEventsMapper
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate


abstract class JdbcEventRepository<T : AggregateRoot> @Autowired constructor(val jdbcTemplate: JdbcTemplate) : Repository<T>() {

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
        val SQL_LOCK_ENABLE = "insert into $TABLE_NAME  ($ID_COLUMN, " +
                                                        "$AGGREGATE_ID_COLUMN, " +
                                                        "$VERSION_COLUMN, " +
                                                        "$EVENT_TYPE_COLUMN, " +
                                                        "$EVENT_COLUMN, " +
                                                        "$META_DATA_COLUMN, " +
                                                        "$AGGREGATE_TYPE, " +
                                                        "$CREATED_AT_COLUMN) " +
                                    "values (?, ?, ?, ?, ?, ?, ?, now())"

        val SQL_LOCK_DISABLE = "insert into $TABLE_NAME ($ID_COLUMN, " +
                                                        "$AGGREGATE_ID_COLUMN, " +
                                                        "$VERSION_COLUMN, " +
                                                        "$EVENT_TYPE_COLUMN, " +
                                                        "$EVENT_COLUMN, " +
                                                        "$META_DATA_COLUMN, " +
                                                        "$AGGREGATE_TYPE, " +
                                                        "$CREATED_AT_COLUMN) " +
                                    "values (?, ?, (select count($ID_COLUMN) from $TABLE_NAME where $AGGREGATE_ID_COLUMN = ?), ?, ?, ?, ?, now())"
    }

    override fun save(aggregateRoot: T, lock: Repository.OptimisticLock) {
        saveAggregate(aggregateRoot, null, lock)
    }

    override fun save(aggregateRoot: T, metaData: MetaData, lock: OptimisticLock) {
        saveAggregate(aggregateRoot, metaData, lock)
    }

    private fun saveAggregate(aggregateRoot: T, metaData: MetaData? = null, lock: OptimisticLock) {
        if (lock == OptimisticLock.ENABLED) {
            saveOptimisticLockEnabled(aggregateRoot, metaData)
        } else {
            saveOptimisticLockDisable(aggregateRoot, metaData)
        }
    }

    private fun saveOptimisticLockEnabled(aggregateRoot: T, metaData: MetaData?) {
        var version = aggregateRoot.version.value + 1
        aggregateRoot.events.forEach {
            jdbcTemplate.update(SQL_LOCK_ENABLE, it.id.value, aggregateRoot.id.value, version, it
                    .retrieveEventType().value, it.retrieveJsonData().data, metaData.objectToJson(), this
                    .getGenericCanonicalName())
            version += 1
        }
    }

    private fun saveOptimisticLockDisable(aggregateRoot: T, metaData: MetaData?) {
        aggregateRoot.events.forEach {
            jdbcTemplate.update(SQL_LOCK_DISABLE, it.id.value, aggregateRoot.id.value, aggregateRoot.id.value, it.retrieveEventType
            ().value,
                    it.retrieveJsonData().data, metaData.objectToJson(), this.getGenericCanonicalName())
        }
    }


    override fun get(aggregateId: AggregateId): T {
        val aggregateClass: Class<*> = Class.forName(getGenericCanonicalName())
        val sql = "select * from $TABLE_NAME where $AGGREGATE_ID_COLUMN = ? and $AGGREGATE_TYPE = ? order by " +
                "$VERSION_COLUMN"
        val events = jdbcTemplate.query(sql, MyAggregateEventsMapper(), aggregateId.value, aggregateClass.canonicalName)
        if (events.isEmpty()) throw NotFoundException()
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

    override fun getLastMetaData(aggregateId: AggregateId): MetaData {
        val aggregateClass: Class<*> = Class.forName(getGenericCanonicalName())
        val sql = "select * from $TABLE_NAME where $AGGREGATE_ID_COLUMN = ? and $AGGREGATE_TYPE = ? order by " +
                "$VERSION_COLUMN"
        val events = jdbcTemplate.query(sql, MyAggregateEventsMapper(), aggregateId.value, aggregateClass.canonicalName)
        if (events.isEmpty()) throw NotFoundException()
        val lastEvent = events.last()
        return lastEvent.metaData.jsonToObject(MetaData::class.java)
    }

}