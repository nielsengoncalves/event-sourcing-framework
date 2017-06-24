package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.AggregateId
import br.com.zup.eventsourcing.Event
import br.com.zup.eventsourcing.util.NoArgsConstructor

/**
 * Created by zacacj on 6/20/2017.
 */
@NoArgsConstructor
class CreateEvent(val aggregateId: AggregateId) : Event()

@NoArgsConstructor
class ModifyEvent(val status: String) : Event()
