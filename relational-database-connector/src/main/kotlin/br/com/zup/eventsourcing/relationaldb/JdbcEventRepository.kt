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
import java.lang.reflect.ParameterizedType
import java.sql.Types


abstract class JdbcEventRepository<T : AggregateRoot> @Autowired constructor(val jdbcTemplate: JdbcTemplate) :
        Repository<T> {

    init {
        val databaseMetaData = jdbcTemplate.dataSource.connection.metaData

        val tables = databaseMetaData.getTables(null, null, getGenericName().toLowerCase(), arrayOf("TABLE"))
        var tableAlreadyExists = false
        while (tables.next()) {
            if (tables.getString("TABLE_NAME").equals(getGenericName().toLowerCase())) tableAlreadyExists = true
        }
        if (!tableAlreadyExists) {
            val typeInfo = databaseMetaData.typeInfo
            var varcharType: String? = null
            var integerType: String? = null
            var clobType: String? = null
            var timestampType: String? = null

            while (typeInfo.next()) {

                when (typeInfo.getInt("DATA_TYPE")) {
                    Types.VARCHAR -> varcharType = typeInfo.getString("TYPE_NAME")
                    Types.CLOB -> clobType = typeInfo.getString("TYPE_NAME")
                    Types.INTEGER -> integerType = typeInfo.getString("TYPE_NAME")
                    Types.TIMESTAMP -> timestampType = typeInfo.getString("TYPE_NAME")
                }
            }

            if (null == clobType) clobType = varcharType

            val tableName = getGenericName()
            val sql = "CREATE TABLE $tableName (" +
                    "ID ${varcharType!!}(36) NOT NULL, " +
                    "AGGREGATE_ID ${varcharType!!}(36) NOT NULL, " +
                    "VERSION ${integerType!!} NOT NULL, " +
                    "EVENT_TYPE ${clobType!!} NOT NULL, " +
                    "EVENT ${clobType!!} NOT NULL, " +
                    "META_DATA ${clobType!!}, " +
                    "AGGREGATE_TYPE ${clobType!!} NOT NULL, " +
                    "CREATED_AT ${timestampType!!} NOT NULL, " +
                    "PRIMARY KEY (AGGREGATE_ID,VERSION))"
            jdbcTemplate.update(sql)
        }

    }

    private fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }

    private fun getGenericCanonicalName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).canonicalName
    }

    private val LOG = LogManager.getLogger(this.javaClass)

    companion object {
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
            val sql = "insert into ${getGenericName()} ($ID_COLUMN, $AGGREGATE_ID_COLUMN, $VERSION_COLUMN, " +
                    "$EVENT_TYPE_COLUMN, $EVENT_COLUMN, $META_DATA_COLUMN, $AGGREGATE_TYPE, $CREATED_AT_COLUMN) " +
                    "values (?, ?, ?, ?, ?, ?, ?, now())"
            jdbcTemplate.update(sql, it.id.value.toString(), aggregate.id.value, version, it
                    .retrieveEventType().value, it.retrieveJsonData().data, metaData.objectToJson(), this
                    .getGenericCanonicalName())
            version += 1
        }
    }

    override fun get(id: AggregateId): T {
        val sql = "select * from ${getGenericName().toLowerCase()} where $AGGREGATE_ID_COLUMN = ? order by $VERSION_COLUMN"
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