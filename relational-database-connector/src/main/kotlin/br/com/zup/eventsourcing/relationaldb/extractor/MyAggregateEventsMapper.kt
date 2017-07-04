package br.com.zup.eventsourcing.relationaldb.extractor

import br.com.zup.eventsourcing.relationaldb.JdbcEventRepository
import br.com.zup.eventsourcing.relationaldb.domain.AggregateEvent
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet


class MyAggregateEventsMapper : RowMapper<AggregateEvent> {
    override fun mapRow(rs: ResultSet, rowNum: Int): AggregateEvent {
        val eventId = rs.getString(JdbcEventRepository.ID_COLUMN)
        val aggregateId = rs.getString(JdbcEventRepository.AGGREGATE_ID_COLUMN)
        val version = Integer(rs.getInt(JdbcEventRepository.VERSION_COLUMN))
        val eventType = rs.getString(JdbcEventRepository.EVENT_TYPE_COLUMN)
        val event = rs.getString(JdbcEventRepository.EVENT_COLUMN)
        val metaData = rs.getString(JdbcEventRepository.META_DATA_COLUMN)
        val aggregateType = rs.getString(JdbcEventRepository.AGGREGATE_TYPE)
        val createdAt = rs.getTimestamp(JdbcEventRepository.CREATED_AT_COLUMN).toLocalDateTime()
        return AggregateEvent(eventId, aggregateId, version, eventType, event, metaData, aggregateType, createdAt)
    }
}