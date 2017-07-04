package br.com.zup.eventsourcing.relationaldb.domain

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.Event
import br.com.zup.eventsourcing.core.util.NoArgsConstructor
import java.time.LocalDateTime

@NoArgsConstructor
class CreateEvent(val aggregateId: AggregateId) : Event()

@NoArgsConstructor
class ModifyEvent(val status: String, val date: LocalDateTime) : Event()
