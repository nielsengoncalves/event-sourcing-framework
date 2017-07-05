package br.com.zup.eventsourcing.core.domain

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.Event
import java.time.LocalDateTime

class CreateEvent(val aggregateId: AggregateId) : Event()

class ModifyEvent(val status: String, val date: LocalDateTime) : Event()
