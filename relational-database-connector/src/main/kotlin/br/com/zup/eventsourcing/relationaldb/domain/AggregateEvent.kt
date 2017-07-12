package br.com.zup.eventsourcing.relationaldb.domain

import java.time.LocalDateTime


class AggregateEvent(val eventId: String, val aggregateId: String, val version: Integer, val eventType: String, val
event: String, val metaData: String, val aggregateType: String, val createdAt: LocalDateTime)